package wacc.backend.generator

import scala.collection.mutable

import wacc.backend.ir.*
import errors.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*
import widgets.*

import wacc.semantics.scoping.semanticTypes.*
import wacc.semantics.typing.*
import TyStmt.*

class WidgetManager {
    private val activeWidgets: mutable.Set[Widget] = mutable.Set.empty

    def activate(widget: Widget): Unit = {
        activeWidgets += widget
    }

    def usedWidgets: Set[Widget] = activeWidgets.toSet
}

class CodeGenerator(var instructions: mutable.Builder[Instruction, List[Instruction]],
                    directives: mutable.Builder[StrLabel, Set[StrLabel]],
                    labeller: Labeller,
                    temp: Temporary,
                    widgets: WidgetManager) {
    def ir: List[Instruction] = instructions.result()
    def data: Set[StrLabel] = directives.result()
    def dependencies: Set[Widget] = widgets.usedWidgets

    def addInstr(instruction: Instruction): Unit = {
        instructions += instruction
    }

    def addStrLabel(directive: StrLabel): Unit = {
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

    allocate(codeGen)
}

def generate(label: Label, params: Array[TyExpr.LVal], stmts: TyStmtList)
            (using codeGen: CodeGenerator): Unit = {    
    codeGen.addInstr(label)

    // TODO: replace 24 with the actual number of pushed registers
    codeGen.addInstr(Push(RBP()))
    codeGen.addInstr(Sub(RSP(), Imm(24)))
    codeGen.addInstr(Mov(RBP(), RSP()))

    // TODO: Assign parameters into registers

    stmts.map(generate)
    if (label.name == "main") {
        codeGen.addInstr(Mov(RAX(), Imm(0)))
    }

    // TODO: keep either move or add here
    // codeGen.addInstr(Mov(RSP(), RBP()))
    // TODO: replace 24 with the actual number of pushed registers
    codeGen.addInstr(Add(RSP(), Imm(24)))

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
        codeGen.addInstr(Call(codeGen.getWidgetLabel(widget)))
    
    case Free(expr: TyExpr) =>

    case Return(expr: TyExpr) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RAX(), temp))
        codeGen.addInstr(Ret)

    case Exit(expr: TyExpr) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RDI(), temp))
        codeGen.addInstr(Call(codeGen.getWidgetLabel(ExitProg)))

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
        codeGen.addInstr(Call(codeGen.getWidgetLabel(widget)))

    case Println(expr: TyExpr) =>
        generate(Print(expr))
        codeGen.addInstr(Call(PrintLn.label))

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
            (using codeGen: CodeGenerator): TempReg = expr match {
    case exp: TyExpr.BinaryBool =>
        shortCircuit(exp)

    case TyExpr.BinaryComp(lhs, rhs, op) =>
        val lhsTemp = generate(lhs)
        val rhsTemp = generate(rhs)

        codeGen.addInstr(Cmp(lhsTemp, rhsTemp))

        val compFlag = convertToJump(op)
        val resultReg = codeGen.nextTemp(BYTE)
        codeGen.addInstr(SetComp(resultReg, compFlag))

        resultReg
    
    case exp @ TyExpr.BinaryArithmetic(lhs, rhs, TyExpr.OpArithmetic.Div) =>
        generateDivMod(exp)
    
    case exp @ TyExpr.BinaryArithmetic(lhs, rhs, TyExpr.OpArithmetic.Mod) =>
        generateDivMod(exp)

    case TyExpr.BinaryArithmetic(lhs, rhs, op) =>
        val lhsTemp = generate(lhs)
        val rhsTemp = generate(rhs)

        codeGen.addInstr(op match {
            case TyExpr.OpArithmetic.Add => Add(lhsTemp, rhsTemp)
            case TyExpr.OpArithmetic.Sub => Sub(lhsTemp, rhsTemp)
            case TyExpr.OpArithmetic.Mul =>
                val temp = codeGen.nextTemp(DOUBLE_WORD)
                Mul(temp, lhsTemp, rhsTemp)
            // TODO: remove this case
            case _ => Add(lhsTemp, rhsTemp)
        })

        codeGen.addInstr(Jump(ErrOverflow.label, JumpFlag.Overflow))

        lhsTemp

    case TyExpr.Not(expr) => {
        // TODO: temp should be 8-bit register
        val temp = generate(expr)
        codeGen.addInstr(Cmp(temp, Imm(1)))
        codeGen.addInstr(SetComp(temp, CompFlag.NE))

        val resultReg = codeGen.nextTemp(BYTE)
        codeGen.addInstr(Mov(resultReg, temp))
        resultReg
    }
    case TyExpr.Neg(expr) => {
        val temp = generate(expr)
        val resultReg = codeGen.nextTemp(DOUBLE_WORD)
        codeGen.addInstr(Mov(resultReg, Imm(0)))
        codeGen.addInstr(Sub(resultReg, temp))

        codeGen.addInstr(Jump(ErrOverflow.label, JumpFlag.Overflow))
        resultReg
    }
    case TyExpr.Len(expr) => TempReg(-1)
    case TyExpr.Ord(expr) =>
        val temp = generate(expr)
        val resultReg = codeGen.nextTemp(DOUBLE_WORD)
        codeGen.addInstr(Mov(resultReg, temp))
        resultReg
    case TyExpr.Chr(expr) =>
        val temp = generate(expr)
        codeGen.addInstr(Test(temp, Imm(-128)))
        codeGen.addInstr(Mov(RSI(), temp))
        codeGen.addInstr(codeGen.getWidgetLabel(ErrBadChar))
        temp

    case TyExpr.IntLit(value)  =>
        val temp = codeGen.nextTemp()
        codeGen.addInstr(Mov(temp, Imm(value)))
        temp
    case TyExpr.BoolLit(value) =>
        val temp = codeGen.nextTemp(BYTE)
        codeGen.addInstr(Mov(temp, Imm(if (value) 1 else 0)))
        temp
    case TyExpr.CharLit(value) =>
        val temp = codeGen.nextTemp()
        codeGen.addInstr(Mov(temp, Imm(value.toInt)))
        temp
    case TyExpr.StrLit(value)  =>
        val label = codeGen.nextLabel(LabelType.Str)
        codeGen.addStrLabel(StrLabel(label, value))

        val temp = codeGen.nextTemp()
        codeGen.addInstr(Lea(temp, MemAccess(RIP(), label)))
        temp
    case TyExpr.PairLit        =>
        val temp = codeGen.nextTemp()
        codeGen.addInstr(Mov(temp, Imm(0)))
        temp

    case TyExpr.Id(value, semTy)    => TempReg(-1)
    case arrElem : TyExpr.ArrayElem => generateArrayElem(arrElem)
    case pairFst : TyExpr.PairFst   => generateFstSnd(pairFst)
    case pairSnd : TyExpr.PairSnd   => generateFstSnd(pairSnd)

    case TyExpr.ArrayLit(exprs, semTy)          => TempReg(-1)
    case TyExpr.NewPair(fst, snd, fstTy, sndTy) => TempReg(-1)
    case TyExpr.Call(func, args, retTy, argTys) => TempReg(-1)

    // TODO: remove this case
    case _ => TempReg(-1)
}

def generateCond(expr: TyExpr, label: Label)
                (using codeGen: CodeGenerator): Unit = expr match {
    case TyExpr.BinaryComp(lhs, rhs, op) => {
        val lhsTemp = generate(lhs)
        val rhsTemp = generate(rhs)
        
        codeGen.addInstr(Cmp(lhsTemp, rhsTemp))

        val compType = convertToJump(op)
        codeGen.addInstr(JumpComp(label, compType))
    }
    case _: TyExpr.BoolLit | _: TyExpr.BinaryBool => {
        val temp = generate(expr)
        codeGen.addInstr(Cmp(temp, Imm(1)))
        codeGen.addInstr(JumpComp(label, CompFlag.E))
    }
    case _ =>
}

def generateDivMod(expr: TyExpr.BinaryArithmetic)
                  (using codeGen: CodeGenerator): TempReg = {
    val TyExpr.BinaryArithmetic(lhs, rhs, op) = expr

    val rhsTemp = generate(rhs)
    codeGen.addInstr(Cmp(rhsTemp, Imm(0)))
    codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrDivZero), CompFlag.E))

    val lhsTemp = generate(lhs)
    codeGen.addInstr(Mov(RAX(), lhsTemp))
    codeGen.addInstr(ConvertDoubleToQuad)
    
    codeGen.addInstr(Div(rhsTemp))

    val resultReg = codeGen.nextTemp(DOUBLE_WORD)
    val res = if (op == TyExpr.OpArithmetic.Div) then RAX(DOUBLE_WORD) else RDX(DOUBLE_WORD)
    codeGen.addInstr(Mov(resultReg, res))

    resultReg
}

def generateFstSnd(pairElem: TyExpr.TyPairElem)
                  (using codeGen: CodeGenerator): TempReg = TempReg(-1)

def generateArrayElem(arrayElem: TyExpr.ArrayElem)
                     (using codeGen: CodeGenerator): TempReg = TempReg(-1)

def convertToJump(op: TyExpr.OpComp): CompFlag = op match {
    case TyExpr.OpComp.Equal        => CompFlag.E
    case TyExpr.OpComp.NotEqual     => CompFlag.NE
    case TyExpr.OpComp.GreaterThan  => CompFlag.G
    case TyExpr.OpComp.GreaterEqual => CompFlag.GE
    case TyExpr.OpComp.LessThan     => CompFlag.L
    case TyExpr.OpComp.LessEqual    => CompFlag.LE
}

def shortCircuit(expr: TyExpr.BinaryBool)
                (using codeGen: CodeGenerator): TempReg = {
    val TyExpr.BinaryBool(lhs, rhs, op) = expr
    val compFlag = if (op == TyExpr.OpBool.And) then CompFlag.NE else CompFlag.E

    val label = codeGen.nextLabel(LabelType.AnyLabel)
    val lhsTemp = generate(lhs)
    codeGen.addInstr(Cmp(lhsTemp, Imm(1)))
    codeGen.addInstr(JumpComp(label , compFlag))

    val rhsTemp = generate(rhs)
    codeGen.addInstr(Cmp(rhsTemp, Imm(1)))
    codeGen.addInstr(label)

    val resultReg = codeGen.nextTemp(BYTE)
    codeGen.addInstr(SetComp(resultReg, CompFlag.E))

    resultReg
}
