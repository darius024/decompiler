package wacc.syntax

import parsley.generic.*
import bridges.*

/** Expression Nodes of the Abstract Syntax Tree.
  * 
  * Implements the expressions that the WACC language supports.
  * The structure of the nodes mimics the grammar rules for expressions.
  * It also embodies information about the precedence and fixity of the operators.
  * The top-down approach allows any trait to replace any trait that is defined above it.
  */
object exprs {
    // ========== Expressions ==========
    // <expr> ::= <unary-oper> <expr> | <expr> <binary-oper> <expr> | <atom> 
    sealed trait Expr extends RValue
    
    // ========== Logical OR (||) ==========
    // <expr> :: <expr-and> '||' <expr> | <expr-and>
    case class Or(lhs: ExprAnd, rhs: Expr)(val pos: Position) extends Expr
    
    // ========== Logical AND (&&) ==========
    // <expr-and> ::= <expr-eq> '&&' <expr-and> | <expr-eq>  
    sealed trait ExprAnd extends Expr
    case class And(lhs: ExprEq, rhs: ExprAnd)(val pos: Position) extends ExprAnd
    
    // ========== Equality/Inequality (==, !=) ==========
    // <expr-eq> ::= <expr-rel> ('==' | '!=') <expr-rel> | <expr-rel>
    sealed trait ExprEq extends ExprAnd
    case class Equal(lhs: ExprRel, rhs: ExprRel)(val pos: Position) extends ExprEq
    case class NotEqual(lhs: ExprRel, rhs: ExprRel)(val pos: Position) extends ExprEq

    // ========== Relational comparisons (>, >=, <, <=) ==========
    // <expr-rel> ::= <expr-add> ('>' | '>=' | '<' | '<=') <expr-add> | <expr-add>
    sealed trait ExprRel extends ExprEq
    case class Greater(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprRel
    case class GreaterEqual(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprRel
    case class Less(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprRel
    case class LessThan(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprRel

    // ========== Additive ==========
    // <expr-add> ::= <expr-add> ('+' | '-') <expr-mul> | <expr-mul>
    sealed trait ExprAdd extends ExprRel
    case class Add(lhs: ExprAdd, rhs: ExprMul)(val pos: Position) extends ExprAdd
    case class Sub(lhs: ExprAdd, rhs: ExprMul)(val pos: Position) extends ExprAdd

    // ========== Multiplicative ==========
    // <expr-mul> ::= <expr-mul> ('*' | '/' | '%') <expr-unary> | <expr-unary>
    sealed trait ExprMul extends ExprAdd
    case class Mul(lhs: ExprMul, rhs: ExprUnary)(val pos: Position) extends ExprMul
    case class Div(lhs: ExprMul, rhs: ExprUnary)(val pos: Position) extends ExprMul
    case class Mod(lhs: ExprMul, rhs: ExprUnary)(val pos: Position) extends ExprMul

    // ========== Unary (highest precedence) ==========
    // <expr-unary> ::= ('!' | '-' | 'len' | 'ord' | 'chr') <atom> | <atom>
    sealed trait ExprUnary extends ExprMul
    case class Not(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Neg(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Len(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Ord(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Chr(expr: ExprUnary)(val pos: Position) extends ExprUnary

    // ========== Atoms ==========
    // <atom> ::= <int-liter> | <bool-liter> | <char-liter> | <str-liter> | <pair-liter>
    //          | <ident> | <array-elem> | '(' <expr> ')'
    sealed trait Atom extends ExprUnary
    case class IntLit(value: Int)(val pos: Position) extends Atom
    case class BoolLit(value: Boolean)(val pos: Position) extends Atom
    case class CharLit(value: Char)(val pos: Position) extends Atom
    case class StrLit(value: String)(val pos: Position) extends Atom
    case object PairLit extends Atom with ParserBridge0[Atom]
    case class Id(value: String)(val pos: Position) extends Atom with LValue
    case class ArrayElem(id: Id, indices: List[Expr])(val pos: Position) extends Atom with LValue
    // optimisation reduce backtracking for ArrayElem and Ident
    case class IdOrArrayElem(id: Id, indices: List[Expr])(val pos: Position) extends Atom with LValue

    case class ParensExpr(expr: Expr)(val pos: Position) extends Atom

    // ========== RValue ==========
    // <rvalue> ::= <expr> | <array-lit> | 'newpair' '(' <expr> ',' <expr> ')'
    //            | <pair-elem> | <call> <ident> '(' <arg-list> ')'
    sealed trait RValue
    case class ArrayLit(exprs: List[Expr])(val pos: Position) extends RValue
    case class NewPair(fst: Expr, snd: Expr)(val pos: Position) extends RValue
    case class Call(func: Id, args: List[Expr])(val pos: Position) extends RValue

    // ========== LValue ==========
    // <lvalue> ::= <ident> | <array-elem> | <pair-elem> 
    sealed trait LValue
    
    // ========== Pair Elements ==========
    // <pair-elem> ::= 'fst' <lvalue> | 'snd' <lvalue>
    sealed trait PairElem extends LValue with RValue
    case class Fst(value: LValue)(val pos: Position) extends PairElem
    case class Snd(value: LValue)(val pos: Position) extends PairElem


    // ========== Companion objects ========== 
    object Or extends ParserBridgePos2[ExprAnd, Expr, Expr]
    object And extends ParserBridgePos2[ExprEq, ExprAnd, ExprAnd]

    object Equal extends ParserBridgePos2[ExprRel, ExprRel, ExprEq]
    object NotEqual extends ParserBridgePos2[ExprRel, ExprRel, ExprEq]

    object Greater extends ParserBridgePos2[ExprAdd, ExprAdd, ExprRel]
    object GreaterEqual extends ParserBridgePos2[ExprAdd, ExprAdd, ExprRel]
    object Less extends ParserBridgePos2[ExprAdd, ExprAdd, ExprRel]
    object LessThan extends ParserBridgePos2[ExprAdd, ExprAdd, ExprRel]

    object Add extends ParserBridgePos2[ExprAdd, ExprMul, ExprAdd]
    object Sub extends ParserBridgePos2[ExprAdd, ExprMul, ExprAdd]

    object Mul extends ParserBridgePos2[ExprMul, ExprUnary, ExprMul]
    object Div extends ParserBridgePos2[ExprMul, ExprUnary, ExprMul]
    object Mod extends ParserBridgePos2[ExprMul, ExprUnary, ExprMul]

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
    // optimisation reduce backtracking for ArrayElem and Ident
    object IdOrArrayElem extends ParserBridgePos2[Id, List[Expr], Atom & LValue] {
        override def apply(id: Id, indices: List[Expr])(pos: Position): Atom & LValue = indices match {
            case Nil => id
            case _ => IdOrArrayElem(id, indices)(pos)
        }
    }
    
    object ParensExpr extends ParserBridgePos1[Expr, ParensExpr]

    object ArrayLit extends ParserBridgePos1[List[Expr], ArrayLit]
    object NewPair extends ParserBridgePos2[Expr, Expr, NewPair]
    object Call extends ParserBridgePos2[Id, List[Expr], Call]

    object Fst extends ParserBridgePos1[LValue, Fst]
    object Snd extends ParserBridgePos1[LValue, Snd]
}
