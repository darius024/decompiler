package wacc.syntax

import parsley.generic.*
import bridges.*

object exprs {
    sealed trait Expr extends RValue
    case class Or(lhs: ExprAnd, rhs: Expr)(val pos: Position) extends Expr
    
    sealed trait ExprAnd extends Expr
    case class And(lhs: ExprEq, rhs: ExprAnd)(val pos: Position) extends ExprAnd
    
    sealed trait ExprEq extends ExprAnd
    case class Equal(lhs: ExprIneq, rhs: ExprIneq)(val pos: Position) extends ExprEq
    case class NotEqual(lhs: ExprIneq, rhs: ExprIneq)(val pos: Position) extends ExprEq

    sealed trait ExprIneq extends ExprEq
    case class Greater(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprIneq
    case class GreaterEqual(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprIneq
    case class Less(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprIneq
    case class LessThan(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprIneq

    sealed trait ExprAdd extends ExprIneq
    case class Add(lhs: ExprAdd, rhs: ExprMul)(val pos: Position) extends ExprAdd
    case class Sub(lhs: ExprAdd, rhs: ExprMul)(val pos: Position) extends ExprAdd

    sealed trait ExprMul extends ExprAdd
    case class Mul(lhs: ExprMul, rhs: ExprUnary)(val pos: Position) extends ExprMul
    case class Div(lhs: ExprMul, rhs: ExprUnary)(val pos: Position) extends ExprMul
    case class Mod(lhs: ExprMul, rhs: ExprUnary)(val pos: Position) extends ExprMul

    sealed trait ExprUnary extends ExprMul
    case class Not(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Neg(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Len(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Ord(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Chr(expr: ExprUnary)(val pos: Position) extends ExprUnary

    sealed trait Atom extends ExprUnary
    case class IntLit(value: Int)(val pos: Position) extends Atom
    case class BoolLit(value: Boolean)(val pos: Position) extends Atom
    case class CharLit(value: Char)(val pos: Position) extends Atom
    case class StrLit(value: String)(val pos: Position) extends Atom
    case object PairLit extends Atom with ParserBridge0[Atom]
    case class Id(value: String)(val pos: Position) extends Atom with LValue
    case class ArrayElem(id: Id, indices: List[Expr])(val pos: Position) extends Atom with LValue
    case class ParensExpr(expr: Expr)(val pos: Position) extends Atom

    sealed trait LValue
    sealed trait RValue
    case class ArrayLit(exprs: List[Expr])(val pos: Position) extends RValue
    case class NewPair(fst: Expr, snd: Expr)(val pos: Position) extends RValue
    case class Call(func: Id, args: List[Expr])(val pos: Position) extends RValue

    sealed trait PairElem extends LValue with RValue
    case class Fst(value: LValue)(val pos: Position) extends PairElem
    case class Snd(value: LValue)(val pos: Position) extends PairElem


    object Or extends ParserBridgePos2[ExprAnd, Expr, Expr]
    object And extends ParserBridgePos2[ExprEq, ExprAnd, ExprAnd]

    object Equal extends ParserBridgePos2[ExprIneq, ExprIneq, ExprEq]
    object NotEqual extends ParserBridgePos2[ExprIneq, ExprIneq, ExprEq]

    object Greater extends ParserBridgePos2[ExprAdd, ExprAdd, ExprIneq]
    object GreaterEqual extends ParserBridgePos2[ExprAdd, ExprAdd, ExprIneq]
    object Less extends ParserBridgePos2[ExprAdd, ExprAdd, ExprIneq]
    object LessThan extends ParserBridgePos2[ExprAdd, ExprAdd, ExprIneq]

    object Add extends ParserBridgePos2[ExprAdd, ExprMul, ExprAdd]
    object Sub extends ParserBridgePos2[ExprAdd, ExprMul, ExprAdd]

    object Mul extends ParserBridgePos2[ExprMul, ExprUnary, ExprMul]
    object Div extends ParserBridgePos2[ExprMul, ExprUnary, ExprMul]

    object Not extends ParserBridgePos1[ExprUnary, ExprUnary]
    object Neg extends ParserBridgePos1[ExprUnary, ExprUnary]
    object Len extends ParserBridgePos1[ExprUnary, ExprUnary]
    object Ord extends ParserBridgePos1[ExprUnary, ExprUnary]
    object Chr extends ParserBridgePos1[ExprUnary, ExprUnary]

    object IntLit extends ParserBridgePos1[Int, IntLit]
    object BoolLit extends ParserBridgePos1[Boolean, BoolLit]
    object CharLit extends ParserBridgePos1[Char, CharLit]
    object StrLit extends ParserBridgePos1[String, StrLit]
    object Id extends ParserBridgePos1[String, Id]
    object ArrayElem extends ParserBridgePos2[Id, List[Expr], ArrayElem]
    object ParensExpr extends ParserBridgePos1[Expr, ParensExpr]

    object ArrayLit extends ParserBridgePos1[List[Expr], ArrayLit]
    object NewPair extends ParserBridgePos2[Expr, Expr, NewPair]
    object Call extends ParserBridgePos2[Id, List[Expr], Call]

    object Fst extends ParserBridgePos1[LValue, Fst]
    object Snd extends ParserBridgePos1[LValue, Snd]
}
