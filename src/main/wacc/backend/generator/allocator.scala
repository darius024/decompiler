package wacc.backend.generator

import scala.collection.mutable

import wacc.backend.ir.*
import allocator.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*

/**
 * Allocates physical registers to the temporary registers used in the IR.
 * This is the second pass of code generation that transforms abstract registers
 * into concrete machine registers.
 */
def allocate(codeGen: CodeGenerator): CodeGenerator = {
    given registers: mutable.Map[TempReg, Register] = mutable.Map.empty

    val instructions = codeGen.ir
    val regMachine: RegisterMachine = RegisterMachine()
    val newInstructions: mutable.ListBuffer[Instruction] = mutable.ListBuffer.empty[Instruction]
    var scopeInstructions: mutable.ListBuffer[Instruction] = mutable.ListBuffer.empty[Instruction]
    var framePointer: Int = 0

    instructions.foreach {
        // track function entry and exit points for register saving
        case Push(RBP(_)) =>
            // do not add the instructions in the buffer yet
            // it will be added after the registers have been saved

        case Pop(RBP(_)) =>
            // save all callee registers on the stack at the beginning of function
            val calleeRegistersToSave = regMachine.getUsedRegisters
            val stackSize = calleeRegistersToSave.length * RegSize.QUAD_WORD.size

            // set up the frame pointer
            Mov(RBP(), RSP()) +=: scopeInstructions

            // prepend to the buffer in reverse order
            for ((reg, index) <- calleeRegistersToSave.reverseIterator.zipWithIndex) {
                val ind = calleeRegistersToSave.length - 1 - index
                Mov(MemAccess(RSP(), ind * RegSize.QUAD_WORD.size), reg) +=: scopeInstructions
            }
            // set the frame and stack pointer
            if (stackSize == 0) {
                Sub(RSP(), Imm(stackSize)) +=: scopeInstructions
            }
            Push(RBP()) +=: scopeInstructions

            // restore all callee registers on the stack at the beginning of function
            for ((reg, index) <- calleeRegistersToSave.zipWithIndex) {
                scopeInstructions += Mov(MemAccess(RSP(), index * RegSize.QUAD_WORD.size), reg)
            }
            // set the frame and stack pointer
            if (stackSize == 0) {
                scopeInstructions += Add(RSP(), Imm(stackSize))
            } else {
                scopeInstructions += Mov(RSP(), RBP())
            }
            scopeInstructions += Pop(RBP())

        case Ret =>
            // finish the current scope and append it to the global scope
            scopeInstructions += Ret
            newInstructions ++= scopeInstructions
            // start a new empty scope
            scopeInstructions =  mutable.ListBuffer.empty[Instruction]
            framePointer = 0

        // handle comparison operations
        case Cmp(dest: Register, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src)
            scopeInstructions += Cmp(destReg, srcReg)
        case instr @ SetComp(dest: Register, compFlag: CompFlag) =>
            val destReg = regMachine.nextRegister(dest)
            scopeInstructions += SetComp(destReg, compFlag)
            
        // handle arithmetic operations
        case Add(dest: Register, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src)
            scopeInstructions += Add(destReg, srcReg)
        case Sub(dest: Register, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src)
            scopeInstructions += Sub(destReg, srcReg)
        case Mul(dest: Register, src1: RegImm, src2: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg1 = regMachine.nextRegisterImm(src1)
            val srcReg2 = regMachine.nextRegisterImm(src2)
            scopeInstructions += Mul(destReg, srcReg1, srcReg2)
        case Mod(src: Register) =>
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += Mod(srcReg)
        case Div(src: Register) =>
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += Div(srcReg)
            
        // handle logical operations
        case And(dest: TempReg, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src)
            scopeInstructions += And(destReg, srcReg)
        case Or (dest: TempReg, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src)
            scopeInstructions += Or(destReg, srcReg)
        case Test(dest: TempReg, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src)
            scopeInstructions += Test(destReg, srcReg)
            
        // handle data movement operations
        case Mov(dest: RegMem, src: RegImmMem) =>
            val destReg = dest match {
                case reg: Register          => regMachine.nextRegister(reg)
                case MemAccess(reg, offset) => MemAccess(regMachine.nextRegister(reg), offset)
                case _                      => dest
            }
            val srcReg = src match {
                case regImm: RegImm => regMachine.nextRegisterImm(regImm)
                case memAcc         => dest
            }
            scopeInstructions += Mov(destReg, srcReg)
        case Lea(dest: TempReg, MemAccess(src, offset)) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += Lea(destReg, MemAccess(srcReg, offset))
        case CMov(dest: Register, src: Register, cond: CompFlag) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += CMov(destReg, srcReg, cond)

        // pass through other instructions unchanged
        case instr =>
            scopeInstructions += instr
    }

    codeGen.instructions = newInstructions
    codeGen
}

/**
 * Contains helper classes and constants for register allocation.
 */
object allocator {
    // TODO: use multiple registers
    private val paramRegisters = List(RDX(), RCX(), RSI(), RDI(), R8(), R9())
    private val calleeSaved: List[Register] = List(RBX(), R12(), R13(), R14(), R15())
    
    /**
     * Manages the allocation of physical registers to temporary registers.
     */
    class RegisterMachine {
        // queue of available registers (initially all callee-saved registers)
        var availableRegisters: mutable.Queue[Register] = mutable.Queue.from(calleeSaved)
        var toSave: List[Register] = List.empty

        /**
         * Allocates a physical register for a temporary register.
         */
        def nextRegister(reg: Register)
                        (using regs: mutable.Map[TempReg, Register]): Register = reg match {
            case temp: TempReg => if (regs.contains(temp)) {
                    regs(temp)
                } else if (availableRegisters.isEmpty) {
                    handleOutOfRegisters
                } else {
                    val reg = availableRegisters.dequeue()
                    regs += temp -> reg
                    toSave = reg :: toSave
                    reg
                }
            case _             => reg
        }

        def nextRegisterImm(regImm: RegImm)
                           (using regs: mutable.Map[TempReg, Register]): RegImm = regImm match {
            case reg: Register => nextRegister(reg)
            case _             => regImm
        }

        def getUsedRegisters: List[Register] = toSave

        /**
         * Handles the case when we run out of registers.
         * Currently returns a fixed register, but should implement spilling.
         */
        def handleOutOfRegisters: Register = {
            // TODO: implement proper register spilling
            paramRegisters(0)
        }
    }
}
