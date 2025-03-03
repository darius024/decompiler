package wacc.backend.generator

import scala.collection.mutable

import wacc.backend.ir.*
import allocator.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*
import utils.*

/**
 * Allocates physical registers to the temporary registers used in the IR.
 * This is the second pass of code generation that transforms abstract registers
 * into concrete machine registers.
 */
def allocate(codeGen: CodeGenerator): CodeGenerator = {
    given registers: mutable.Map[TempReg, RegMem] = mutable.Map.empty

    val instructions = codeGen.ir
    given temporaries: mutable.Map[String, Register] = codeGen.varRegs
    val regMachine: RegisterMachine = new RegisterMachine()
    val newInstructions: mutable.Builder[Instruction, List[Instruction]] = List.newBuilder
    given scopeInstructions: mutable.ListBuffer[Instruction] = mutable.ListBuffer.empty[Instruction]

    instructions.foreach {
        // track function entry and exit points for register saving
        case Push(RBP(_)) =>
            // do not add the instructions in the buffer yet
            // the push will be added after the registers have been saved

            // add the label to the instructions
            newInstructions ++= scopeInstructions
            // start a new empty scope
            scopeInstructions.clear()

            // initialize the RBP pointer
            regMachine.initializeRBP
        
        case Push(src) =>
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += Push(srcReg)

        case Pop(RBP(_)) =>
            // save all callee registers on the stack at the beginning of function
            val calleeRegistersToSave = regMachine.getUsedRegisters
            val stackSize = calleeRegistersToSave.length * RegSize.QUAD_WORD.size

            // set up the frame pointer
            if (regMachine.rbpSize != 0) {
                Sub(RSP(), Imm(regMachine.rbpSize)) +=: scopeInstructions
            }
            Mov(RBP(), RSP()) +=: scopeInstructions

            // prepend to the buffer in reverse order
            for ((reg, index) <- calleeRegistersToSave.reverseIterator.zipWithIndex) {
                val ind = calleeRegistersToSave.length - 1 - index
                Mov(MemAccess(RSP(), ind * RegSize.QUAD_WORD.size), changeRegisterSize(reg, RegSize.QUAD_WORD)) +=: scopeInstructions
            }
            // set the frame and stack pointer
            if (stackSize != 0) {
                Sub(RSP(), Imm(stackSize)) +=: scopeInstructions
            }
            Push(RBP()) +=: scopeInstructions

            // restore all callee registers on the stack at the beginning of function
            for ((reg, index) <- calleeRegistersToSave.zipWithIndex) {
                scopeInstructions += Mov(changeRegisterSize(reg, RegSize.QUAD_WORD), MemAccess(RSP(), index * RegSize.QUAD_WORD.size))
            }
            // set the frame and stack pointer
            if (stackSize != 0) {
                scopeInstructions += Add(RSP(), Imm(stackSize))
            } else {
                scopeInstructions += Mov(RSP(), RBP())
            }
            scopeInstructions += Pop(RBP())
        
        case Pop(src) =>
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += Pop(srcReg)

        case Ret =>
            // finish the current scope and append it to the global scope
            scopeInstructions += Ret
            newInstructions ++= scopeInstructions
            // start a new empty scope
            scopeInstructions.clear()

        // handle comparison operations
        case Cmp(dest: Register, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src)
            scopeInstructions += Cmp(destReg, srcReg)
        case SetComp(dest: Register, compFlag: CompFlag) =>
            val destReg = regMachine.nextRegister(dest)
            scopeInstructions += SetComp(destReg, compFlag)
            
        // handle arithmetic operations
        case Add(dest: Register, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src, RBX())
            scopeInstructions += Add(destReg, srcReg)
        case Sub(dest: Register, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src, RBX())
            scopeInstructions += Sub(destReg, srcReg)
        case Mul(dest: Register, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src)
            scopeInstructions += Mul(destReg, srcReg)
        case Mod(src: Register) =>
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += Mod(srcReg)
        case Div(src: Register) =>
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += Div(srcReg)
            
        // handle logical operations
        case And(dest: TempReg, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src, RBX())
            scopeInstructions += And(destReg, srcReg)
        case Or (dest: TempReg, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src, RBX())
            scopeInstructions += Or(destReg, srcReg)
        case Test(dest: TempReg, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src, RBX())
            scopeInstructions += Test(destReg, srcReg)
            
        // handle data movement operations
        case Mov(dest: RegMem, src: RegImmMem) =>
            val register = scopeInstructions match {
                case _ :+ Mov(RAX(_), _) => RBX()
                case _                   => RAX()
            }
            val destReg = dest match {
                case reg: Register          => regMachine.nextRegister(reg, register)
                case MemAccess(reg, offset) => MemAccess(regMachine.nextRegister(reg,register), offset)
                case _                      => dest
            }
            val srcReg = src match {
                case regImm: RegImm => regMachine.nextRegisterImm(regImm)
                case MemAccess(reg, offset) => MemAccess(regMachine.nextRegister(reg), offset)
                case _                      => src

            }
            scopeInstructions += Mov(destReg, srcReg)
            
        case Lea(dest: TempReg, MemAccess(src, offset)) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += Lea(destReg, MemAccess(srcReg, offset))
        case CMov(dest: Register, src: Register, cond: CompFlag) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegister(src, RBX())
            scopeInstructions += CMov(destReg, srcReg, cond)

        case Call(label) =>
            // save all callee-saved registers on stack
            regMachine.saveRegisters
            scopeInstructions += Call(label)
            regMachine.restoreRegisters

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
    // parameter registers used for function calls
    val paramRegisters = Set(RDX(), RCX(), RSI(), RDI(), R8(), R9())
    // callee-saved registers that need to be preserved across function calls
    private val calleeSaved: List[Register] = List(R12(), R13(), R14(), R15(), RDX(), RCX(), RSI(), RDI(), R8(), R9())
    
    /**
     * Manages the allocation of physical registers to temporary registers.
     */
    class RegisterMachine {
        // queue of available registers (initially all callee-saved registers)
        var availableRegisters: mutable.Queue[Register] = mutable.Queue.from(calleeSaved)
        var usedRegisters: mutable.ListBuffer[Register] = mutable.ListBuffer.empty[Register]
        var rbpSize = 0
        private var usingParameters = false

        def nextRegisterMem(reg: RegMem, regParam: Register = RAX())
                           (using temporaries: mutable.Map[String, Register])
                           (using scopeInstructions: mutable.ListBuffer[Instruction])
                           (using regs: mutable.Map[TempReg, RegMem]): RegMem = reg match {
            case temp: TempReg => {
                val register = if (regs.contains(temp)) {
                    regs(temp)
                } else if (temporaries.values.toList.contains(temp)) {
                    // if registers are not available, use the stack
                    val regMem: RegMem = if (availableRegisters.isEmpty) {
                        rbpSize = rbpSize + temp.size.size
                        // TODO: implement proper RBP management
                        val mem = RBX(temp.size) // MemAccess(RBP(), -rbpSize)
                        regs += temp -> mem
                        mem
                    } else {
                        val reg = availableRegisters.dequeue()
                        reg.size = temp.size

                        // if callee saved registers run out, use parameter registers
                        if (availableRegisters.isEmpty && !usingParameters) {
                            availableRegisters = mutable.Queue.from(paramRegisters)
                            usingParameters = true
                        }

                        regs += temp -> changeRegisterSize(reg, temp.size)
                        usedRegisters += reg
                        reg
                    }
                    regMem
                } else {
                    if (regs.contains(temp)) {
                        regs(temp)
                    } else {
                        regParam
                    }
                }
                register match {
                    case reg: Register => changeRegisterSize(reg, temp.size)
                    case mem           => mem
                }
            }
            case _             => reg
        }

        def nextRegister(reg: Register, regParam: Register = RAX())
                        (using temporaries: mutable.Map[String, Register])
                        (using scopeInstructions: mutable.ListBuffer[Instruction])
                        (using regs: mutable.Map[TempReg, RegMem]): Register = nextRegisterMem(reg, regParam) match {
            case reg: Register => reg
            // TODO: handle memory accesses
            case mem           => RAX()
        }

        def nextRegisterImm(regImm: RegImm, regParam: Register = RAX())
                           (using temporaries: mutable.Map[String, Register])
                           (using scopeInstructions: mutable.ListBuffer[Instruction])
                           (using regs: mutable.Map[TempReg, RegMem]): RegImm = regImm match {
            case reg: Register => nextRegister(reg, regParam)
            case _             => regImm
        }

        def initializeRBP: Unit = {
            rbpSize = 0
            usedRegisters = mutable.ListBuffer.empty[Register]
            usedRegisters += RBX()
        }

        def getUsedRegisters: List[Register] = usedRegisters.toList

        def saveRegisters(using scopeInstructions: mutable.ListBuffer[Instruction]): Unit = {
            for (reg <- usedRegisters) {
                if (paramRegisters.contains(reg)) {
                    scopeInstructions += Push(changeRegisterSize(reg, RegSize.QUAD_WORD))
                }
            }
        }

        def restoreRegisters(using scopeInstructions: mutable.ListBuffer[Instruction]): Unit = {
            for (reg <- usedRegisters.reverse) {
                if (paramRegisters.contains(reg)) {
                    scopeInstructions += Pop(changeRegisterSize(reg, RegSize.QUAD_WORD))
                }
            }
        }
    }
}
