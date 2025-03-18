package wacc.extension.decompiler

import scala.collection.mutable

import wacc.backend.*
import generator.utils.*
import ir.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*

import controlGraph.*
import intermediate.*

private final val parameters: List[Register] = List(ARG1(), ARG2(), ARG3(), ARG4(), ARG5(), ARG6())

/** Stores information about patterns within the assembly code.
  * 
  * Emulates registers, stack, and the memory space.
  */
class Disassembler(val label: Label,
                   blocks: Map[Label, Block],
                   directives: Set[StrLabel],
                   stack: mutable.Stack[String]) {
    // map registers to the variables they store
    private val registers = mutable.Map.empty[Register, String]
    // map memory accesses to the variables they store
    private val memory = mutable.Map.empty[MemoryAccess, String]
    // provide a uniform naming convention
    val namer = new Namer

    var currentLabel = label
    var numParameters = 0
    var rdi = 0

    // array extraction
    var arrayElem = ""
    var arrayInd: ExprVar = 0
    val arrays = mutable. Set.empty[RegMem]

    // structure pattern: arrays and pairs
    var structPattern = false
    var arrayPattern = false
    var structPointer = false
    // register that holds the pointer of access to the structure
    var initialStructReg: RegMem = R11()
    var structReg: RegMem = R11()
    // information about the structure size and its element sizes
    var structElemSize = 0
    var structSize = 0
    // construct the structure sequeunce to be able to print it
    private var structLiteral = mutable.ListBuffer.empty[String | Int]

    // record of all comparisons and their jump points
    private val cmps = mutable.Map.empty[Label, Comp]
    // information about the last operands that set the flags
    private var lastFlagSet: Option[(Register, RegImm)] = None
    // condition of `if`s and `while`s
    var condition: Option[Expr] = None

    // flag the beginning of a possible structure pattern
    def startStructPattern: Unit = {
        structPattern = true
        structPointer = true
        arrayPattern = false
        structElemSize = 0
        structSize = rdi
        structLiteral = mutable.ListBuffer.empty[String | Int]
    }

    // add an element to the structure buffer
    def addStructElement(variable: String | Int, size: Int): Unit = {
        structLiteral += variable
        structElemSize = size
        structSize -= size
    }

    // retrieve the structure constructed
    def getStructure: Expr = {
        structPattern = false
        arrays += structReg
        if (arrayPattern) {
            ArrayLit(structLiteral.toList, structElemSize)
        } else {
            NewPair(structLiteral(0), structLiteral(1))
        }
    }

    // retrieve a string literal from the data segment
    def getDirString(label: Label): StrLiteral = {
        // TODO: raise an error here
        StrLiteral(directives.find(_.label == label).map(_.name).getOrElse(""))
    }

    // record the last operands that set the flags
    def setFlag(reg: Register, regImm: RegImm): Unit = {
        val register = changeRegisterSize(reg, RegSize.QUAD_WORD)
        lastFlagSet = Some((register, regImm))
    }

    // form the condition that enabled the conditional jump
    def jumpComp(label: Label, flag: CompFlag): Unit = lastFlagSet match {
        case Some(reg, regImm) =>
            val comp = Comp(getRegisterValue(reg), evaluate(regImm), convertFlag(flag))
            cmps += label -> comp
            condition = Some(comp)
        case None =>
    }

    // retrieve the first operand of a boolean expression
    def getCompDest: (ExprVar, CompOp) = if (cmps.contains(currentLabel)) {
        cmps(currentLabel) match {
            case Comp(reg, _, CompOp.Equal)    => (reg, CompOp.Or)
            case Comp(reg, _, CompOp.NotEqual) => (reg, CompOp.And)
            case _                             => (getVariable(RAX()), CompOp.Or)
        }
    } else {
        lastFlagSet match {
            case Some(reg, _) => (getRegisterValue(reg), CompOp.Equal)
            case None         => (getVariable(RAX()), CompOp.Equal)
        }
    }

    // retrieve the second operand of a boolean expression
    def getCompSrc: ExprVar = lastFlagSet match {
        case Some((_, reg)) => evaluate(reg)
        case None           => getVariable(RAX())
    }

    // get the block at the given label
    def getBlock(label: Label): Block = {
        blocks(label)
    }

    // retrieve the variable that is stored into the register at the current time
    def getRegisterValue(reg: Register): String = {
        val register = changeRegisterSize(reg, RegSize.QUAD_WORD)
        if (registers.contains(register)) {
            registers(register)
        } else {
            ""
        }
    }

    // push the variable on the stack
    def pushStack(reg: Register): Unit = {
        stack.push(getVariable(reg, true))
    }

    // pop the variable from the stack
    def popStack(reg: Register): Unit = {
        registers(reg) = stack.pop()
    }

    // create a new variable to assign
    def getVariableAndRemove(regMem: RegMem): String = {
        regMem match {
            case reg: Register     => registers.remove(changeRegisterSize(reg, RegSize.QUAD_WORD))
            case mem: MemoryAccess => memory.remove(mem)
        }
        getVariable(regMem)
    }

    // get the variable from the map of registers, or a new one if none is used
    def getVariable(regMem: RegMem, rhs: Boolean = false): String = regMem match {
        case reg: Register =>
            // correct the size
            val register = changeRegisterSize(reg, RegSize.QUAD_WORD)

            // TODO: handle parameters pushed on stack
            if (registers.contains(register)) {
                registers(register)
            } else {
                // if it is a parameter register and appears for the first time
                // assume it is a function parameter
                val variable = if (rhs && parameters.contains(register)) {
                    parameters.zipWithIndex.foreach { case (param, ind) =>
                        if (param == register) {
                            numParameters = if (numParameters > ind + 1) numParameters else ind + 1
                        }
                    }
                    namer.nextVariable(VarType.Parameter(register, label.name))
                } else {
                    namer.nextVariable(VarType.Variable)
                }
                // record the register
                registers += register -> variable
                variable
            }
        case mem: MemoryAccess =>
            if (memory.contains(mem)) {
                memory(mem)
            } else {
                val variable = namer.nextVariable(VarType.Variable)
                memory += mem -> variable
                variable
            }
    }

    // evaluate the operand (usually the source)
    def evaluate(oper: RegImmMem): String | Int = oper match {
        case imm: Immediate => imm.value
        case regMem: RegMem => getVariable(regMem, true)
    }
}

/** Disassembles the program using the current analysed information. */
def disassemble(controller: ControlFlow): List[Function] = {
    val blocks: List[(Label, Block)] = controller.programBlocks

    // assign each label block to the corresponding function block
    val funcBlocks: List[FuncBlock] = blocks
        .groupMap(_._1)(t => t._2.label -> t._2).toList
        .map((label, blocks) => FuncBlock(label, blocks.toMap))
    
    funcBlocks.map(disassemble(_, controller.programDirectives))
}

/** Disassembles the current function block. */
def disassemble(funcBlock: FuncBlock, directives: Set[StrLabel]): Function = {
    val FuncBlock(label, blocks) = funcBlock
    val instrs = mutable.ListBuffer.empty[Instr]

    // create a new disassembler for each function
    // TODO: track stack state between function calls
    val stack = mutable.Stack.empty[String]
    given disassembler: Disassembler =
        Disassembler(label, blocks, directives, stack)

    if (blocks.nonEmpty) {
        disassemble(blocks(label), instrs)
    }

    // return the value stored in the return register for all functions
    if (funcBlock.funcLabel.name != "main") {
        instrs += Return(disassembler.getVariable(RAX()))
    }

    // adjust the number of parameters of calls that do not use them explicitly
    if (functionIsWacc(funcBlock.funcLabel.name)) {
        disassembler.numParameters = 1
    }

    val params = parameters.take(disassembler.numParameters).map { reg =>
        disassembler.namer.nextVariable(VarType.Parameter(reg, label.name))
    }

    Function(label.name, params, instrs.toList)
}

/** Transforms a block of assembly instructions to the intermediate representation. */
def disassemble(block: Block, instrs: mutable.ListBuffer[Instr])
               (using disassembler: Disassembler): Unit = {
    disassembler.currentLabel = block.label
    // disassemble the current block of instructions
    block.instrs.foreach(disassemble(_, instrs))

    // check for an `if` pattern
    if (block.next.size == 2) {
        val first = disassembler.getBlock(block.next(0))
        val second = disassembler.getBlock(block.next(1))

        // check for a diamond shape in the control flow graph
        if (first.next.size == 1 && second.next.size == 1 && first.next(0) == second.next(0)) {
            // disassemble the two blocks of instructions
            val firstInstrs = mutable.ListBuffer.empty[Instr]
            disassemble(first, firstInstrs)
            val secondInstrs = mutable.ListBuffer.empty[Instr]
            disassemble(second, secondInstrs)

            instrs += If(disassembler.condition.getOrElse(memoryOffsets.TRUE), firstInstrs.toList, secondInstrs.toList)
            disassemble(disassembler.getBlock(first.next(0)), instrs)
        }
        // check for backward reference, which flags a loop
        else if (first.next.size == 1 && first.next(0) == block.label) {
            // conidition of the while loop
            val condition = disassembler.condition.getOrElse(memoryOffsets.TRUE)
            val firstInstrs = mutable.ListBuffer.empty[Instr]
            first.instrs.foreach(disassemble(_, firstInstrs))

            instrs += While(condition, firstInstrs.toList)
            disassemble(second, instrs)
        } else {
            // disassemble the second block, which is placed before the first one in assembly
            disassemble(second, instrs)
        }
    } else if (block.next.size == 1) {
        // move lineary to the next block
        val next = disassembler.getBlock(block.next(0))
        disassemble(next, instrs)
    }
}

/** Disassemble an instruction. */
def disassemble(instr: Instruction, instrs: mutable.ListBuffer[Instr])
               (using disassembler: Disassembler): Unit = instr match {
    case Push(reg) =>
        disassembler.pushStack(reg)
    case Pop(reg) =>
        disassembler.popStack(reg)

    // simple peephole optimisation
    case Mov(dest, src) if (dest == src) =>

    // find structure pattern
    case Mov(RDI(_), size: Immediate) =>
        disassembler.rdi = size.value
    case Call(Label(name)) if (name == "_malloc" || name == "malloc") =>
        disassembler.startStructPattern

    // remove stack operation
    case Mov(_, RSP(RegSize.QUAD_WORD)) =>

    // array load
    case Mov(R10(_), src) =>
        disassembler.arrayInd = disassembler.evaluate(src)
    case Mov(R9(_), regMem: RegMem) =>
        disassembler.arrayElem = disassembler.getVariable(regMem)
    case Call(Label(name)) if (name.startsWith("_arrStore")) =>
        instrs += Assignment(ArrayElem(disassembler.arrayElem, disassembler.arrayInd), disassembler.getVariable(RAX()))
    case Call(Label(name)) if (name.startsWith("_arrLoad")) =>
    case Mov(reg, R9(_)) =>
        instrs += Assignment(disassembler.getVariableAndRemove(reg), ArrayElem(disassembler.arrayElem, disassembler.arrayInd))

    // pair extraction
    case Cmp(R12(_), imm: Imm) if imm.value == memoryOffsets.NO_OFFSET =>
    case JumpComp(Label("_errNull"), _) =>
    case Mov(reg, MemAccess(R12(_), memoryOffsets.NO_OFFSET, _)) =>
        instrs += Assignment(disassembler.getVariableAndRemove(reg), Fst(disassembler.getVariable(R12())))
    case Mov(reg, MemAccess(R12(_), RegSize.QUAD_WORD.size, _)) =>
        instrs += Assignment(disassembler.getVariableAndRemove(reg), Snd(disassembler.getVariable(R12())))
    case Mov(MemAccess(R12(_), memoryOffsets.NO_OFFSET, _), reg) =>
        instrs += Assignment(Fst(disassembler.getVariable(R12())), disassembler.evaluate(reg))
    case Mov(MemAccess(R12(_), RegSize.QUAD_WORD.size, _), reg) =>
        instrs += Assignment(Snd(disassembler.getVariable(R12())), disassembler.evaluate(reg))

    case Mov(regMem, src) if (disassembler.structPattern && src == disassembler.initialStructReg) =>
        // check if multiple pointers are used to access the structure (usually in C)
        if (disassembler.structSize > 0) {
            disassembler.structReg = regMem
        } else {
            // retrieve the structure if it is accessed after it has been completed
            val structure = disassembler.getStructure
            instrs += Assignment(disassembler.getVariableAndRemove(regMem), structure)
        }

    // check for structure allocation
    case Mov(MemAccess(reg, offset: Int, size), src)
        if (disassembler.structPattern && reg == disassembler.structReg && !disassembler.arrays.contains(reg)) =>
            if (offset < 0) { disassembler.arrayPattern = true; disassembler.structSize -= RegSize.DOUBLE_WORD.size }
            else { disassembler.addStructElement(disassembler.evaluate(src), size.size) }
    case Mov(MemAccess(reg, offset: Int, size), src)
        if (disassembler.arrays.contains(reg)) =>
            instrs += Assignment(ArrayElem(disassembler.getVariable(reg), disassembler.arrayInd), disassembler.evaluate(src))

    case Mov(regMem, src) =>
        // check for division or modulo
        if (instrs.nonEmpty) { instrs.last match {
            case Assignment(_, Arithmetic(reg, regImm, ArithmeticOp.Div)) => src match {
                // if the RDX is accessed, it means that the operation was a modulo
                case RDX(size) =>
                    instrs.dropRight(1)
                    val dest = disassembler.getVariable(RDX(RegSize.DOUBLE_WORD))
                    instrs += Assignment(dest, Arithmetic(reg, regImm, ArithmeticOp.Mod))
                case _ =>
            }
            case _ =>
        } }

        // if it a structure pattern movement, update the structure register
        if (disassembler.structPattern && disassembler.structPointer) {
            disassembler.initialStructReg = regMem
            disassembler.structReg = regMem
            disassembler.getVariable(regMem)
            disassembler.structPointer = false
        } else {
            instrs += Assignment(disassembler.getVariableAndRemove(regMem), disassembler.evaluate(src))
        }

    case Cmp(reg, regImm) =>
        disassembler.setFlag(reg, regImm)
        disassembler.evaluate(reg)
    
    case JumpComp(label, flag) =>
        disassembler.jumpComp(label, flag)

    case SetComp(reg, CompFlag.E) =>
        val (dest, flag) = disassembler.getCompDest
        val src = disassembler.getCompSrc
        instrs += Assignment(disassembler.getVariableAndRemove(reg), Comp(dest, src, flag))
    case SetComp(reg, CompFlag.NE) =>
        val (dest, flag) = disassembler.getCompDest
        val src = disassembler.getCompSrc
        val comp = Comp(dest, src, flag)
        instrs += Assignment(disassembler.getVariableAndRemove(reg), Unary(comp, UnaryOp.Not))
    case CMov(dst, src, flag) =>

    case Add(reg, regImm) =>
        // check for pair pattern
        if (disassembler.structPattern && changeRegisterSize(reg, RegSize.QUAD_WORD) == disassembler.structReg) {
            // both WACC and C use addition to move within the array
            disassembler.arrayPattern = true
            // disassembler.structSize -= RegSize.DOUBLE_WORD.size
        } else {
            val dest = disassembler.getVariable(reg)
            instrs += Assignment(dest, Arithmetic(dest, disassembler.evaluate(regImm), ArithmeticOp.Add))
        }
    case Sub(reg, regImm) =>
        val dest = disassembler.getVariable(reg)
        instrs += Assignment(dest, Arithmetic(dest, disassembler.evaluate(regImm), ArithmeticOp.Sub))
    case Mul(reg, regImm) =>
        val dest = disassembler.getVariable(reg)
        instrs += Assignment(dest, Arithmetic(dest, disassembler.evaluate(regImm), ArithmeticOp.Mul))
    case MulImm(reg, regMul, regImm) =>
        val dest = disassembler.getVariable(reg)
        val src  = disassembler.getVariable(regMul)
        instrs += Assignment(dest, Arithmetic(src, disassembler.evaluate(regImm), ArithmeticOp.Mul))
    case Div(regImm) =>
        val reg = disassembler.getVariable(RAX(RegSize.DOUBLE_WORD))
        instrs += Assignment(reg, Arithmetic(reg, disassembler.evaluate(regImm), ArithmeticOp.Div))

    case Lea(reg, MemAccess(RIP(_), label: Label, _)) =>
        val dest = disassembler.getVariableAndRemove(reg)
        instrs += Assignment(dest, disassembler.getDirString(label))
    
    case Ret =>
        if (disassembler.label.name != "main") {
            instrs += Return(disassembler.getRegisterValue(RAX()))
        }

    case Call(label) =>
        // append the function parameters with all registers used
        // these would be capped on the next pass, when the number of parameters will be known
        val regs = parameters.map(disassembler.getRegisterValue(_))
        instrs += Assignment(disassembler.getVariable(RAX()), FuncCall(label.name, regs))

    case _ =>
}

/** Converts a conditional flag to a comparison operation. */
def convertFlag(flag: CompFlag): CompOp = flag match {
    case CompFlag.E  => CompOp.Equal
    case CompFlag.NE => CompOp.NotEqual
    case CompFlag.G  => CompOp.Greater
    case CompFlag.GE => CompOp.GreaterEq
    case CompFlag.L  => CompOp.Less
    case CompFlag.LE => CompOp.LessEq
}

def functionIsWacc(name: String): Boolean = {
    name == "_exit" || name == "_malloc" || name.startsWith("_read") || name == "_free"
}
