package wacc.backend.generator

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

/**
 * First pass of code generation.
 * 
 * Translates the typed AST into intermediate representation (IR) instructions.
 * Uses temporary registers to represent values, which will be allocated to
 * physical registers in the second pass.
 */
def generate(prog: TyProg): CodeGenerator = {
    given codeGen: CodeGenerator =
        CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)

    val TyProg(funcs, stmts) = prog

    // generate code for each function
    funcs.map { case TyFunc(name, params, stmts) =>
        val label = codeGen.nextLabel(LabelType.Function(name))

        // map function parameters to registers according to calling convention
        params.zip(codeGen.registers).foreach { case (param, reg) =>
            codeGen.addVar(param.value, reg)
        }

        generate(label, stmts)
    }

    // generate code for the main program
    generate(codeGen.nextLabel(LabelType.Main), stmts)

    // perform register allocation in the second pass
    allocate(codeGen)
}

/**
 * Generates IR instructions for a function or the main program.
 * Sets up the function prologue and epilogue.
 */
def generate(label: Label, stmts: TyStmtList)
            (using codeGen: CodeGenerator): Unit = {    
    // function prologue
    codeGen.addInstr(label)
    codeGen.addInstr(Push(RBP()))

    // generate code for the function body
    stmts.map(generate)
    
    // for main, set return value to 0
    if (label.name == "main") {
        codeGen.addInstr(Mov(RAX(), Imm(0)))
    }

    // function epilogue
    codeGen.addInstr(Pop(RBP()))
    codeGen.addInstr(Ret)
}

/**
 * Generates IR instructions for a statement.
 * Handles different statement types like assignments, conditionals, loops, etc.
 */
def generate(stmt: TyStmt)
            (using codeGen: CodeGenerator): Unit = stmt match {
    // simple variable assignment
    case Assignment(id: TyExpr.Id, expr: TyExpr) => 
        val rhs = generate(expr)
        val lhs = codeGen.getVar(id.value)
        codeGen.addInstr(Mov(lhs, rhs))
    
    // array element assignment
    case Assignment(TyExpr.ArrayElem(id, idx, semTy), expr: TyExpr) =>
        // compute the array index
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

        // compute the expression value
        val rhs = generate(expr)
        rhs.size = getTypeSize(expr.ty)
        codeGen.addInstr(Mov(RAX(rhs.size), rhs))

        // store the value in the array
        codeGen.addInstr(Mov(R9(), codeGen.getVar(id.value)))
        codeGen.addInstr(Call(codeGen.getWidgetLabel(getArrayElementStoreWidget(size))))
    
    // pair element assignment
    case Assignment(pairElem: TyExpr.TyPairElem, expr: TyExpr) =>
        // get the pair pointer
        val pairPtr = generate(pairElem.lval)

        // check for null pointer dereference
        codeGen.addInstr(Cmp(pairPtr, Imm(memoryOffsets.NULL)))
        codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrNull), CompFlag.E))

        // determine which element to access (first or second)
        val offset = pairElem match {
            case _: TyExpr.PairFst => memoryOffsets.NO_OFFSET
            case _: TyExpr.PairSnd => RegSize.QUAD_WORD.size
        }

        // compute the expression and store it
        val rhs = generate(expr)
        codeGen.addInstr(Mov(MemAccess(pairPtr, offset), rhs))  

    // read input into a variable
    case Read(expr: TyExpr.LVal) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RDI(), temp))

        // use the appropriate read widget based on the type
        val (widget, regSize) = expr.ty match {
            case KType.Int  => (ReadInt , RegSize.DOUBLE_WORD)
            case KType.Char => (ReadChar, RegSize.BYTE)
            // TODO: remove this case
            case _          => (ReadInt, RegSize.DOUBLE_WORD)
        }
        codeGen.addInstr(Call(codeGen.getWidgetLabel(widget)))
        codeGen.addInstr(Mov(generate(expr), RAX(regSize)))
    
    // free heap-allocated memory
    case Free(expr: TyExpr) =>
        val temp = generate(expr)
        // handle different types of pointers
        expr.ty match {
            case KType.Array(_, _) =>
                // for arrays, adjust the pointer to include the length field
                codeGen.addInstr(Mov(RDI(), temp))
                codeGen.addInstr(Sub(RDI(), Imm(memoryOffsets.ARRAY_LENGTH_OFFSET)))
                codeGen.addInstr(Call(codeGen.getWidgetLabel(FreeProg)))
            case KType.Pair(_, _) =>
                // for pairs, check for null and use the pair-specific free function
                codeGen.addInstr(Cmp(temp, Imm(0)))
                codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrNull), CompFlag.E))
                codeGen.addInstr(Mov(RDI(), temp))
                codeGen.addInstr(Call(codeGen.getWidgetLabel(FreePair)))
            case _ =>
                // for other types, use the generic free function
                codeGen.addInstr(Mov(RDI(), temp))
                codeGen.addInstr(Call(codeGen.getWidgetLabel(FreeProg)))
        }

    // return from a function
    case Return(expr: TyExpr) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RAX(), temp))
        codeGen.addInstr(Ret)

    // exit the program with a status code
    case Exit(expr: TyExpr) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RDI(), temp))
        codeGen.addInstr(Call(codeGen.getWidgetLabel(ExitProg)))

    // print a value
    case Print(expr: TyExpr) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RDI(), temp))

        // use the appropriate print widget based on the type
        val widget = getPrintWidget(expr.ty)
        codeGen.addInstr(Call(codeGen.getWidgetLabel(widget)))

    // print a value followed by a newline
    case Println(expr: TyExpr) =>
        generate(Print(expr))
        codeGen.addInstr(Call(codeGen.getWidgetLabel(PrintLn)))

    // if-then-else statement
    case If(cond: TyExpr, thenStmts: TyStmtList, elseStmts: TyStmtList) =>
        val ifLabel = codeGen.nextLabel(LabelType.If)
        val endIfLabel = codeGen.nextLabel(LabelType.IfEnd)

        // generate condition and jump to then branch if true
        generateCond(cond, ifLabel)
        // generate else branch
        elseStmts.map(generate)
        codeGen.addInstr(Jump(endIfLabel, JumpFlag.Unconditional))
        // generate then branch
        codeGen.addInstr(ifLabel)
        thenStmts.map(generate)
        codeGen.addInstr(endIfLabel)
    
    // while loop
    case While(cond: TyExpr, doStmts: TyStmtList) =>
        val whileBodyLabel = codeGen.nextLabel(LabelType.WhileBody)
        val whileCondLabel = codeGen.nextLabel(LabelType.WhileCond)

        // jump to condition check first
        codeGen.addInstr(Jump(whileCondLabel, JumpFlag.Unconditional))
        // generate loop body
        codeGen.addInstr(whileBodyLabel)
        doStmts.map(generate)
        // generate condition check
        codeGen.addInstr(whileCondLabel)
        generateCond(cond, whileBodyLabel)
    
    // block of statements
    case Block(stmts: TyStmtList) =>
        stmts.map(generate)
}

/**
 * Generates IR instructions for expressions.
 * Returns the register containing the result of the expression.
 */
def generate(expr: TyExpr)
            (using codeGen: CodeGenerator): Register = expr match {
    // boolean binary operations with short-circuit evaluation
    case exp: TyExpr.BinaryBool =>
        shortCircuit(exp)

    // comparison operations
    case TyExpr.BinaryComp(lhs, rhs, op) =>
        val lhsTemp = generate(lhs)
        val rhsTemp = generate(rhs)
        codeGen.addInstr(Cmp(lhsTemp, rhsTemp))

        val compFlag = convertToJump(op)
        val resultReg = codeGen.nextTemp(RegSize.BYTE)
        codeGen.addInstr(SetComp(resultReg, compFlag))

        resultReg
    
    // division operation
    case exp @ TyExpr.BinaryArithmetic(lhs, rhs, TyExpr.OpArithmetic.Div) =>
        generateDivMod(exp)
    
    // modulo operation
    case exp @ TyExpr.BinaryArithmetic(lhs, rhs, TyExpr.OpArithmetic.Mod) =>
        generateDivMod(exp)

    // other arithmetic operations
    case TyExpr.BinaryArithmetic(lhs, rhs, op) =>
        val lhsTemp = generate(lhs)
        val rhsTemp = generate(rhs)

        // generate the appropriate arithmetic instruction
        codeGen.addInstr(op match {
            case TyExpr.OpArithmetic.Add => Add(lhsTemp, rhsTemp)
            case TyExpr.OpArithmetic.Sub => Sub(lhsTemp, rhsTemp)
            case TyExpr.OpArithmetic.Mul =>
                val temp = codeGen.nextTemp(RegSize.DOUBLE_WORD)
                Mul(temp, lhsTemp, rhsTemp)
            case _ => Add(lhsTemp, rhsTemp)
        })
        // check for overflow
        codeGen.addInstr(Jump(codeGen.getWidgetLabel(ErrOverflow), JumpFlag.Overflow))
        lhsTemp

    // boolean NOT operation
    case TyExpr.Not(expr) => 
        val temp = generate(expr)
        codeGen.addInstr(Cmp(changeRegisterSize(temp, RegSize.BYTE), Imm(memoryOffsets.TRUE)))
        codeGen.addInstr(SetComp(RAX(RegSize.BYTE), CompFlag.NE))

        val resultReg = codeGen.nextTemp(RegSize.BYTE)
        codeGen.addInstr(Mov(resultReg, RAX(RegSize.BYTE)))
        resultReg
    
    // numeric negation
    case TyExpr.Neg(expr) =>
        val temp = generate(expr)
        val resultReg = codeGen.nextTemp(RegSize.DOUBLE_WORD)
        codeGen.addInstr(Mov(resultReg, Imm(memoryOffsets.NO_OFFSET)))
        codeGen.addInstr(Sub(resultReg, temp))

        // check for overflow
        codeGen.addInstr(Jump(codeGen.getWidgetLabel(ErrOverflow), JumpFlag.Overflow))
        resultReg
    
    // array length operation
    case TyExpr.Len(expr) => 
        val temp = generate(expr)
        val resultReg = codeGen.nextTemp(RegSize.DOUBLE_WORD)
        // the array length is stored 4 bytes before the array data
        codeGen.addInstr(Mov(resultReg, MemAccess(temp, memoryOffsets.ARRAY_LENGTH_OFFSET)))
        resultReg
    
    // character to integer conversion
    case TyExpr.Ord(expr) =>
        val temp = generate(expr)
        val resultReg = codeGen.nextTemp(RegSize.DOUBLE_WORD)
        codeGen.addInstr(Mov(resultReg, temp))
        resultReg
    
    // integer to character conversion
    case TyExpr.Chr(expr) =>
        val temp = generate(expr)
        // check if the value is in the valid character range
        codeGen.addInstr(Test(temp, Imm(constants.CHR)))
        codeGen.addInstr(Mov(RSI(), temp))
        codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrBadChar), CompFlag.NE))
        temp

    // literal values
    case TyExpr.IntLit(value)  =>
        val temp = codeGen.nextTemp(RegSize.DOUBLE_WORD)
        codeGen.addInstr(Mov(temp, Imm(value)))
        temp
    case TyExpr.BoolLit(value) =>
        val temp = codeGen.nextTemp(RegSize.BYTE)
        codeGen.addInstr(Mov(temp, Imm(if (value) memoryOffsets.TRUE else memoryOffsets.FALSE)))
        temp
    case TyExpr.CharLit(value) =>
        val temp = codeGen.nextTemp(RegSize.BYTE)
        codeGen.addInstr(Mov(temp, Imm(value.toInt)))
        temp
    case TyExpr.StrLit(value)  =>
        val label = codeGen.nextLabel(LabelType.Str)
        codeGen.addStrLabel(StrLabel(label, value))

        val temp = codeGen.nextTemp()
        codeGen.addInstr(Lea(temp, MemAccess(RIP(), label)))
        temp
    case TyExpr.PairLit        =>
        val temp = codeGen.nextTemp(RegSize.DOUBLE_WORD)
        codeGen.addInstr(Mov(temp, Imm(memoryOffsets.NULL)))
        temp

    // variable and memory access
    case TyExpr.Id(value, semTy)                => codeGen.getVar(value)
    case TyExpr.ArrayElem(id, idx, semTy)       => generateArrayElem(id, idx, semTy)
    case pairFst: TyExpr.PairFst                => generateFstSnd(pairFst)
    case pairSnd: TyExpr.PairSnd                => generateFstSnd(pairSnd)

    // compound expressions
    case TyExpr.ArrayLit(exprs, semTy)          => generateArrayLit(exprs, semTy)
    case TyExpr.NewPair(fst, snd, fstTy, sndTy) => generateNewPair(fst, snd, fstTy, sndTy)
    case TyExpr.Call(func, args, retTy, _)      => generateCall(func, args, retTy)
}

/**
 * Generates code for conditional expressions in if and while statements.
 * Jumps to the provided label if the condition is true.
 */
def generateCond(expr: TyExpr, label: Label)
                (using codeGen: CodeGenerator): Unit = expr match {
    // comparison operations
    case TyExpr.BinaryComp(lhs, rhs, op) => {
        val lhsTemp = generate(lhs)
        val rhsTemp = generate(rhs)

        // TODO: use immediates for ints, chars and bools

        val compareSize = getTypeSize(lhs.ty)

        codeGen.addInstr(Cmp(changeRegisterSize(lhsTemp, compareSize), changeRegisterSize(rhsTemp, compareSize)))

        val compType = convertToJump(op)
        codeGen.addInstr(JumpComp(label, compType))
    }
    // boolean literals and expressions
    case tyExpr if tyExpr.ty == KType.Bool => {
        val temp = generate(expr)
        codeGen.addInstr(Cmp(changeRegisterSize(temp, RegSize.BYTE), Imm(memoryOffsets.TRUE)))
        codeGen.addInstr(JumpComp(label, CompFlag.E))
    }
    case _ =>
}

/**
 * Generates code for division and modulo operations.
 * Handles division by zero checks and sign extension.
 */
def generateDivMod(expr: TyExpr.BinaryArithmetic)
                  (using codeGen: CodeGenerator): Register = {
    val TyExpr.BinaryArithmetic(lhs, rhs, op) = expr

    // check for division by zero
    val rhsTemp = generate(rhs) 
    codeGen.addInstr(Cmp(rhsTemp, Imm(memoryOffsets.DIV_ZERO)))
    codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrDivZero), CompFlag.E))

    // compute the division
    val lhsTemp = generate(lhs) 
    codeGen.addInstr(Mov(RAX(), lhsTemp))
    codeGen.addInstr(ConvertDoubleToQuad)
    codeGen.addInstr(Div(rhsTemp))

    // select the appropriate result (quotient or remainder)
    val resultReg = codeGen.nextTemp(RegSize.DOUBLE_WORD)
    val res = op match {
        case TyExpr.OpArithmetic.Div => RAX(RegSize.DOUBLE_WORD)
        case _                       => RDX(RegSize.DOUBLE_WORD)
    }
    codeGen.addInstr(Mov(resultReg, res))

    resultReg
}

/**
 * Generates code for creating a new pair.
 * Allocates memory and initializes the pair elements.
 */
def generateNewPair(fst: TyExpr, snd: TyExpr, fstTy: SemType, sndTy: SemType)
                   (using codeGen: CodeGenerator): TempReg = {
    // a pair consists of two 8-byte pointers
    val pairSize = RegSize.QUAD_WORD.size + RegSize.QUAD_WORD.size
    
    // allocate memory for the pair
    codeGen.addInstr(Mov(RDI(RegSize.DOUBLE_WORD), Imm(pairSize)))
    codeGen.addInstr(Call(codeGen.getWidgetLabel(Malloc)))

    // save the pair pointer
    val pairPtr = R11()
    codeGen.addInstr(Mov(pairPtr, RAX()))

    // store the first and second elements
    val fstTemp = generate(fst)
    codeGen.addInstr(Mov(MemAccess(pairPtr), fstTemp))
    val sndTemp = generate(snd)
    codeGen.addInstr(Mov(MemAccess(pairPtr, RegSize.QUAD_WORD.size), sndTemp))

    val temp = codeGen.nextTemp()
    codeGen.addInstr(Mov(temp, pairPtr))
    temp
}

/**
 * Generates code for accessing pair elements (first or second).
 * Handles null pointer checks.
 */
def generateFstSnd(pairElem: TyExpr.TyPairElem)
                  (using codeGen: CodeGenerator): TempReg = {
    // get the pair pointer
    val pairPtr = generate(pairElem.lval)

    // check for null pointer dereference
    codeGen.addInstr(Cmp(pairPtr, Imm(memoryOffsets.NULL)))
    codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrNull), CompFlag.E))

    // determine which element to access
    val offset = pairElem match {
        case _: TyExpr.PairFst => memoryOffsets.NO_OFFSET
        case _: TyExpr.PairSnd => RegSize.QUAD_WORD.size
    }

    // load the element
    val resultReg = codeGen.nextTemp()
    codeGen.addInstr(Mov(RAX(), MemAccess(pairPtr, offset)))
    codeGen.addInstr(Mov(resultReg, RAX(getTypeSize(pairElem.ty))))

    resultReg
}

/**
 * Generates code for array literals.
 * Allocates memory for the array and initializes its elements.
 */
def generateArrayLit(exprs: List[TyExpr], semTy: SemType)
                     (using codeGen: CodeGenerator): TempReg = {
    // compute the size of the array
    val elementSize = getTypeSize(getArrayType(semTy))
    val exprsLength = exprs.length
    // the array layout has a 4-byte length field followed by the elements
    val totalSize = RegSize.DOUBLE_WORD.size + elementSize.size * exprsLength

    // allocate memory for the array
    codeGen.addInstr(Mov(RDI(RegSize.DOUBLE_WORD), Imm(totalSize)))
    codeGen.addInstr(Call(codeGen.getWidgetLabel(Malloc)))

    // use specific register for array operations
    val arrayPtr = R11()
    codeGen.addInstr(Mov(arrayPtr, RAX()))

    // store the array length
    codeGen.addInstr(Add(arrayPtr, Imm(RegSize.DOUBLE_WORD.size)))
    codeGen.addInstr(Mov(RAX(RegSize.DOUBLE_WORD), Imm(exprsLength)))
    codeGen.addInstr(Mov(MemAccess(arrayPtr, -RegSize.DOUBLE_WORD.size), RAX(RegSize.DOUBLE_WORD)))

    // store the array elements
    exprs.zipWithIndex.foreach { case (expr, i) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(MemAccess(arrayPtr, i * elementSize.size), temp))
    }

    val temp = codeGen.nextTemp()
    codeGen.addInstr(Mov(temp, arrayPtr))
    temp
}

/**
 * Generates code for array element access.
 * Handles bounds checking and multi-dimensional arrays.
 */
def generateArrayElem(id: TyExpr.Id, idx: List[TyExpr], semTy: SemType)
                     (using codeGen: CodeGenerator): TempReg = {
    // handle nested array access
    val (temp, size) = idx match {
        case expr1 :: expr2 :: rest  =>
            // for multi-dimensional arrays, recursively access inner arrays
            (generateArrayElem(id, expr2 :: rest, semTy), RegSize.QUAD_WORD)
        case expr :: Nil             =>
            // for single-dimensional arrays, compute the index
            (generate(expr), getTypeSize(getArrayType(semTy)))
            
        // TODO: remove this case
        case Nil                     =>
            (TempReg(0), RegSize.QUAD_WORD)
    }

    // set up parameters for the array load widget
    codeGen.addInstr(Mov(R10(size), temp))
    codeGen.addInstr(Mov(R9(), codeGen.getVar(id.value)))
    codeGen.addInstr(Call(codeGen.getWidgetLabel(getArrayElementLoadWidget(size))))

    // get the result
    val resultReg = codeGen.nextTemp(size)
    codeGen.addInstr(Mov(resultReg, R9(size)))

    resultReg
}

/**
 * Generates code for function calls.
 * Handles parameter passing according to the x86-64 calling convention.
 */
def generateCall(func: String, args: List[TyExpr], retTy: SemType)
                (using codeGen: CodeGenerator): TempReg = {
    // pass arguments according to the calling convention
    args.zipWithIndex.foreach { case (arg, ind) =>
        val instr = if (ind < constants.MAX_CALL_ARGS) {
            // first 6 arguments go in registers
            Mov(codeGen.registers(ind), generate(arg))
        } else {
            // additional arguments go on the stack
            Push(generate(arg))
        }
        codeGen.addInstr(instr)
    }
    
    // call the function
    codeGen.addInstr(Call(codeGen.nextLabel(LabelType.Function(func))))

    // get the return value from RAX
    val returnSize = getTypeSize(retTy)
    val temp = codeGen.nextTemp(returnSize)
    codeGen.addInstr(Mov(temp, RAX(returnSize)))

    temp
}

/**
 * Generates code for short-circuit evaluation of boolean expressions.
 * For AND, the second operand is only evaluated if the first is true.
 * For OR, the second operand is only evaluated if the first is false.
 */
def shortCircuit(expr: TyExpr.BinaryBool)
                (using codeGen: CodeGenerator): TempReg = {
    val TyExpr.BinaryBool(lhs, rhs, op) = expr
    val compFlag = if (op == TyExpr.OpBool.And) then CompFlag.NE else CompFlag.E

    // evaluate the left-hand side
    val label = codeGen.nextLabel(LabelType.AnyLabel)
    val lhsTemp = generate(lhs)
    codeGen.addInstr(Cmp(changeRegisterSize(lhsTemp, RegSize.BYTE), Imm(memoryOffsets.TRUE)))

    // skip the right-hand side if the result is already determined
    codeGen.addInstr(JumpComp(label, compFlag))

    // evaluate the right-hand side if needed
    val rhsTemp = generate(rhs)
    codeGen.addInstr(Cmp(changeRegisterSize(rhsTemp, RegSize.BYTE), Imm(memoryOffsets.TRUE)))
    codeGen.addInstr(label)

    // set the result based on the comparison
    val resultReg = codeGen.nextTemp(RegSize.BYTE)
    codeGen.addInstr(SetComp(resultReg, CompFlag.E))

    resultReg
}
