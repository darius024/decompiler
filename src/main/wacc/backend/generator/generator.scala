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

    val functionRegs: List[Register] = List(RDI(), RSI(), RDX(), RCX(), R8(), R9())

    private val varToLoc: mutable.Map[String, Register] = mutable.Map.empty

    //function label to where arguments are stored
    private val funcToArgs: mutable.Map[String, List[Register]] = mutable.Map.empty

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

    def allocateVar(varName: String, location: Register): Unit = {
        varToLoc(varName) = location
    }

    def getVar(name: String): Register = varToLoc.getOrElse(name, {
        val temp = nextTemp()
        allocateVar(name, temp)
        temp
    })

    def initFunctionArgs(label: String): Unit = {
        funcToArgs(label) = List.empty
    }

    def getFunctionArgs(label: String): List[Register] = funcToArgs(label)

    def addFunctionArg(label: String, arg: Register): Unit = funcToArgs(label) = funcToArgs(label) :+ arg
    
    def getWidgetLabel(widget: Widget): Label = {
        widgets.activate(widget)
        widget.label
    }

    def getArrayElementLoadWidget(elemSize: Int): Widget = elemSize match {
        case BYTE => ArrayLoad1
        case WORD => ArrayLoad2
        case DOUBLE_WORD => ArrayLoad4
        case QUAD_WORD => ArrayLoad8  
        case _ => throw new RuntimeException(s"Invalid element size: $elemSize") //keep until we extensively test backend
    }

    def getArrayElementStoreWidget(elemSize: Int): Widget = elemSize match {
        case BYTE => ArrayLoad1
        case WORD => ArrayLoad2
        case DOUBLE_WORD => ArrayLoad4
        case QUAD_WORD => ArrayLoad8 
        case _ => throw new RuntimeException(s"Invalid element size: $elemSize") //keep until we extensively test backend
    }
}


def generate(prog: TyProg): CodeGenerator = {
    given codeGen: CodeGenerator =
        CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)

    val TyProg(funcs, stmts) = prog

    funcs.map { func =>
        val label = codeGen.nextLabel(LabelType.Function(func.name))

        //initialize the function arguments
        codeGen.initFunctionArgs(label.name)

        func.params.zipWithIndex.foreach { case (param, i) =>
        val loc =   if (i < 6) then 
                        codeGen.functionRegs(i) 
                    else 
                        codeGen.nextTemp(getTypeSize(param.ty))
        val name = getLvalName(param)
        codeGen.allocateVar(name, loc)
        // append the location to the list of registers where arguments are stored
        codeGen.addFunctionArg(label.name, loc)
        }
    }

    funcs.map { func =>
        val label = codeGen.nextLabel(LabelType.Function(func.name))
        generate(label, func.params, func.stmts)
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
    
    //variable declarations or reassignments
    case Assignment(id: TyExpr.Id, expr: TyExpr) => 
        val rhs = generate(expr)
        val lhs = codeGen.getVar(id.value)
        codeGen.addInstr(Mov(lhs, rhs))
    
    
    // array reassignments only
    case Assignment(arrayElem: TyExpr.ArrayElem, expr: TyExpr) =>
        val rhs = generate(expr)
        val arrayName = getArrayName(arrayElem)
        val idx = getArrayIndex(arrayElem)

        val arrayPtr= codeGen.getVar(arrayName)

        // Get array type information
        val baseArrayType = getBaseArrayInfo(arrayElem)

        // Process all dimensions except last one to get to correct sub-array
        val resultReg = codeGen.nextTemp()
        codeGen.addInstr(Mov(resultReg, arrayPtr))
        
        idx.dropRight(1).foreach { indexExpr =>
            val idx = generate(indexExpr)
            codeGen.addInstr(Mov(R9(), resultReg))
            codeGen.addInstr(Mov(R10(DOUBLE_WORD), idx))
            codeGen.addInstr(Call(codeGen.getWidgetLabel(ArrayLoad8)))
            codeGen.addInstr(Mov(resultReg, RAX()))
        }
        
        // For final dimension, use ArrayStore
        val lastIdx = generate(arrayElem.idx.last)
        codeGen.addInstr(Mov(R9(), resultReg))  // Array base
        codeGen.addInstr(Mov(R10(DOUBLE_WORD), lastIdx)) // Index


        // Calculate remaining dimensions after this access
        val remainingDimensions = baseArrayType._2 - arrayElem.idx.length

        // If remainingDimensions > 0, we're returning an array pointer
        // Otherwise, we're returning an element
        val (finalSize, storeWidget) 
        = if (remainingDimensions > 0) {
                (QUAD_WORD, ArrayStore8)
            } else {
                val elemSize = getTypeSize(baseArrayType._1) 
                (elemSize, codeGen.getArrayElementStoreWidget(elemSize))
            }
        codeGen.addInstr(Mov(RAX(finalSize), rhs)) // Value
        codeGen.addInstr(Call(codeGen.getWidgetLabel(storeWidget)))

    
    // pair reassignments only
    case Assignment(pairElem: TyExpr.TyPairElem, expr: TyExpr) =>
        val rhs = generate(expr)
        
        //get the lval of the pairElem by deconstructing the pairElem
        val lval = getNestedPairLval(pairElem)
        
        val pairPtr = lval match {
            case id: TyExpr.Id => codeGen.getVar(getLvalName(id)) 
            case arrayElem: TyExpr.ArrayElem => generateArrayElem(arrayElem)
            case pairFst: TyExpr.PairFst => generateFstSnd(pairFst)
            case pairSnd: TyExpr.PairSnd => generateFstSnd(pairSnd)
        }

        // compare with 0 to check if pair is null
        codeGen.addInstr(Cmp(pairPtr, Imm(0)))
        codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrNull), CompFlag.E))

        val offset = pairElem match {
            case _: TyExpr.PairFst => 0
            case _: TyExpr.PairSnd => 8
        }

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
            val resultReg = codeGen.nextTemp(BYTE)
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
                    val temp = codeGen.nextTemp(DOUBLE_WORD)
                    Mul(temp, lhsLoc, rhsLoc)
                // TODO: remove this case
                case _ => Add(lhsLoc, rhsLoc)
            })
            codeGen.addInstr(Jump(codeGen.getWidgetLabel(ErrOverflow), JumpFlag.Overflow))
            lhsLoc

        case TyExpr.Not(expr) => 
            // TODO: temp should be 8-bit register
            val temp = generate(expr) 
            codeGen.addInstr(Cmp(temp, Imm(1)))
            codeGen.addInstr(SetComp(temp, CompFlag.NE))

            val resultReg = codeGen.nextTemp(BYTE)
            codeGen.addInstr(Mov(resultReg, temp))
            resultReg
        
        case TyExpr.Neg(expr) => {
            val temp = generate(expr)
            val resultReg = codeGen.nextTemp(DOUBLE_WORD)
            codeGen.addInstr(Mov(resultReg, Imm(0)))
            codeGen.addInstr(Sub(resultReg, temp))

            codeGen.addInstr(Jump(ErrOverflow.label, JumpFlag.Overflow))
            resultReg
        }
        case TyExpr.Len(expr) => {
            val temp = generate(expr)
            // TODO: ask darius if we need to check if the array is null
            val resultReg = codeGen.nextTemp(DOUBLE_WORD)
            codeGen.addInstr(Mov(resultReg, MemAccess(temp, -4)))
            resultReg
        }
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

        case TyExpr.Id(value, semTy)    => codeGen.getVar(value)
        case arrElem : TyExpr.ArrayElem => generateArrayElem(arrElem)
        case pairFst : TyExpr.PairFst   => generateFstSnd(pairFst)
        case pairSnd : TyExpr.PairSnd   => generateFstSnd(pairSnd)

        case TyExpr.ArrayLit(exprs, semTy)          => generateArrayLit(exprs, semTy)
        case TyExpr.NewPair(fst, snd, fstTy, sndTy) => generateNewPair(fst, snd, fstTy, sndTy)
        case TyExpr.Call(func, args, retTy, argTys) => {
            val funcLabel : Label = Label(s"wacc_${func}")
            val argsLoc: List[Register] = codeGen.getFunctionArgs(funcLabel.name)
            // move arguments into the temporary registers in the function to [TempReg] map
            args.zipWithIndex.foreach { case (arg, i) =>
                codeGen.addInstr(Mov(argsLoc(i), generate(arg)))
            }

            //TODO :check if stack is 16 byte aligned or do in second pass
            
            //call function
            codeGen.addInstr(Call(funcLabel))

            // get return type size
            val retSize = getTypeSize(retTy)

            //save return value
            val temp = codeGen.nextTemp(retSize)
            codeGen.addInstr(Mov(temp, RAX(retSize)))

            temp
        }

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
                  (using codeGen: CodeGenerator): Register = {
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

def generateNewPair(fst: TyExpr, snd: TyExpr, fstTy: SemType, sndTy: SemType)
                   (using codeGen: CodeGenerator): TempReg = {
    val fstTemp = generate(fst)
    val sndTemp = generate(snd)

    val pairPtr = codeGen.nextTemp()
    val pairSize = WORD  // 8 bytes for fst + 8 bytes for snd
    
    codeGen.addInstr(Mov(RDI(DOUBLE_WORD), Imm(pairSize)))
    codeGen.addInstr(Call(codeGen.getWidgetLabel(Malloc)))
    codeGen.addInstr(Mov(pairPtr, RAX()))

    codeGen.addInstr(Mov(MemAccess(pairPtr, 0), fstTemp))
    codeGen.addInstr(Mov(MemAccess(pairPtr, 8), sndTemp))

    pairPtr
}



def generateFstSnd(pairElem: TyExpr.TyPairElem)
                  (using codeGen: CodeGenerator): TempReg = {
    //we need to check if the PairElem is another PairFst/Snd or an Id
    //if it is an Id, we can continue if not we need to recursively call this function
    //to get the correct pairPtr

    val lval = getNestedPairLval(pairElem)

    val pairPtr = lval match {
        case id: TyExpr.Id => codeGen.getVar(getLvalName(id)) 
        case arrayElem: TyExpr.ArrayElem => generateArrayElem(arrayElem)
        case pairFst: TyExpr.PairFst => generateFstSnd(pairFst)
        case pairSnd: TyExpr.PairSnd => generateFstSnd(pairSnd)
    }

    // compare with 0 to check if pair is null
    codeGen.addInstr(Cmp(pairPtr, Imm(0)))
    codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrNull), CompFlag.E))


    val pairElemSize = pairElem match {
        case pairFst: TyExpr.PairFst => getTypeSize(pairFst.semTy) 
        case pairSnd: TyExpr.PairSnd => getTypeSize(pairSnd.semTy)
    }
    val resultReg = codeGen.nextTemp(pairElemSize)

    val offset = pairElem match {
        case _: TyExpr.PairFst => 0
        case _: TyExpr.PairSnd => 8
    }

    codeGen.addInstr(Mov(RAX(), MemAccess(pairPtr, offset)))

    codeGen.addInstr(Mov(resultReg, RAX(pairElemSize)))

    resultReg
}

def generateArrayLit(exprs: List[TyExpr], semTy: SemType)
                     (using codeGen: CodeGenerator): TempReg = {
    
    val elementSize = getTypeSize(semTy) / 8 // convert bits to bytes 
    val totalSize = 4 + elementSize * exprs.length

    //TODO: We can use r11 here or in second pass and then store it another register when we are done setting it up
    // because in the reference compiler it is used when adding elements to array

    val arrayPtr = codeGen.nextTemp()
    codeGen.addInstr(Mov(RDI(DOUBLE_WORD), Imm(totalSize)))
    codeGen.addInstr(Call(codeGen.getWidgetLabel(Malloc)))
    codeGen.addInstr(Mov(arrayPtr, RAX(DOUBLE_WORD)))

    // store size
    codeGen.addInstr(Add(arrayPtr, Imm(4)))
    codeGen.addInstr(Mov(MemAccess(arrayPtr, -4), Imm(exprs.length)))

    exprs.zipWithIndex.foreach { case (expr, i) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(MemAccess(arrayPtr, i * elementSize), temp))
    }

    arrayPtr
}

// assumes this is not a reassignment
def generateArrayElem(arrayElem: TyExpr.ArrayElem)
                     (using codeGen: CodeGenerator): TempReg = {
    val arrayName = getArrayName(arrayElem) 

    val arrayPtr = codeGen.getVar(arrayName) 

    val idx = getArrayIndex(arrayElem)

    // Get array type information
    val baseArrayType = getBaseArrayInfo(arrayElem)

    val resultReg = codeGen.nextTemp()
    codeGen.addInstr(Mov(resultReg, arrayPtr))

    // Process all dimensions except the last one
    idx.dropRight(1).foreach { indexExpr =>
        val idx = generate(indexExpr)
        codeGen.addInstr(Mov(R9(), resultReg))
        codeGen.addInstr(Mov(R10(DOUBLE_WORD), idx))
        codeGen.addInstr(Call(codeGen.getWidgetLabel(ArrayLoad8)))
        codeGen.addInstr(Mov(resultReg, RAX()))
    }

    // Handle final dimension
    val lastIdx = generate(idx.last)
    codeGen.addInstr(Mov(R9(), resultReg))
    codeGen.addInstr(Mov(R10(DOUBLE_WORD), lastIdx))

    // Calculate remaining dimensions after this access
    val remainingDimensions = baseArrayType._2 - idx.length

    // If remainingDimensions > 0, we're returning an array pointer
    // Otherwise, we're returning an element
    val (finalSize, loadWidget) = if (remainingDimensions > 0) {
        (QUAD_WORD, ArrayLoad8)
    } else {
        val elemSize = getTypeSize(baseArrayType._1) 
        (elemSize, codeGen.getArrayElementLoadWidget(elemSize))
    }

    codeGen.addInstr(Call(codeGen.getWidgetLabel(loadWidget)))

    val finalReg = codeGen.nextTemp(finalSize)
    codeGen.addInstr(Mov(finalReg, RAX(finalSize)))
    finalReg
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
    codeGen.addInstr(Cmp(lhsTemp, Imm(1)))
    codeGen.addInstr(JumpComp(label , compFlag))

    val rhsTemp = generate(rhs)
    codeGen.addInstr(Cmp(rhsTemp, Imm(1)))
    codeGen.addInstr(label)

    val resultReg = codeGen.nextTemp(BYTE)
    codeGen.addInstr(SetComp(resultReg, CompFlag.E))

    resultReg
}
