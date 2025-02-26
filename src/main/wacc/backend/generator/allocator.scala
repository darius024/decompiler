package wacc.backend.generator

import scala.collection.mutable

import wacc.backend.ir.*
import allocator.*
import flags.*
import instructions.*
import memory.*
import registers.*

def allocate(codeGen: CodeGenerator): CodeGenerator = {
    given registers: mutable.Map[TempReg, Register] = mutable.Map.empty

    val instructions = codeGen.ir    
    val regMachine: RegisterMachine = RegisterMachine()

    // TODO: pattern match on the rest of the instructions

    // TODO: add the pushes and pops within the IR

    val newInstructions = instructions.map {
        case instr @ Push(RBP(_)) =>
            regMachine.updatePushPoint(instr)
            instr
        case instr @ Pop(RBP(_)) =>
            regMachine.updatePopPoint(instr)
            instr

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
        case Neg(dest: TempReg, src: RegImm) =>
            regMachine.nextRegister(dest)
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Neg(dest, srcReg)
        case Not(dest: TempReg, src: RegImm) =>
            regMachine.nextRegister(dest)
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Not(dest, srcReg)
        case Test(dest: TempReg, src: RegImm) =>
            regMachine.nextRegister(dest)
            val srcReg = src match {
                case reg: TempReg => regMachine.nextRegister(reg)
                case reg          => reg
            }
            Test(dest, srcReg)
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

        case instr => instr
    }

    // TODO: this is inefficient, do not traverse again
    codeGen.instructions = List.newBuilder
    newInstructions.foreach { instr =>
        codeGen.addInstr(instr)
    }

    codeGen
}

object allocator {
    // TODO: use multiple registers
    private val paramRegisters = List(RDI(), RSI(), RDX(), RCX(), R8(), R9())
    private val callerSaved = List(R10(), R11())
    private val calleeSaved: List[Register] = List(RBX(), R12(), R13(), R14(), R15())
    private val allRegisters = paramRegisters ++ callerSaved ++ calleeSaved
    
    class RegisterMachine {
        var availableRegisters: mutable.Queue[Register] = mutable.Queue.from(calleeSaved)
        var pushPoint: Option[Instruction] = None
        var toSave: List[Register] = List.empty

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

        def updatePushPoint(instr: Instruction) = {
            pushPoint = Some(instr)
        }

        def updatePopPoint(instr: Instruction) = {
            pushPoint = None

            toSave.foreach(Mov(MemAccess(RSP(), memoryOffsets.NO_OFFSET), _))
            toSave = List.empty
            availableRegisters = mutable.Queue.from(calleeSaved)
        }

        def handleOutOfRegisters: Register = {
            // TODO: correct this
            RDI()
        }
    }
}
