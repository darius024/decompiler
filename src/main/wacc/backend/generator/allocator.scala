package wacc.backend.generator

import scala.collection.mutable

import wacc.backend.ir.*
import allocator.*
import flags.*
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

    // TODO: pattern match on the rest of the instructions

    // TODO: add the pushes and pops within the IR

    val newInstructions = instructions.map {
        // track function entry and exit points for register saving
        case instr @ Push(RBP(_)) =>
            regMachine.updatePushPoint(instr)
            instr
        case instr @ Pop(RBP(_)) =>
            regMachine.updatePopPoint(instr)
            instr

        // handle comparison operations
        case Cmp(dest: TempReg, src: RegImm) =>
            regMachine.nextRegister(dest)
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Cmp(dest, srcReg)
        case instr @ SetComp(dest: TempReg, compFlag: CompFlag) =>
            regMachine.nextRegister(dest)
            instr
            
        // handle arithmetic operations
        case Add(dest: TempReg, src: RegImm) =>
            regMachine.nextRegister(dest)
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Add(dest, srcReg)
        case Sub(dest: TempReg, src: RegImm) =>
            regMachine.nextRegister(dest)
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Sub(dest, srcReg)
        case Mul(dest: TempReg, src1: RegImm, src2: RegImm) =>
            regMachine.nextRegister(dest)
            val srcReg1 = src1 match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            val srcReg2 = src2 match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Mul(dest, srcReg1, srcReg2)
            
        // handle logical operations
        case And(dest: TempReg, src: RegImm) =>
            regMachine.nextRegister(dest)
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            And(dest, srcReg)
        case Or (dest: TempReg, src: RegImm) =>
            regMachine.nextRegister(dest)
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Or(dest, srcReg)
        case Test(dest: TempReg, src: RegImm) =>
            regMachine.nextRegister(dest)
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Test(dest, srcReg)
            
        // handle data movement operations
        case Mov(dest: RegMem, src: RegImmMem) =>
            val destReg = dest match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Mov(destReg, srcReg)
        case Lea(dest: TempReg, MemAccess(src, offset)) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Lea(destReg, MemAccess(srcReg, offset))

        // pass through other instructions unchanged
        case instr => instr
    }

    // TODO: this is inefficient, do not traverse again
    codeGen.instructions = List.newBuilder
    newInstructions.foreach { instr =>
        codeGen.addInstr(instr)
    }

    codeGen
}

/**
 * Contains helper classes and constants for register allocation.
 */
object allocator {
    // TODO: use multiple registers
    private val paramRegisters = List(RDI(), RSI(), RDX(), RCX(), R8(), R9())
    private val callerSaved = List(R10(), R11())
    private val calleeSaved: List[Register] = List(RBX(), R12(), R13(), R14(), R15())
    private val allRegisters = paramRegisters ++ callerSaved ++ calleeSaved
    
    /**
     * Manages the allocation of physical registers to temporary registers.
     */
    class RegisterMachine {
        // queue of available registers (initially all callee-saved registers)
        var availableRegisters: mutable.Queue[Register] = mutable.Queue.from(calleeSaved)
        var pushPoint: Option[Instruction] = None
        var toSave: List[Register] = List.empty

        /**
         * Allocates a physical register for a temporary register.
         */
        def nextRegister(temp: TempReg)
                        (using regs: mutable.Map[TempReg, Register]): Register = {
            if (regs.contains(temp)) {
                regs(temp)
            } else {
                if (availableRegisters.isEmpty) {
                    handleOutOfRegisters
                } else {
                    val reg = availableRegisters.dequeue()
                    regs += temp -> reg
                    toSave = reg :: toSave
                    reg
                }
            }
        }

        def updatePushPoint(instr: Instruction): Unit = {
            pushPoint = Some(instr)
        }

        def updatePopPoint(instr: Instruction): Unit = {
            pushPoint = None

            // save registers to stack at function entry
            toSave.foreach(Mov(MemAccess(RSP(), memoryOffsets.NO_OFFSET), _))
            toSave = List.empty
            availableRegisters = mutable.Queue.from(calleeSaved)
        }

        /**
         * Handles the case when we run out of registers.
         * Currently returns a fixed register, but should implement spilling.
         */
        def handleOutOfRegisters: Register = {
            // TODO: Implement proper register spilling
            RDI()
        }
    }
}
