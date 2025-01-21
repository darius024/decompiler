package wacc.syntax

object expr {
    sealed trait Expr extends RValue
    case class Or(lhs: ExprAnd, rhs: Expr) extends Expr
    
    sealed trait ExprAnd extends Expr
    case class And(lhs: ExprEq, rhs: ExprAnd) extends ExprAnd
    
    sealed trait ExprEq extends ExprAnd
    case class Equal(lhs: ExprIneq, rhs: ExprIneq) extends ExprEq
    case class NotEqual(lhs: ExprIneq, rhs: ExprIneq) extends ExprEq

    sealed trait ExprIneq extends ExprEq
    case class Greater(lhs: ExprAdd, rhs: ExprAdd) extends ExprIneq
    case class GreaterEqual(lhs: ExprAdd, rhs: ExprAdd) extends ExprIneq
    case class Less(lhs: ExprAdd, rhs: ExprAdd) extends ExprIneq
    case class LessThan(lhs: ExprAdd, rhs: ExprAdd) extends ExprIneq

    sealed trait ExprAdd extends ExprIneq
    case class Add(lhs: ExprAdd, rhs: ExprMul) extends ExprAdd
    case class Sub(lhs: ExprAdd, rhs: ExprMul) extends ExprAdd

    sealed trait ExprMul extends ExprAdd
    case class Mul(lhs: ExprMul, rhs: ExprUnary) extends ExprMul
    case class Div(lhs: ExprMul, rhs: ExprUnary) extends ExprMul
    case class Mod(lhs: ExprMul, rhs: ExprUnary) extends ExprMul

    sealed trait ExprUnary extends ExprMul
    case class Not(expr: ExprUnary) extends ExprUnary
    case class Neg(expr: ExprUnary) extends ExprUnary
    case class Len(expr: ExprUnary) extends ExprUnary
    case class Ord(expr: ExprUnary) extends ExprUnary
    case class Chr(expr: ExprUnary) extends ExprUnary

    sealed trait Atom extends ExprUnary
    case class IntLit(value: Int) extends Atom
    case class BoolLit(value: Boolean) extends Atom
    case class CharLit(value: Char) extends Atom
    case class StrLit(value: String) extends Atom
    case object PairLit extends Atom
    case class Id(value: String) extends Atom with LValue
    case class ArrayElem(id: Id, indices: List[Expr]) extends Atom with LValue
    case class ParensExpr(expr: Expr) extends Atom

    sealed trait LValue
    sealed trait RValue
    case class ArrayLit(exprs: List[Expr]) extends RValue
    case class NewPair(fst: Expr, snd: Expr) extends RValue
    case class Call(func: Id, args: List[Expr]) extends RValue

    sealed trait PairElem extends LValue with RValue
    case class Fst(value: LValue) extends PairElem
    case class Snd(value: LValue) extends PairElem
}
