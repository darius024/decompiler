package wacc.backend.generator

import scala.collection.mutable

import wacc.backend.ir.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*
import widgets.*

import wacc.semantics.scoping.semanticTypes.*
import wacc.semantics.typing.*
import TyExpr.*
import TyStmt.*

class WidgetManager {
    private val activeWidgets: mutable.Set[Widget] = mutable.Set.empty

    def activate(widget: Widget): Unit = {
        activeWidgets += widget
    }

    def usedWidgets: Set[Widget] = activeWidgets.toSet
}

class CodeGenerator(instructions: mutable.Builder[Instruction, List[Instruction]],
                    directives: mutable.Builder[Directive, Set[Directive]],
                    labeller: Labeller,
                    temp: Temporary,
                    widgets: WidgetManager) {
    def ir: List[Instruction] = instructions.result()
    def data: Set[Directive] = directives.result()
    def dependencies: Set[Widget] = widgets.usedWidgets

    def addInstr(instruction: Instruction): Unit = {
        instructions += instruction
    }

    def addDirective(directive: Directive): Unit = {
        directives += directive
    }

    def nextLabel(labelType: LabelType): Label = {
        labeller.nextLabel(labelType)
    }

    def nextTemp(size: Int = QUAD_WORD): TempReg = {
        temp.next(size)
    }

    def getWidgetLabel(widget: Widget): Label = {
        widgets.activate(widget)
        widget.label
    }
}

def generate(prog: TyProg): CodeGenerator = {
    given codeGen: CodeGenerator =
        CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)

    val TyProg(funcs, stmts) = prog

    funcs.map { func =>
        generate(codeGen.nextLabel(LabelType.Function(func.name)), func.params, func.stmts)
    }

    generate(codeGen.nextLabel(LabelType.Main), Array.empty, stmts)

    codeGen
}

def generate(label: Label, params: Array[TyExpr.LVal], stmts: TyStmtList)
            (using codeGen: CodeGenerator): Unit = {    
    codeGen.addInstr(label)

    codeGen.addInstr(Push(RBP()))
    codeGen.addInstr(Mov(RBP(), RSP()))

    // TODO: Assign parameters into registers

    stmts.map(generate)
    if (label.name == "main") {
        codeGen.addInstr(Mov(RAX(), Imm(0)))
    }

    codeGen.addInstr(Mov(RSP(), RBP()))
    codeGen.addInstr(Pop(RBP()))
    codeGen.addInstr(Ret)
}

def generate(stmt: TyStmt)
            (using codeGen: CodeGenerator): Unit = stmt match {
    case Assignment(ref: TyExpr.LVal, expr: TyExpr) => {
        val rhs = generate(expr)
        val lhs = generate(ref)
        codeGen.addInstr(Mov(lhs, rhs))
    }

    case Read(expr: TyExpr.LVal) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RDI(), temp))

        val widget = expr.ty match {
            case KType.Int  => ReadInt
            case KType.Char => ReadChar
            // TODO: Remove this case
            case _          => ReadInt
        }
        codeGen.addInstr(FuncCall(codeGen.getWidgetLabel(widget)))
    
    case Free(expr: TyExpr) => ???

    case Return(expr: TyExpr) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RAX(), temp))
        codeGen.addInstr(Ret)

    case Exit(expr: TyExpr) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RDI(), temp))
        codeGen.addInstr(FuncCall(codeGen.getWidgetLabel(ExitProg)))

    case Print(expr: TyExpr) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RDI(), temp))

        val widget = expr.ty match {
            case KType.Int  => PrintInt
            case KType.Bool => PrintBool
            case KType.Char => PrintChar
            case KType.Str  => PrintString
            // TODO: Remove this case
            case _          => PrintInt
        }
        codeGen.addInstr(FuncCall(codeGen.getWidgetLabel(widget)))

    case Println(expr: TyExpr) =>
        generate(Print(expr))
        codeGen.addInstr(FuncCall(PrintLn.label))

    case If(cond: TyExpr, thenStmts: TyStmtList, elseStmts: TyStmtList) =>
        val ifLabel = codeGen.nextLabel(LabelType.If)
        val endIfLabel = codeGen.nextLabel(LabelType.IfEnd)

        generateCond(cond, ifLabel)
        elseStmts.map(generate)
        codeGen.addInstr(Jump(endIfLabel, JumpFlag.Unconditional))
        codeGen.addInstr(ifLabel)
        thenStmts.map(generate)
        codeGen.addInstr(endIfLabel)
    
    case While(cond: TyExpr, doStmts: TyStmtList) =>
        val whileBodyLabel = codeGen.nextLabel(LabelType.WhileBody)
        val whileCondLabel = codeGen.nextLabel(LabelType.WhileCond)

        codeGen.addInstr(Jump(whileCondLabel, JumpFlag.Unconditional))
        codeGen.addInstr(whileBodyLabel)
        doStmts.map(generate)

        codeGen.addInstr(whileCondLabel)
        generateCond(cond, whileBodyLabel)
    
    case Block(stmts: TyStmtList) =>
        stmts.map(generate)
}

def generate(expr: TyExpr)
            (using codeGen: CodeGenerator): TempReg = ???

def generateCond(expr: TyExpr, label: Label)
                (using codeGen: CodeGenerator): Unit = expr match {
    case BinaryComp(lhs, rhs, op) => {
        val lhsTemp = generate(lhs)
        val rhsTemp = generate(rhs)
        
        codeGen.addInstr(Cmp(lhsTemp, rhsTemp))

        val compType = convertToJump(op)
        codeGen.addInstr(JumpComp(label, compType))
    }
    case _: BoolLit | _: BinaryBool => {
        val temp = generate(expr)
        codeGen.addInstr(Cmp(temp, Imm(1)))
        codeGen.addInstr(JumpComp(label, CompFlag.E))
    }
    case _ =>
}

def generateDivMod(expr: TyExpr)
                  (using codeGen: CodeGenerator): TempReg = ???

def generateFstSnd(pairElem: TyPairElem)
                  (using codeGen: CodeGenerator): TempReg = ???

def generateArrayElem(arrayElem: ArrayElem)
                     (using codeGen: CodeGenerator): TempReg = ???

def convertToJump(op: OpComp): CompFlag = op match {
    case OpComp.Equal        => CompFlag.E
    case OpComp.NotEqual     => CompFlag.NE
    case OpComp.GreaterThan  => CompFlag.G
    case OpComp.GreaterEqual => CompFlag.GE
    case OpComp.LessThan     => CompFlag.L
    case OpComp.LessEqual    => CompFlag.LE
}
