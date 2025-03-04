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
        var size = constants.STACK_ADDR

        // map function parameters to registers according to calling convention
        params.zipWithIndex.foreach { case (param, ind) =>
            val paramSize = getTypeSize(param.semTy)
            if (ind < constants.MAX_CALL_ARGS) {
                // first 6 arguments go in registers
                val register = codeGen.registers(ind)
                codeGen.addVar(param.value, changeRegisterSize(register, paramSize))
            } else {
                // additional arguments go on the stack
                codeGen.addVar(param.value, MemAccess(RBP(), size, paramSize))
                size += paramSize.size
            }
        }

        generate(label, stmts)
    }

    // generate code for the main program
    generateMain(stmts)

    // perform register allocation in the second pass
    allocate(codeGen)
}

/**
 * Generates IR instructions for the main program.
 * Sets up the function prologue and epilogue.
 */
def generateMain(stmts: TyStmtList)
                (using codeGen: CodeGenerator): Unit = {    
    // function prologue
    codeGen.addInstr(codeGen.nextLabel(LabelType.Main))
    codeGen.addInstr(Push(RBP()))

    // generate code for the function body
    stmts.map(generate(_, false))
    
    // set exit code to success
    codeGen.addInstr(Mov(RAX(), Imm(constants.SUCCESS)))

    // function epilogue
    codeGen.addInstr(Pop(RBP()))
    codeGen.addInstr(Ret)
}

/**
 * Generates IR instructions for the main program.
 * Sets up the function prologue and epilogue.
 */
def generate(label: Label, stmts: TyStmtList)
            (using codeGen: CodeGenerator): Unit = {    
    // function prologue
    codeGen.addInstr(label)
    codeGen.addInstr(Push(RBP()))

    // generate code for the function body
    stmts.foreach { stmt =>
        generate(stmt)
    }
}

/**
 * Generates IR instructions for a statement.
 * Handles different statement types like assignments, conditionals, loops, etc.
 */
def generate(stmt: TyStmt, inFunction: Boolean = true)
            (using codeGen: CodeGenerator): Unit = stmt match {
    // simple variable assignment
    case Assignment(id: TyExpr.Id, expr: TyExpr) => 
        val rhs = generate(expr)
        val lhs = codeGen.getVar(id.value, rhs.size)
        val size = getTypeSize(id.ty)
        codeGen.addInstr(Mov(changeRegisterSize(lhs, size), changeRegisterSize(rhs, size)))
    
    // array element assignment
    case Assignment(TyExpr.ArrayElem(id, idx, semTy), expr: TyExpr) =>
        // compute the array index
        val temp = idx match {
            case expr :: Nil =>
                codeGen.addInstr(Mov(R9(), codeGen.getVar(id.value)))
                generate(expr)
            case _           =>
                generateArrayElem(id, idx.dropRight(1), semTy)
                generate(idx.last)
        }

        codeGen.addInstr(Mov(R10(RegSize.DOUBLE_WORD), temp))

        // compute the expression value
        val rhs = generate(expr)
        codeGen.addInstr(Mov(RAX(rhs.size), rhs))

        // store the value in the array
        val size = rhs.size
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
        codeGen.addInstr(Mov(MemAccess(pairPtr, offset, rhs.size), rhs))  

    // read input into a variable
    case Read(expr: TyExpr.LVal) =>
        if (inFunction) codeGen.registers.foreach { reg => codeGen.addInstr(Push(reg)) }
        val temp = generate(expr)
        codeGen.addInstr(Mov(RDI(temp.size), temp))

        // use the appropriate read widget based on the type
        val (widget, regSize) = expr.ty match {
            case KType.Int  => (ReadInt , RegSize.DOUBLE_WORD)
            case KType.Char => (ReadChar, RegSize.BYTE)
            // TODO: remove this case
            case _          => (ReadInt, RegSize.DOUBLE_WORD)
        }
        codeGen.addInstr(Call(codeGen.getWidgetLabel(widget)))
        codeGen.addInstr(Mov(generate(expr), RAX(regSize)))
        if (inFunction) codeGen.registers.reverse.foreach { reg => codeGen.addInstr(Pop(reg)) }
    
    // free heap-allocated memory
    case Free(expr: TyExpr) =>
        if (inFunction) codeGen.registers.foreach { reg => codeGen.addInstr(Push(reg)) }
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
                codeGen.addInstr(Mov(RDI(), temp))
                codeGen.addInstr(Call(codeGen.getWidgetLabel(FreePair)))
            case _ =>
                // for other types, use the generic free function
                codeGen.addInstr(Mov(RDI(), temp))
                codeGen.addInstr(Call(codeGen.getWidgetLabel(FreeProg)))
        }
        if (inFunction) codeGen.registers.reverse.foreach { reg => codeGen.addInstr(Pop(reg)) }
        
    // return from a function
    case Return(expr: TyExpr) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RAX(temp.size), temp))
        // function epilogue
        codeGen.addInstr(Pop(RBP()))
        codeGen.addInstr(Ret)

    // exit the program with a status code
    case Exit(expr: TyExpr) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(RDI(temp.size), temp))
        codeGen.addInstr(Call(codeGen.getWidgetLabel(ExitProg)))

    // print a value
    case Print(expr: TyExpr) =>
        if (inFunction) codeGen.registers.foreach { reg => codeGen.addInstr(Push(reg)) }
        val temp = generate(expr)
        codeGen.addInstr(Mov(RDI(temp.size), temp))

        // use the appropriate print widget based on the type
        val widget = getPrintWidget(expr.ty)
        codeGen.addInstr(Call(codeGen.getWidgetLabel(widget)))
        if (inFunction) codeGen.registers.reverse.foreach { reg => codeGen.addInstr(Pop(reg)) }

    // print a value followed by a newline
    case Println(expr: TyExpr) =>
        if (inFunction) codeGen.registers.foreach { reg => codeGen.addInstr(Push(reg)) }
        generate(Print(expr), false)
        codeGen.addInstr(Call(codeGen.getWidgetLabel(PrintLn)))
        if (inFunction) codeGen.registers.reverse.foreach { reg => codeGen.addInstr(Pop(reg)) }

    // if-then-else statement
    case If(cond: TyExpr, thenStmts: TyStmtList, elseStmts: TyStmtList) =>
        val ifLabel = codeGen.nextLabel(LabelType.If)
        val endIfLabel = codeGen.nextLabel(LabelType.IfEnd)

        // generate condition and jump to then branch if true
        generateCond(cond, ifLabel)
        // generate else branch
        elseStmts.map(generate(_, inFunction))
        codeGen.addInstr(Jump(endIfLabel, JumpFlag.Unconditional))
        // generate then branch
        codeGen.addInstr(ifLabel)
        thenStmts.map(generate(_, inFunction))
        codeGen.addInstr(endIfLabel)
    
    // while loop
    case While(cond: TyExpr, doStmts: TyStmtList) =>
        val whileBodyLabel = codeGen.nextLabel(LabelType.WhileBody)
        val whileCondLabel = codeGen.nextLabel(LabelType.WhileCond)

        // jump to condition check first
        codeGen.addInstr(Jump(whileCondLabel, JumpFlag.Unconditional))
        // generate loop body
        codeGen.addInstr(whileBodyLabel)
        doStmts.map(generate(_, inFunction))
        // generate condition check
        codeGen.addInstr(whileCondLabel)
        generateCond(cond, whileBodyLabel)
    
    // block of statements
    case Block(stmts: TyStmtList) =>
        stmts.map(generate(_, inFunction))
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
        val (lhsTemp, rhsTemp) = generateBinary(lhs, rhs)
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
        val (lhsTemp, rhsTemp) = generateBinary(lhs, rhs)

        // generate the appropriate arithmetic instruction
        codeGen.addInstr(op match {
            case TyExpr.OpArithmetic.Add => Add(lhsTemp, rhsTemp)
            case TyExpr.OpArithmetic.Sub => Sub(lhsTemp, rhsTemp)
            case TyExpr.OpArithmetic.Mul => Mul(lhsTemp, rhsTemp)
            // TODO: remove this case
            case _                       => Add(lhsTemp, rhsTemp)
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
        val resultReg = RAX(RegSize.DOUBLE_WORD)//codeGen.nextTemp(RegSize.DOUBLE_WORD)
        // the array length is stored 4 bytes before the array data
        codeGen.addInstr(Mov(resultReg, MemAccess(temp, memoryOffsets.ARRAY_LENGTH_OFFSET, RegSize.DOUBLE_WORD)))
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
        codeGen.addInstr(Mov(RSI(temp.size), temp))
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
        val temp = codeGen.nextTemp()
        codeGen.addInstr(Mov(temp, Imm(memoryOffsets.NULL)))
        temp

    // variable and memory access
    case TyExpr.Id(value, semTy)                => codeGen.getVar(value, getTypeSize(semTy))
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
        val (lhsTemp, rhsTemp) = generateBinary(lhs, rhs)
        codeGen.addInstr(Cmp(lhsTemp, rhsTemp))

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

    // Save the divisor for later use
    codeGen.addInstr(Push(changeRegisterSize(rhsTemp, RegSize.QUAD_WORD)))

    // compute the division
    val lhsTemp = generate(lhs)
    
    // Handle the special case of INT_MIN / -1 which causes overflow
    // Create a label to skip the special case check if not needed
    val skipOverflowLabel = codeGen.nextLabel(LabelType.AnyLabel)
    
    // Check if divisor is -1
    codeGen.addInstr(Cmp(rhsTemp, Imm(-1)))
    codeGen.addInstr(JumpComp(skipOverflowLabel, CompFlag.NE))
    
    // Check if dividend is INT_MIN (-2^31 for 32-bit integers)
    codeGen.addInstr(Cmp(lhsTemp, Imm(Int.MinValue)))
    codeGen.addInstr(JumpComp(codeGen.getWidgetLabel(ErrOverflow), CompFlag.E))
    
    codeGen.addInstr(skipOverflowLabel)
    
    codeGen.addInstr(Mov(RAX(RegSize.DOUBLE_WORD), lhsTemp))
    codeGen.addInstr(ConvertDoubleToQuad)

    codeGen.addInstr(Pop(RBX()))
    codeGen.addInstr(Div(RBX(RegSize.DOUBLE_WORD)))

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
    codeGen.addInstr(Mov(MemAccess(pairPtr, size = fstTemp.size), fstTemp))
    val sndTemp = generate(snd)
    codeGen.addInstr(Mov(MemAccess(pairPtr, RegSize.QUAD_WORD.size), changeRegisterSize(sndTemp, RegSize.QUAD_WORD)))

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
    val size = getTypeSize(pairElem.ty)
    val resultReg = codeGen.nextTemp(size)
    codeGen.addInstr(Mov(RAX(size), MemAccess(pairPtr, offset, size)))
    codeGen.addInstr(Mov(resultReg, RAX(size)))

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
    codeGen.addInstr(Mov(MemAccess(arrayPtr, -RegSize.DOUBLE_WORD.size, RegSize.DOUBLE_WORD), RAX(RegSize.DOUBLE_WORD)))

    // store the array elements
    exprs.zipWithIndex.foreach { case (expr, i) =>
        val temp = generate(expr)
        codeGen.addInstr(Mov(MemAccess(arrayPtr, i * elementSize.size, temp.size), temp))
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
    var arrayReg = codeGen.getVar(id.value)
    var arrayTy = semTy
    var elementSize = RegSize.QUAD_WORD

    val size = idx.length
    idx.zipWithIndex.foreach { (indexExpr, i) =>
        val index = generate(indexExpr)
        elementSize = if (i != size - 1) RegSize.QUAD_WORD else getTypeSize(arrayTy)
        codeGen.addInstr(Mov(R10(RegSize.DOUBLE_WORD), index))
        codeGen.addInstr(Mov(R9(), arrayReg))
        codeGen.addInstr(Call(codeGen.getWidgetLabel(getArrayElementLoadWidget(elementSize))))
        arrayTy = getArrayType(arrayTy)
        arrayReg = R9()
    }

    // get the result
    val resultReg = codeGen.nextTemp(elementSize)
    codeGen.addInstr(Mov(resultReg, R9(elementSize)))
    resultReg
}

/**
 * Generates code for function calls.
 * Handles parameter passing according to the x86-64 calling convention.
 */
def generateCall(func: String, args: List[TyExpr], retTy: SemType)
                (using codeGen: CodeGenerator): TempReg = {
    var size: Int = 0
    args.foreach { arg => size += getTypeSize(arg.ty).size }

    // save parameters on stack
    codeGen.addInstr(Sub(RSP(), Imm(size)))

    // pass arguments according to the calling convention
    args.zipWithIndex.foreach { case (arg, ind) =>
        val temp = generate(arg)
        val instr = if (ind < constants.MAX_CALL_ARGS) {
            // first 6 arguments go in registers
            Mov(changeRegisterSize(codeGen.registers(ind), temp.size), temp)
        } else {
            // additional arguments go on the stack
            val prevSize = size
            size += getTypeSize(arg.ty).size
            Mov(MemAccess(RSP(), prevSize, temp.size), temp)
        }
        codeGen.addInstr(instr)
    }
    
    // call the function
    codeGen.addInstr(Call(codeGen.nextLabel(LabelType.Function(func))))

    // get the return value from RAX
    val returnSize = getTypeSize(retTy)
    val temp = codeGen.nextTemp(returnSize)
    codeGen.addInstr(Mov(temp, RAX(returnSize)))

    // reset the stack pointer
    codeGen.addInstr(Add(RSP(), Imm(size)))

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

def generateBinary(lhs: TyExpr, rhs: TyExpr)
                  (using codeGen: CodeGenerator): (Register, Register) = {
    var (left, right) = (lhs, rhs)
    
    if (false) { // computeSize(lhs) < computeSize(rhs)) {
        left = rhs
        right = lhs
    }

    val leftReg = generate(left)
    codeGen.addInstr(Push(changeRegisterSize(leftReg, RegSize.QUAD_WORD)))
    val rightReg = generate(right)
    codeGen.addInstr(Push(changeRegisterSize(rightReg, RegSize.QUAD_WORD)))

    // TODO: do not hardcode this registers
    val leftTemp = RAX()
    val rightTemp = RBX()
    val size = getTypeSize(rhs.ty)

    codeGen.addInstr(Pop(rightTemp))
    codeGen.addInstr(Pop(leftTemp))

    (changeRegisterSize(leftTemp, size), changeRegisterSize(rightTemp, size))
}
