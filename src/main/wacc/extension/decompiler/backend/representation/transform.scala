package wacc.extension.decompiler

import representation.*

/** Holds information about the signatures of the functions. */
class Transformer(functionNumParams: Map[String, Int]) {
    def getNumParams(name: String): Int = if (functionNumParams.contains(name)) {
        functionNumParams(name)
    } else {
        0
    }
}

/** Transforms a list of functions into a structured program. */
def transform(functions: List[intermediate.Function]): Program = {
    given transformer: Transformer =
        Transformer(functions.map(func => func.name -> func.params.length).toMap)

    // find the main function of the representation
    val mainFunction = functions.find(_.name == "main").get

    // transform all the functions
    val funcs = functions.filter(_.name != "main").map(transform)
    // transform the main body
    val main = transform(mainFunction).stmts

    Program(funcs, main)
}

/** Transforms a function. */
def transform(function: intermediate.Function)
             (using transformer: Transformer): Func = {
    val intermediate.Function(name, params, block) = function

    // set the parameters of the function to default naming conventions
    Func((Unset, Id(name)), params.map(param => (Unset, Id(param))).toArray, block.map(transform))
}

/** Transforms an instruction to a statement. */
def transform(instr: intermediate.Instr)
             (using transformer: Transformer): Statement = instr match {
    case intermediate.Assignment(id, expr) =>
        Assignment(transform(id), transform(expr))
    case intermediate.Return(id) =>
        Return(transform(id))

    case intermediate.If(condition, thenStatements, elseStatements) =>
        val cond = transform(condition)
        val thenStmts = thenStatements.map(transform)
        val elseStmts = elseStatements.map(transform)
        If(cond, thenStmts, elseStmts)
    
    case intermediate.While(condition, doStatements) =>
        val cond = transform(condition)
        val doStmts = doStatements.map(transform)
        While(cond, doStmts)
}

/** Transforms an expression. */
def transform(expr: intermediate.ExprVar)
             (using transformer: Transformer): Expression = expr match {
    case i: Int if (i >= 'A'.toInt && i <= 'z'.toInt) => CharLit(i.toChar)
    case i: Int => IntLit(i)
    case id: String => Id(id)

    case intermediate.StrLiteral(value) => StrLit(value)
    case intermediate.Arithmetic(lhs, rhs, op) =>
        val lhsExpr = transform(lhs)
        val rhsExpr = transform(rhs)
        ParensExpression(op match {
            case intermediate.ArithmeticOp.Add => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.Add)
            case intermediate.ArithmeticOp.Sub => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.Sub)
            case intermediate.ArithmeticOp.Mul => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.Mul)
            case intermediate.ArithmeticOp.Div => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.Div)
            case intermediate.ArithmeticOp.Mod => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.Mod)
        })
    case intermediate.Comp(lhs, rhs, op) =>
        val lhsExpr = transform(lhs)
        val rhsExpr = transform(rhs)
        ParensExpression(op match {
            case intermediate.CompOp.Equal     => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.Equal)
            case intermediate.CompOp.NotEqual  => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.NotEqual)
            case intermediate.CompOp.Greater   => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.Greater)
            case intermediate.CompOp.GreaterEq => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.GreaterEq)
            case intermediate.CompOp.Less      => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.Less)
            case intermediate.CompOp.LessEq    => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.LessEq)
            case intermediate.CompOp.And       => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.And)
            case intermediate.CompOp.Or        => BinaryOp(lhsExpr, rhsExpr, BinaryOperation.Or)
        })
    case intermediate.Unary(expr, op) =>
        val transExpr = transform(expr)
        op match {
            case intermediate.UnaryOp.Not => UnaryOp(transExpr, UnaryOperation.Not)
            case intermediate.UnaryOp.Neg => UnaryOp(transExpr, UnaryOperation.Neg)
            case intermediate.UnaryOp.Len => UnaryOp(transExpr, UnaryOperation.Len)
            case intermediate.UnaryOp.Ord => UnaryOp(transExpr, UnaryOperation.Ord)
            case intermediate.UnaryOp.Chr => UnaryOp(transExpr, UnaryOperation.Chr)
        }

    // TODO: use the size information
    case intermediate.ArrayLit(exprs, elemSize) =>
        ArrayLit(exprs.map(transform))
    case intermediate.NewPair(fst, snd) =>
        NewPair(transform(fst), transform(snd))

    case intermediate.ArrayElem(id, expr) =>
        ArrayElem(Id(id), List(transform(expr)))
    case intermediate.Fst(expr) =>
        Fst(transform(expr))
    case intermediate.Snd(expr) =>
        Snd(transform(expr))

    case intermediate.FuncCall(name, params) =>
        // reduce the signature of a function to the number of parameters it actually uses
        Call(Id(name), params.take(transformer.getNumParams(name)).map(transform))
}
