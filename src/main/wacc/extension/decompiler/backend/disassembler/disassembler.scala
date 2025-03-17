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

private final val parameters: List[Register] = List(RDI(), RSI(), RDX(), RCX(), R8(), R9())

class Disassembler(val label: Label,
                   blocks: Map[Label, Block],
                   directives: Set[StrLabel],
                   stack: mutable.Stack[String]) {
    private val registers = mutable.Map.empty[Register, String]
    private val memory = mutable.Map.empty[MemoryAccess, String]
    private val namer = new Namer

    var currentLabel = label
    var numParameters = 0
    var rdi = 0

    // array pattern
    var structPattern = false
    var structPointer = false
    var pairPattern = false
    var initialStructReg: RegMem = R11()
    var structReg: RegMem = R11()
    private var structElemSize = 0
    var structSize = 0
    private var structLiteral = mutable.ListBuffer.empty[String | Int]

    private var lastCmp: Option[(Register, RegImm)] = None
    private val cmps = mutable.Map.empty[Label, Comp]
    var condition: Option[Expr] = None

    def startStructLiteral: Unit = {
        structPattern = true
        structPointer = true
        pairPattern = false
        structElemSize = 0
        structSize = rdi
        structLiteral = mutable.ListBuffer.empty[String | Int]
    }

    def addStructElement(variable: String | Int, size: Int): Unit = {
        structLiteral += variable
        structElemSize = size
        structSize -= size
    }

    def getStructure: Expr = {
        structPattern = false
        if (pairPattern) {
            NewPair(structLiteral(0), structLiteral(1))
        } else {
            ArrayLit(structLiteral.toList, structElemSize)
        }
    }

    def getDirString(label: Label): StrLiteral = {
        // TODO: raise an error here
        StrLiteral(directives.find(_.label == label).map(_.name).getOrElse(""))
    }

    def setFlag(reg: Register, regImm: RegImm): Unit = {
        val register = changeRegisterSize(reg, RegSize.QUAD_WORD)
        lastCmp = Some((register, regImm))
    }

    def jumpComp(label: Label, flag: CompFlag): Unit = lastCmp match {
        case Some(reg, regImm) =>
            val comp = Comp(getRegisterValue(reg), evaluate(regImm), convertFlag(flag))
            cmps += label -> comp
            condition = Some(comp)

            // free the RAX register, which would likely be reused by the other side
            registers.remove(reg)
        case None =>
    }

    def getCompDest: (ExprVar, CompOp) = cmps(currentLabel) match {
        case Comp(reg, _, CompOp.Equal)    => (reg, CompOp.Or)
        case Comp(reg, _, CompOp.NotEqual) => (reg, CompOp.And)
        case _                             => (getVariable(RAX()), CompOp.Or)
    }

    def getCompSrc: ExprVar = lastCmp match {
        case Some((reg, _)) => getVariable(reg)
        case None           => getVariable(RAX())
    }

    def getBlock(label: Label): Block = {
        blocks(label)
    }
    def getRegisterValue(reg: Register): String = {
        val register = changeRegisterSize(reg, RegSize.QUAD_WORD)
        if (registers.contains(register)) {
            registers(register)
        } else {
            ""
        }
    }
    def pushStack(reg: Register): Unit = {
        stack.push(getRegisterValue(reg))
        registers.remove(reg)
    }
    def popStack(reg: Register): Unit = {
        registers(reg) = stack.pop()
    }

    def getVariableAndRemove(regMem: RegMem): String = {
        regMem match {
            case reg: Register     => registers.remove(changeRegisterSize(reg, RegSize.QUAD_WORD))
            case mem: MemoryAccess => memory.remove(mem)
        }
        getVariable(regMem)
    }

    def getVariable(regMem: RegMem, rhs: Boolean = false): String = regMem match {
        case reg: Register =>
            val register = changeRegisterSize(reg, RegSize.QUAD_WORD)
            if (registers.contains(register)) {
                registers(register)
            } else {
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
                registers += register -> variable
                variable
            }
        case mem: MemoryAccess =>
            if (memory.contains(mem)) {
                memory(mem)
            } else {
                // TODO: deal with parameters
                val variable = namer.nextVariable(VarType.Variable)
                memory += mem -> variable
                variable
            }
    }

    def getVariableWithoutAssign(reg: Register): String = {
        if (registers.contains(reg)) {
            registers(reg)
        } else {
            ""
        }
    }

    def evaluate(oper: RegImmMem): String | Int = oper match {
        case imm: Immediate => imm.value
        case regMem: RegMem => getVariable(regMem, true)
    }
}

def convertFlag(flag: CompFlag): CompOp = flag match {
    case CompFlag.E  => CompOp.Equal
    case CompFlag.NE => CompOp.NotEqual
    case CompFlag.G  => CompOp.Greater
    case CompFlag.GE => CompOp.GreaterEq
    case CompFlag.L  => CompOp.Less
    case CompFlag.LE => CompOp.LessEq
}

def disassemble(controller: ControlFlow): List[Function] = {
    val blocks: List[(Label, Block)] = controller.programBlocks

    val funcBlocks: List[FuncBlock] = blocks
        .groupMap(_._1)(t => t._2.label -> t._2).toList
        .map((label, blocks) => FuncBlock(label, blocks.toMap))
    
    funcBlocks.map(disassemble(_, controller.programDirectives))
}

def disassemble(funcBlock: FuncBlock, directives: Set[StrLabel]): Function = {
    val FuncBlock(label, blocks) = funcBlock
    val instrs = mutable.ListBuffer.empty[Instr]

    given disassembler: Disassembler =
        Disassembler(label, blocks, directives, mutable.Stack.empty[String])

    if (blocks.nonEmpty) {
        disassemble(blocks(label), instrs)
    }

    if (funcBlock.funcLabel.name != "main") {
        instrs += Return(disassembler.getVariable(RAX()))
    }

    if (funcBlock.funcLabel.name == "_exit" || funcBlock.funcLabel.name == "_malloc") {
        disassembler.numParameters = 1
    }

    Function(label.name, disassembler.numParameters, instrs.toList)
}

def disassemble(block: Block, instrs: mutable.ListBuffer[Instr])
               (using disassembler: Disassembler): Unit = {
    disassembler.currentLabel = block.label
    block.instrs.foreach(disassemble(_, instrs))

    // check if we encounter an if
    if (block.next.size == 2) {
        val first = disassembler.getBlock(block.next(0))
        val second = disassembler.getBlock(block.next(1))

        if (first.next.size == 1 && second.next.size == 1 && first.next(0) == second.next(0)) {
            val firstInstrs = mutable.ListBuffer.empty[Instr]
            disassemble(first, firstInstrs)
            val secondInstrs = mutable.ListBuffer.empty[Instr]
            disassemble(second, secondInstrs)

            // TODO: replace 1
            instrs += If(disassembler.condition.getOrElse(memoryOffsets.TRUE), firstInstrs.toList, secondInstrs.toList)
            disassemble(disassembler.getBlock(first.next(0)), instrs)
        } else if (first.next.size == 1 && first.next(0) == block.label) {
            // conidition of the while loop - maybe
            // one of the label will reference back, do not recompute it
            val condition = disassembler.condition.getOrElse(memoryOffsets.TRUE)
            val firstInstrs = mutable.ListBuffer.empty[Instr]
            first.instrs.foreach(disassemble(_, firstInstrs))
            // disassemble(first, firstInstrs)

            instrs += While(condition, firstInstrs.toList)
            disassemble(second, instrs)
        } else {
            // TOOD: correct this
            disassemble(second, instrs)
        }
    } else if (block.next.size == 1) {
        val next = disassembler.getBlock(block.next(0))
        disassemble(next, instrs)
    }
}

def disassemble(instr: Instruction, instrs: mutable.ListBuffer[Instr])
               (using disassembler: Disassembler): Unit = instr match {
    case Push(reg) =>
        disassembler.pushStack(reg)
    case Pop(reg) =>
        disassembler.popStack(reg)

    // simple peephole optimisation
    case Mov(dest, src) if (dest == src) =>

    // find array
    case Mov(RDI(_), size: Immediate) =>
        disassembler.rdi = size.value
    case Call(Label(name)) if (name == "_malloc" || name == "malloc") =>
        disassembler.startStructLiteral

    case Mov(regMem, src) if (disassembler.structPattern && src == disassembler.initialStructReg) =>
        if (disassembler.structSize > 0) {
            disassembler.structReg = regMem
        } else {
            val structure = disassembler.getStructure
            instrs += Assignment(disassembler.getVariableAndRemove(regMem), structure)
        }

    // check for structure allocation
    case Mov(MemAccess(reg, offset: Int, size), src)
        if (disassembler.structPattern && reg == disassembler.structReg) =>
            if (offset > 0) { disassembler.pairPattern = true }
            disassembler.addStructElement(disassembler.evaluate(src), size.size)

    case Mov(regMem, src) =>
        // check for divs and mods
        if (instrs.nonEmpty) { instrs.last match {
            case Assignment(_, Arithmetic(reg, regImm, ArithmeticOp.Div)) => src match {
                case RDX(size) =>
                    instrs.dropRight(1)
                    val dest = disassembler.getVariable(RDX(RegSize.DOUBLE_WORD))
                    instrs += Assignment(dest, Arithmetic(reg, regImm, ArithmeticOp.Mod))
                case _ =>
            }
            case _ =>
        } }

        if (disassembler.structPattern && disassembler.structPointer) {
            disassembler.initialStructReg = regMem
            disassembler.structReg = regMem
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
        instrs += Assignment(disassembler.getVariable(reg), Comp(dest, src, flag))
    case SetComp(reg, CompFlag.NE) =>
        val (dest, flag) = disassembler.getCompDest
        val src = disassembler.getCompSrc
        val comp = Comp(dest, src, flag)
        instrs += Assignment(disassembler.getVariable(reg), Unary(comp, UnaryOp.Not))
    
    // TODO: handle `cmov`s

    case Add(reg, regImm) =>
        // check for pair pattern
        if (disassembler.structPattern && changeRegisterSize(reg, RegSize.QUAD_WORD) == disassembler.structReg) {
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
        // TODO: deal with registers: RAX(), RDX()
        // EAX() => DIV, CAT
        // EDX() => MOD, REST
        val reg = disassembler.getVariable(RAX(RegSize.DOUBLE_WORD))
        instrs += Assignment(reg, Arithmetic(reg, disassembler.evaluate(regImm), ArithmeticOp.Div))

    case Lea(reg, MemAccess(RIP(_), label: Label, _)) =>
        val dest = disassembler.getVariableAndRemove(reg)
        instrs += Assignment(dest, disassembler.getDirString(label))
    
    case Ret =>
        if (disassembler.label.name == "main") {
            instrs += Return(disassembler.getRegisterValue(RAX()))
        }

    case Call(label) =>
        val regs = parameters.map(disassembler.getVariableWithoutAssign(_))
        instrs += Assignment(disassembler.getVariable(RAX()), FuncCall(label.name, regs))

    case _ =>
}
