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
  * Allocates physical registers and stack memory to the temporary registers used in the IR.
  * This is the second pass of code generation that transforms abstract temporary registers
  * into concrete machine registers or memory accesses.
  */
def allocate(codeGen: CodeGenerator, optimise: Boolean): CodeGenerator = {
    // mapped temporary registers to machine registers
    given registers: mutable.Map[TempReg, RegMem] = mutable.Map.empty
    // local buffer of instructions
    given scopeInstructions: mutable.ListBuffer[Instruction] = mutable.ListBuffer.empty[Instruction]
    // mapped variables to registers or memory
    given temporaries: mutable.Map[String, RegMem] = codeGen.varRegs
    given liveness: Map[RegMem, (Int, Int)] = codeGen.varLive.toMap
    // register machine
    given regMachine: RegisterMachine = new RegisterMachine()

    // global buffer of instructions
    val newInstructions: mutable.Builder[Instruction, List[Instruction]] = List.newBuilder
    var withinFunction = false

    // second pass through the IR instructions
    codeGen.ir.foreach {
        case label @ Label(name) =>
            scopeInstructions += label
            // check for entering a new scope
            if (name == "main" || name.startsWith("wacc_")) {
                if (name == "main") { regMachine.inMain = true }

                // start a new function scope
                withinFunction = false
                regMachine.initializeNewScope
                regMachine.currentStackSize = codeGen.numRegisters(label)
            }

        // track function entry and exit points for register saving
        case Push(RBP(_)) =>
            // do not add the instructions in the buffer yet
            // the push will be added after the registers have been saved

            // append the local buffer to the instructions
            newInstructions ++= scopeInstructions
            // start a new empty scope
            scopeInstructions.clear()

            // initialize the RBP pointer
            regMachine.initializeNewScope
        
        case Push(src) =>
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += Push(changeRegisterSize(srcReg, RegSize.QUAD_WORD))
        
        case Pop(RBP(_)) =>
            // save all callee registers on the stack at the beginning of function
            val calleeRegistersToSave = (RBX() :: (calleeSaved ::: paramRegisters)).take(regMachine.currentStackSize)
            regMachine.stackSize = calleeRegistersToSave.length * RegSize.QUAD_WORD.size

            // set up the frame pointer
            if (!withinFunction) {
                // set the frame and stack pointer
                if (regMachine.rbpSize != 0) {
                    Sub(RSP(), Imm(regMachine.rbpSize)) +=: scopeInstructions
                }
                Mov(FRAME_REG(), RSP()) +=: scopeInstructions

                // prepend to the buffer in reverse order
                for ((reg, index) <- calleeRegistersToSave.reverseIterator.zipWithIndex) {
                    val ind = calleeRegistersToSave.length - 1 - index
                    Mov(MemAccess(RSP(), ind * RegSize.QUAD_WORD.size), changeRegisterSize(reg, RegSize.QUAD_WORD)) +=: scopeInstructions
                }

                // set the frame and stack pointer
                if (regMachine.stackSize != 0) {
                    Sub(RSP(), Imm(regMachine.stackSize)) +=: scopeInstructions
                }
                Push(FRAME_REG()) +=: scopeInstructions

                withinFunction = true
            }

            if (regMachine.rbpSize != 0) {
                scopeInstructions += Add(RSP(), Imm(regMachine.rbpSize))
            }

            // restore all callee registers on the stack at the beginning of the function
            for ((reg, index) <- calleeRegistersToSave.zipWithIndex) {
                scopeInstructions += Mov(changeRegisterSize(reg, RegSize.QUAD_WORD), MemAccess(RSP(), index * RegSize.QUAD_WORD.size))
            }
            // set the frame and stack pointer
            if (regMachine.stackSize != 0) {
                scopeInstructions += Add(RSP(), Imm(regMachine.stackSize))
            } else {
                scopeInstructions += Mov(RSP(), FRAME_REG())
            }
            scopeInstructions += Pop(FRAME_REG())
        
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
            val srcReg = regMachine.nextRegisterImm(src, AUX_REG())
            scopeInstructions += Add(destReg, srcReg)
        case Sub(dest: Register, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src, AUX_REG())
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
            val srcReg = regMachine.nextRegisterImm(src, AUX_REG())
            scopeInstructions += And(destReg, srcReg)
        case Or (dest: TempReg, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src, AUX_REG())
            scopeInstructions += Or(destReg, srcReg)
        case Test(dest: TempReg, src: RegImm) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegisterImm(src, AUX_REG())
            scopeInstructions += Test(destReg, srcReg)
            
        // handle data movement operations
        case mov @ Mov(dest: RegMem, src: RegImmMem) =>
            val useReg = regMachine.checkVariableLiveness(mov, dest)
            val register = scopeInstructions match {
                case _ :+ Mov(RAX(_), _) => src match {
                    case MemAccess(RAX(_), _, _) => RETURN_REG()
                    case _                       => AUX_REG()
                }
                case _                   => RETURN_REG()
            }
            val destReg: RegMem = dest match {
                case reg: Register                => regMachine.nextRegisterMem(reg, register)
                case MemAccess(reg, offset, size) => MemAccess(regMachine.nextRegister(reg), offset, size)
                case _                            => dest
            }
            val srcReg: RegImmMem = src match {
                case regImm: RegImm                       => regMachine.nextRegisterImm(regImm)
                case MemAccess(RBP(_), offset: Int, size) => MemAccess(FRAME_REG(), offset + regMachine.currentStackSize * RegSize.QUAD_WORD.size, size)
                case MemAccess(reg, offset, size)         => MemAccess(regMachine.nextRegister(reg), offset, size)
                case _                                    => src
            }
            if (!optimise || useReg) {
                scopeInstructions += Mov(destReg, srcReg)
            }

            // optimisation: free the register
            if (optimise) {
                src match {
                    case reg: Register
                        if (liveness.contains(reg) && liveness(reg)._2 == mov.number) => regMachine.freeRegister(reg)
                    case _ =>
                }
            }
                        
        case Lea(dest: TempReg, MemAccess(src, offset, size)) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegister(src)
            scopeInstructions += Lea(destReg, MemAccess(srcReg, offset, size))
        case CMov(dest: Register, src: Register, cond: CompFlag) =>
            val destReg = regMachine.nextRegister(dest)
            val srcReg = regMachine.nextRegister(src, AUX_REG())
            scopeInstructions += CMov(destReg, srcReg, cond)

        case Call(label) =>
            // save all callee-saved registers on stack
            regMachine.saveRegisters
            scopeInstructions += Call(label)
            regMachine.restoreRegisters

        // pass through other instructions without changing them
        case instr =>
            scopeInstructions += instr
    }

    codeGen.instrs = newInstructions.result()
    codeGen
}

/**
  * Contains helper classes and constants for register allocation.
  */
object allocator {
    // parameter registers used for function calls
    val paramRegisters = List(ARG3(), ARG4(), ARG2(), ARG1(), ARG5())
    // callee-saved registers that need to be preserved across function calls
    val calleeSaved: List[Register] = List(R12(), R13(), R14(), R15())
    
    /**
      * Manages the allocation of physical registers to temporary registers.
      */
    class RegisterMachine {
        // queue of available registers (initially all callee-saved registers)
        var availableRegisters: mutable.Queue[Register] = mutable.Queue.from(calleeSaved)
        var usedRegisters: mutable.ListBuffer[Register] = mutable.ListBuffer.empty[Register]
        
        private var usingParameters = false
        var inMain = false

        // parameters that track the dimensions of the stack
        var rbpSize = 0
        var stackSize = 0
        var currentStackSize = 0

        /** Return the register where the variable needs to be stored. */
        def nextRegisterMem(reg: RegMem, regParam: Register = RETURN_REG())
                           (using temporaries: mutable.Map[String, RegMem])
                           (using scopeInstructions: mutable.ListBuffer[Instruction])
                           (using regs: mutable.Map[TempReg, RegMem]): RegMem = reg match {
            case temp: TempReg => {
                val regMem = if (regs.contains(temp)) {
                    regs(temp)
                } else if (temporaries.values.toList.contains(temp)) {
                    // if registers are not available, use the stack
                    val regMem: RegMem = if (availableRegisters.isEmpty) {
                        rbpSize = rbpSize + temp.size.size
                        val mem = MemAccess(FRAME_REG(), -rbpSize, temp.size)
                        mem
                    } else {
                        val reg = availableRegisters.dequeue()

                        // if callee saved registers run out, use parameter registers
                        if (availableRegisters.isEmpty && !usingParameters && inMain) {
                            availableRegisters = mutable.Queue.from(paramRegisters)
                            usingParameters = true
                        }

                        usedRegisters += reg
                        changeRegisterSize(reg, temp.size)
                    }
                    // keep track of the location for future references
                    regs += temp -> regMem
                    regMem
                } else {
                    regParam
                }
                // alter the size of the location if it is required
                regMem match {
                    case reg: Register                        => changeRegisterSize(reg, temp.size)
                    case MemAccess(RBP(_), offset: Int, size) => MemAccess(FRAME_REG(), offset + stackSize, size)
                    case mem                                  => mem
                }
            }
            // adjust the accesses to the parameters stored on the stack
            case MemAccess(RBP(_), offset: Int, size) => MemAccess(FRAME_REG(), offset + stackSize, size)
            case mem                                  => mem      
        }

        /** Return the register where the variable needs to be stored. */
        def nextRegister(reg: Register, regParam: Register = RETURN_REG())
                        (using temporaries: mutable.Map[String, RegMem])
                        (using scopeInstructions: mutable.ListBuffer[Instruction])
                        (using regs: mutable.Map[TempReg, RegMem]): Register = nextRegisterMem(reg, regParam) match {
            case reg: Register => reg
            case mem           =>
                scopeInstructions += Mov(RETURN_REG(mem.size), mem)
                RETURN_REG(mem.size)
        }

        /** Return the register where the variable needs to be stored. */
        def nextRegisterImm(regImm: RegImm, regParam: Register = RETURN_REG())
                           (using temporaries: mutable.Map[String, RegMem])
                           (using scopeInstructions: mutable.ListBuffer[Instruction])
                           (using regs: mutable.Map[TempReg, RegMem]): RegImm = regImm match {
            case reg: Register => nextRegister(reg, regParam)
            case _             => regImm
        }

        /** Reset the register machine for a new scope. */
        def initializeNewScope: Unit = {
            rbpSize = 0
            availableRegisters = mutable.Queue.from(calleeSaved)

            usedRegisters = mutable.ListBuffer.empty[Register]
            usedRegisters += AUX_REG()
        }

        /** Retrieves the used registers of the function scope. */
        def getUsedRegisters: List[Register] = usedRegisters.toList

        /** Saves register on the stack in increasing order. */
        def saveRegisters(using scopeInstructions: mutable.ListBuffer[Instruction]): Unit = {
            for (reg <- usedRegisters) {
                if (paramRegisters.contains(reg)) {
                    scopeInstructions += Push(changeRegisterSize(reg, RegSize.QUAD_WORD))
                }
            }
        }

        /** Restores register on the stack in decreasing order. */
        def restoreRegisters(using scopeInstructions: mutable.ListBuffer[Instruction]): Unit = {
            for (reg <- usedRegisters.reverse) {
                if (paramRegisters.contains(reg)) {
                    scopeInstructions += Pop(changeRegisterSize(reg, RegSize.QUAD_WORD))
                }
            }
        }

        /** Checks if it useful to allocate a register to the variable or not. */
        def checkVariableLiveness(mov: Instruction, srcReg: RegImmMem)
                                 (using liveness: Map[RegMem, (Int, Int)]): Boolean = srcReg match {
            case reg: RegMem => !(liveness.contains(reg) && liveness(reg)._1 == liveness(reg)._2)
            case _ => true
        }

        /** Makes the register available. */
        def freeRegister(reg: Register): Unit =
            if ((calleeSaved ++ paramRegisters).contains(reg)) {
                availableRegisters += reg
                usedRegisters = usedRegisters.filter(_ != reg)
            }
    }
}
