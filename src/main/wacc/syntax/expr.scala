package wacc.syntax

import parsley.generic.*

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
    case object PairLit extends Atom with ParserBridge0[Atom]
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


    object Or extends ParserBridge2[ExprAnd, Expr, Expr]
    object And extends ParserBridge2[ExprEq, ExprAnd, ExprAnd]

    object Equal extends ParserBridge2[ExprIneq, ExprIneq, ExprEq]
    object NotEqual extends ParserBridge2[ExprIneq, ExprIneq, ExprEq]

    object Greater extends ParserBridge2[ExprAdd, ExprAdd, ExprIneq]
    object GreaterEqual extends ParserBridge2[ExprAdd, ExprAdd, ExprIneq]
    object Less extends ParserBridge2[ExprAdd, ExprAdd, ExprIneq]
    object LessThan extends ParserBridge2[ExprAdd, ExprAdd, ExprIneq]

    object Add extends ParserBridge2[ExprAdd, ExprMul, ExprAdd]
    object Sub extends ParserBridge2[ExprAdd, ExprMul, ExprAdd]

    object Mul extends ParserBridge2[ExprMul, ExprUnary, ExprMul]
    object Div extends ParserBridge2[ExprMul, ExprUnary, ExprMul]

    object Not extends ParserBridge1[ExprUnary, ExprUnary]
    object Neg extends ParserBridge1[ExprUnary, ExprUnary]
    object Len extends ParserBridge1[ExprUnary, ExprUnary]
    object Ord extends ParserBridge1[ExprUnary, ExprUnary]
    object Chr extends ParserBridge1[ExprUnary, ExprUnary]

    object IntLit extends ParserBridge1[Int, IntLit]
    object BoolLit extends ParserBridge1[Boolean, BoolLit]
    object CharLit extends ParserBridge1[Char, CharLit]
    object StrLit extends ParserBridge1[String, StrLit]
    object Id extends ParserBridge1[String, Id]
    object ArrayElem extends ParserBridge2[Id, List[Expr], ArrayElem]
    object ParensExpr extends ParserBridge1[Expr, ParensExpr]

    object ArrayLit extends ParserBridge1[List[Expr], ArrayLit]
    object NewPair extends ParserBridge2[Expr, Expr, NewPair]
    object Call extends ParserBridge2[Id, List[Expr], Call]

    object Fst extends ParserBridge1[LValue, Fst]
    object Snd extends ParserBridge1[LValue, Snd]
}
