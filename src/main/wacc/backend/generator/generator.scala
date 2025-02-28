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
import utils.*

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

    // information of which temporary registers hold variables
    final val registers: Array[Register] = Array(RDI(), RSI(), RDX(), RCX(), R8(), R9())
    private val varRegs: mutable.Map[String, Register] = mutable.Map.empty

    def addInstr(instruction: Instruction): Unit = {
        instructions += instruction
    }

    def addStrLabel(directive: StrLabel): Unit = {
        directives += directive
    }

    def nextLabel(labelType: LabelType): Label = {
        labeller.nextLabel(labelType)
    }

    def nextTemp(size: RegSize = RegSize.QUAD_WORD): TempReg = {
        temp.next(size)
    }

    def addVar(name: String, reg: Register): Register = {
        varRegs += name -> reg
        reg
    }

    def getVar(name: String): Register = varRegs.getOrElse(name, addVar(name, nextTemp()))

    // TODO: delete all the below

    // def allocateVar(varName: String, location: Register): Unit = {
    //     varToLoc(varName) = location
    // }

    // def getVar(name: String): Register = varToLoc.getOrElse(name, {
    //     val temp = nextTemp()
    //     allocateVar(name, temp)
    //     temp
    // })

    // def initFunctionArgs(label: String): Unit = {
    //     funcToArgs(label) = List.empty
    // }

    // def getFunctionArgs(label: String): List[Register] = funcToArgs(label)

    // def addFunctionArg(label: String, arg: Register): Unit = funcToArgs(label) = funcToArgs(label) :+ arg
    
    def getWidgetLabel(widget: Widget): Label = {
        widgets.activate(widget)
        widget.label
    }
}

def generate(prog: TyProg): CodeGenerator = {
    given codeGen: CodeGenerator =
        CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)

    val TyProg(funcs, stmts) = prog

    funcs.map { case TyFunc(name, params, stmts) =>
        val label = codeGen.nextLabel(LabelType.Function(name))

        params.zip(codeGen.registers).foreach { case (param, reg) =>
            codeGen.addVar(param.value, reg)
        }

        generate(label, stmts)
    }

    generate(codeGen.nextLabel(LabelType.Main), stmts)

    allocate(codeGen)
}

def generate(label: Label, stmts: TyStmtList)
            (using codeGen: CodeGenerator): Unit = {    
    codeGen.addInstr(label)
    codeGen.addInstr(Push(RBP()))

    stmts.map(generate)
    if (label.name == "main") {
        codeGen.addInstr(Mov(RAX(), Imm(0)))
    }

    codeGen.addInstr(Pop(RBP()))
    codeGen.addInstr(Ret)
}

def generate(stmt: TyStmt)
            (using codeGen: CodeGenerator): Unit = stmt match {
    //variable declarations or reassignments
    case Assignment(id: TyExpr.Id, expr: TyExpr) => 
        val rhs = generate(expr)
        val lhs = codeGen.getVar(id.value)
        codeGen.addInstr(Mov(lhs, rhs))
    
    // array reassignments only
    case Assignment(TyExpr.ArrayElem(id, idx, semTy), expr: TyExpr) =>
        // compute the left-hand side
        val (temp, size) = idx match {
            case expr1 :: expr2 :: rest  =>
                (generateArrayElem(id, expr2 :: rest, semTy), RegSize.QUAD_WORD)
            case expr :: Nil             =>
                (generate(expr), getTypeSize(getArrayType(semTy)))
            // TODO: remove this case
            case Nil                     =>
                (TempReg(0), RegSize.QUAD_WORD)
        }
        codeGen.addInstr(Mov(R10(size), temp))

        // compute the expression
        val rhs = generate(expr)
        codeGen.addInstr(Mov(RAX(), rhs))

        // store the element into the array
        codeGen.addInstr(Mov(R9(), codeGen.getVar(id.value)))
        codeGen.addInstr(Call(codeGen.getWidgetLabel(getArrayElementStoreWidget(size))))
    
    // pair reassignments only
    case Assignment(pairElem: TyExpr.TyPairElem, expr: TyExpr) =>
        // compute the inner lvalue
        val pairPtr = generate(pairElem.lval)

        // check if a null pair is being dereferenced
        codeGen.addInstr(Cmp(pairPtr, Imm(memoryOffsets.NULL)))
        codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrNull), CompFlag.E))

        // compute the offset
        val offset = pairElem match {
            case _: TyExpr.PairFst => memoryOffsets.NO_OFFSET
            case _: TyExpr.PairSnd => RegSize.QUAD_WORD.size
        }

        // compute the expression
        val rhs = generate(expr)
        codeGen.addInstr(Mov(MemAccess(pairPtr, offset), rhs))  

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
        val temp = generate(expr)
        expr.ty match {
            case KType.Array(_, _) =>
                codeGen.addInstr(Mov(RDI(), temp))
                codeGen.addInstr(Sub(RDI(), Imm(memoryOffsets.ARRAY_LENGTH_OFFSET)))
                codeGen.addInstr(Call(codeGen.getWidgetLabel(FreeProg)))
            case KType.Pair(_, _) =>
                codeGen.addInstr(Cmp(temp, Imm(0)))
                codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrNull), CompFlag.E))
                codeGen.addInstr(Mov(RDI(), temp))
                codeGen.addInstr(Call(codeGen.getWidgetLabel(FreePair)))
            case _ =>
                codeGen.addInstr(Mov(RDI(), temp))
                codeGen.addInstr(Call(codeGen.getWidgetLabel(FreeProg)))
        }

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
        codeGen.addInstr(Call(codeGen.getWidgetLabel(PrintLn)))

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
            (using codeGen: CodeGenerator): Register = expr match {
    case exp: TyExpr.BinaryBool =>
        shortCircuit(exp)

    case TyExpr.BinaryComp(lhs, rhs, op) =>
        val lhsLoc = generate(lhs)
        val rhsLoc = generate(rhs)

        codeGen.addInstr(Cmp(lhsLoc, rhsLoc))

        val compFlag = convertToJump(op)
        val resultReg = codeGen.nextTemp(RegSize.BYTE)
        codeGen.addInstr(SetComp(resultReg, compFlag))

        resultReg
    
    case exp @ TyExpr.BinaryArithmetic(lhs, rhs, TyExpr.OpArithmetic.Div) =>
        generateDivMod(exp)
    
    case exp @ TyExpr.BinaryArithmetic(lhs, rhs, TyExpr.OpArithmetic.Mod) =>
        generateDivMod(exp)

    case TyExpr.BinaryArithmetic(lhs, rhs, op) =>
        val lhsLoc = generate(lhs)
        val rhsLoc = generate(rhs)

        codeGen.addInstr(op match {
            case TyExpr.OpArithmetic.Add => Add(lhsLoc, rhsLoc)
            case TyExpr.OpArithmetic.Sub => Sub(lhsLoc, rhsLoc)
            case TyExpr.OpArithmetic.Mul =>
                val temp = codeGen.nextTemp(RegSize.DOUBLE_WORD)
                Mul(temp, lhsLoc, rhsLoc)
            // TODO: remove this case
            case _ => Add(lhsLoc, rhsLoc)
        })
        codeGen.addInstr(Jump(codeGen.getWidgetLabel(ErrOverflow), JumpFlag.Overflow))
        lhsLoc

    case TyExpr.Not(expr) => 
        // TODO: temp should be 8-bit register
        val temp = generate(expr) 
        codeGen.addInstr(Cmp(temp, Imm(memoryOffsets.TRUE)))
        codeGen.addInstr(SetComp(temp, CompFlag.NE))

        val resultReg = codeGen.nextTemp(RegSize.BYTE)
        codeGen.addInstr(Mov(resultReg, temp))
        resultReg
    
    case TyExpr.Neg(expr) => 
        val temp = generate(expr)
        val resultReg = codeGen.nextTemp(RegSize.DOUBLE_WORD)
        codeGen.addInstr(Mov(resultReg, Imm(memoryOffsets.NO_OFFSET)))
        codeGen.addInstr(Sub(resultReg, temp))

        codeGen.addInstr(Jump(ErrOverflow.label, JumpFlag.Overflow))
        resultReg
    
    case TyExpr.Len(expr) => 
        val temp = generate(expr)
        val resultReg = codeGen.nextTemp(RegSize.DOUBLE_WORD)
        // the size is placed 4 bytes before the beginning of the array
        codeGen.addInstr(Mov(resultReg, MemAccess(temp, -RegSize.DOUBLE_WORD.size)))
        resultReg
    
    case TyExpr.Ord(expr) =>
        val temp = generate(expr)
        val resultReg = codeGen.nextTemp(RegSize.DOUBLE_WORD)
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
        val temp = codeGen.nextTemp(RegSize.BYTE)
        codeGen.addInstr(Mov(temp, Imm(if (value) memoryOffsets.TRUE else memoryOffsets.FALSE)))
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
        codeGen.addInstr(Mov(temp, Imm(memoryOffsets.NULL)))
        temp

    case TyExpr.Id(value, semTy)    => codeGen.getVar(value)
    case TyExpr.ArrayElem(id, idx, semTy) => generateArrayElem(id, idx, semTy)
    case pairFst : TyExpr.PairFst   => generateFstSnd(pairFst)
    case pairSnd : TyExpr.PairSnd   => generateFstSnd(pairSnd)

    case TyExpr.ArrayLit(exprs, semTy)          => generateArrayLit(exprs, semTy)
    case TyExpr.NewPair(fst, snd, fstTy, sndTy) => generateNewPair(fst, snd, fstTy, sndTy)
    case TyExpr.Call(func, args, retTy, _)      => generateCall(func, args, retTy)
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
        codeGen.addInstr(Cmp(temp, Imm(memoryOffsets.TRUE)))
        codeGen.addInstr(JumpComp(label, CompFlag.E))
    }
    case _ =>
}

def generateDivMod(expr: TyExpr.BinaryArithmetic)
                  (using codeGen: CodeGenerator): Register = {
    val TyExpr.BinaryArithmetic(lhs, rhs, op) = expr

    val rhsTemp = generate(rhs) 
    codeGen.addInstr(Cmp(rhsTemp, Imm(memoryOffsets.DIV_ZERO)))
    codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrDivZero), CompFlag.E))

    val lhsTemp = generate(lhs) 

    codeGen.addInstr(Mov(RAX(), lhsTemp))
    codeGen.addInstr(ConvertDoubleToQuad)
    
    codeGen.addInstr(Div(rhsTemp))

    val resultReg = codeGen.nextTemp(RegSize.DOUBLE_WORD)
    val res = if (op == TyExpr.OpArithmetic.Div) then RAX(RegSize.DOUBLE_WORD) else RDX(RegSize.DOUBLE_WORD)
    codeGen.addInstr(Mov(resultReg, res))

    resultReg
}

def generateNewPair(fst: TyExpr, snd: TyExpr, fstTy: SemType, sndTy: SemType)
                   (using codeGen: CodeGenerator): TempReg = {
    // the size of the pair is: 8 bytes for fst + 8 bytes for snd
    val pairSize = RegSize.QUAD_WORD.size + RegSize.QUAD_WORD.size
    
    // allocate memory for the pair
    codeGen.addInstr(Mov(RDI(RegSize.DOUBLE_WORD), Imm(pairSize)))
    codeGen.addInstr(Call(codeGen.getWidgetLabel(Malloc)))

    // save the pair pointer into a register
    val pairPtr = codeGen.nextTemp()
    codeGen.addInstr(Mov(pairPtr, RAX()))

    // move the values into the pair locations
    val fstTemp = generate(fst)
    codeGen.addInstr(Mov(MemAccess(pairPtr), fstTemp))
    val sndTemp = generate(snd)
    codeGen.addInstr(Mov(MemAccess(pairPtr, RegSize.QUAD_WORD.size), sndTemp))

    pairPtr
}

def generateFstSnd(pairElem: TyExpr.TyPairElem)
                  (using codeGen: CodeGenerator): TempReg = {
    // compute the inner lvalue
    val pairPtr = generate(pairElem.lval)

    // check if a null pair is being dereferenced
    codeGen.addInstr(Cmp(pairPtr, Imm(memoryOffsets.NULL)))
    codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrNull), CompFlag.E))

    // compute the offset
    val offset = pairElem match {
        case _: TyExpr.PairFst => memoryOffsets.NO_OFFSET
        case _: TyExpr.PairSnd => RegSize.QUAD_WORD.size
    }

    val resultReg = codeGen.nextTemp()
    codeGen.addInstr(Mov(RAX(), MemAccess(pairPtr, offset)))
    codeGen.addInstr(Mov(resultReg, RAX(getTypeSize(pairElem.ty))))

    resultReg
}

def generateArrayLit(exprs: List[TyExpr], semTy: SemType)
                     (using codeGen: CodeGenerator): TempReg = {
    // compute the size of the array
    val elementSize = getTypeSize(semTy)
    val exprsLength = exprs.length
    // reserve the first 4 bytes for the size
    val totalSize = RegSize.DOUBLE_WORD.size + elementSize.size * exprsLength

    codeGen.addInstr(Mov(RDI(RegSize.DOUBLE_WORD), Imm(totalSize)))
    codeGen.addInstr(Call(codeGen.getWidgetLabel(Malloc)))

    val arrayPtr = codeGen.nextTemp()
    codeGen.addInstr(Mov(arrayPtr, RAX(RegSize.DOUBLE_WORD)))

    // store the size in the first 4 bytes
    codeGen.addInstr(Add(arrayPtr, Imm(RegSize.DOUBLE_WORD.size)))
    codeGen.addInstr(Mov(MemAccess(arrayPtr, -RegSize.DOUBLE_WORD.size), Imm(exprsLength)))

    // store the elements
    exprs.zipWithIndex.foreach { case (expr, i) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(MemAccess(arrayPtr, i * elementSize.size), temp))
    }

    arrayPtr
}

def generateArrayElem(id: TyExpr.Id, idx: List[TyExpr], semTy: SemType)
                     (using codeGen: CodeGenerator): TempReg = {
    // check if the array is nested
    val (temp, size) = idx match {
        case expr1 :: expr2 :: rest  =>
            (generateArrayElem(id, expr2 :: rest, semTy), RegSize.QUAD_WORD)
        case expr :: Nil             =>
            (generate(expr), getTypeSize(getArrayType(semTy)))
        // TODO: remove this case
        case Nil                     =>
            (TempReg(0), RegSize.QUAD_WORD)
    }

    codeGen.addInstr(Mov(R10(size), temp))
    codeGen.addInstr(Mov(R9(), codeGen.getVar(id.value)))
    codeGen.addInstr(Call(codeGen.getWidgetLabel(getArrayElementLoadWidget(size))))

    val resultReg = codeGen.nextTemp(size)
    codeGen.addInstr(Mov(resultReg, R9(size)))

    resultReg
}

def generateCall(func: String, args: List[TyExpr], retTy: SemType)
                (using codeGen: CodeGenerator): TempReg = {
    // move the first six arguments into the corresponding registers
    // push the rest of the arguments on the stack
    args.zipWithIndex.foreach { case (arg, ind) =>
        val instr = if (ind < constants.MAX_CALL_ARGS) {
            Mov(codeGen.registers(ind), generate(arg))
        } else {
            Push(generate(arg))
        }
        codeGen.addInstr(instr)
    }
    
    // call the function
    codeGen.addInstr(Call(codeGen.nextLabel(LabelType.Function(func))))

    // get return type size
    val returnSize = getTypeSize(retTy)

    // save return value
    val temp = codeGen.nextTemp(returnSize)
    codeGen.addInstr(Mov(temp, RAX(returnSize)))

    temp
}

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
    codeGen.addInstr(Cmp(lhsTemp, Imm(memoryOffsets.TRUE)))
    codeGen.addInstr(JumpComp(label , compFlag))

    val rhsTemp = generate(rhs)
    codeGen.addInstr(Cmp(rhsTemp, Imm(memoryOffsets.TRUE)))
    codeGen.addInstr(label)

    val resultReg = codeGen.nextTemp(RegSize.BYTE)
    codeGen.addInstr(SetComp(resultReg, CompFlag.E))

    resultReg
}
