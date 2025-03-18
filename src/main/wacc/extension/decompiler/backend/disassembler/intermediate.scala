package wacc.extension.decompiler

import wacc.semantics.scoping.semanticTypes.*

/** Intermediate representation of the decompiler.
  * 
  * Represents the main control flow structures: functions, conditionals, loops.
  * Mimics a three-address code, while also finding patterns in assembly.
  */
object intermediate {
    type ExprVar = Expr | String | Int
    type InstrList = List[Instr]

    sealed trait Expr
    case class Arithmetic(lhs: ExprVar, rhs: ExprVar, op: ArithmeticOp) extends Expr
    case class Comp(lhs: ExprVar, rhs: ExprVar, op: CompOp) extends Expr
    case class Unary(expr: ExprVar, op: UnaryOp) extends Expr

    case class FuncCall(name: String, params: List[String]) extends Expr
    case class StrLiteral(value: String) extends Expr
    case class ArrayLit(exprs: List[ExprVar], elemSize: Int) extends Expr
    case class ArrayElem(id: String, expr: ExprVar) extends Expr
    case class NewPair(fst: ExprVar, snd: ExprVar) extends Expr
    case class Fst(expr: ExprVar) extends Expr
    case class Snd(expr: ExprVar) extends Expr

    enum CompOp {
        case Equal
        case NotEqual
        case Greater
        case GreaterEq
        case Less
        case LessEq
        case And
        case Or
    }

    enum ArithmeticOp {
        case Add
        case Sub
        case Mul
        case Div
        case Mod
    }

    enum UnaryOp {
        case Not
        case Neg
        case Len
        case Ord
        case Chr
    }

    sealed trait Instr
    case class Assignment(id: ExprVar, expr: ExprVar) extends Instr
    case class Return(id: String) extends Instr
    case class If(condition: ExprVar, thenStatements: InstrList, elseStatements: InstrList) extends Instr
    case class While(condition: ExprVar, doStatements: InstrList) extends Instr

    case class Function(name: String, params: List[String], block: InstrList)
}
