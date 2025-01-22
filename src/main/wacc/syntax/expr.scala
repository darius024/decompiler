package wacc.syntax

import parsley.generic.*
import bridges.*

/** Expression Abstract Syntax Tree (AST) Nodes for WACC
 *
 * Implements the following BNF grammar rules:
 * 
 * <expr> ::= <unary-oper> <expr> 
 *          | <expr> <binary-oper> <expr> 
 *          | <atom>
 *          | <lvalue> 
 * 
 * <lvalue> ::= <ident> 
 *            | <array-elem> 
 *            | <pair-elem>
 * 
 * <rvalue> ::= <expr> 
 *            | <array-literal> 
 *            | 'newpair' '(' <expr> ',' <expr> ')'
 *            | <pair-elem> 
 *            | 'call' <ident> '(' <arg-list>? ')'
 *
 * <atom> ::= <int-liter> 
 *          | <bool-liter> 
 *          | <char-liter> 
 *          | <str-liter> 
 *          | <pair-liter> 
 *          | <ident> 
 *          | <array-elem> 
 *          | '(' <expr> ')'
 *
 * Operators follow precedence levels, listed from lowest to highest:
 *
 * 1. Logical OR (||): Represented by the `Or` case class extending `Expr`.
 * 2. Logical AND (&&): Represented by the `And` case class extending `ExprAnd`.
 * 3. Equality/Inequality (==, !=): Represented by `Equal` and `NotEqual` extending `ExprEq`.
 * 4. Relational comparisons (>, >=, <, <=): Represented by `Greater`, `GreaterEqual`, `Less`, and `LessEqual` extending  ExprRel`.
 * 5. Addition/Subtraction (+, -): Represented by `Add` and `Sub` extending `ExprAdd`.
 * 6. Multiplication/Division/Modulo (*, /, %): Represented by `Mul`, `Div`, and `Mod` extending `ExprMul`.
 * 7. Unary operators (!, -, len, ord, chr): Represented by `Not`, `Neg`, `Len`, `Ord`, and `Chr` extending `ExprUnary`.
 *
 * Binary operator nodes reflect grammar rules and precedence:
 * - Left-associative operators (e.g., +, -, *, /, %) extend their own type to enable chaining.
 *   For example, `Add(lhs: ExprAdd, rhs: ExprMul)(val pos: Position) extends ExprAdd`.
 * - Right-associative operators (e.g., &&, ||) extend the parent precedence type.
 *   For example, `And(lhs: ExprEq, rhs: ExprAnd)(val pos: Position) extends ExprAnd`.
 * - Non-associative operators (e.g., ==, !=) enforce strict operand types by extending the same type.
 *   For example, `Equal(lhs: ExprRel, rhs: ExprRel)(val pos: Position) extends ExprEq`.
 *
 * Unary operators bind tighter than all binary operators. For example:
 * `!a * b` is parsed as `(!a) * b`, where `ExprUnary` extends `ExprMul`.
 *
 * Example:
 * An input expression like `1 + 2 * 3 && !4` is represented as:
 * ```
 * And(
 *   Add(IntLit(1), Mul(IntLit(2), IntLit(3))),
 *   Not(IntLit(4))
 * )
 * ```
 *
 * Expression AST nodes enable structured representation and processing of expressions,
 * adhering to WACC grammar rules, precedence, associativity, and type constraints.
 */



object exprs {
    // Base expression trait
    // <expr> ::= <unary-oper> <expr> | <expr> <binary-oper> <expr> | <atom> 
    sealed trait Expr extends RValue
    
    // ========== Logical OR (||) (Lowest Precedence) ==========
    // <expr> :: <expr> | <and-expr> '||' <expr>
    case class Or(lhs: ExprAnd, rhs: Expr)(val pos: Position) extends Expr
    
    // ========== Logical AND (&&) ==========
    // <and-expr> ::= <eq-expr> | <and-expr> '&&' <eq-expr>  
    sealed trait ExprAnd extends Expr
    case class And(lhs: ExprEq, rhs: ExprAnd)(val pos: Position) extends ExprAnd
    
    // ========== Equality/Inequality (==, !=) ==========
    // <eq-expr> ::= <rel-expr> | <eq-expr> ('==' | '!=') <rel-expr>
    sealed trait ExprEq extends ExprAnd
    case class Equal(lhs: ExprRel, rhs: ExprRel)(val pos: Position) extends ExprEq
    case class NotEqual(lhs: ExprRel, rhs: ExprRel)(val pos: Position) extends ExprEq

    // ========== Relational comparisons (>, >=, <, <=) ==========
    // <rel-expr> ::= <add-expr> | <rel-expr> ('>' | '>=' | '<' | '<=') <add-expr>
    sealed trait ExprRel extends ExprEq
    case class Greater(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprRel
    case class GreaterEqual(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprRel
    case class Less(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprRel
    case class LessThan(lhs: ExprAdd, rhs: ExprAdd)(val pos: Position) extends ExprRel

    // ========== Additive ==========
    // <add-expr> ::= <mul-expr> | <add-expr> ('+' | '-') <mul-expr>
    sealed trait ExprAdd extends ExprRel
    case class Add(lhs: ExprAdd, rhs: ExprMul)(val pos: Position) extends ExprAdd
    case class Sub(lhs: ExprAdd, rhs: ExprMul)(val pos: Position) extends ExprAdd

    // ========== Multiplicative ==========
    // <mul-expr> ::= <unary-expr> | <mul-expr> ('*' | '/' | '%') <unary-expr>
    sealed trait ExprMul extends ExprAdd
    case class Mul(lhs: ExprMul, rhs: ExprUnary)(val pos: Position) extends ExprMul
    case class Div(lhs: ExprMul, rhs: ExprUnary)(val pos: Position) extends ExprMul
    case class Mod(lhs: ExprMul, rhs: ExprUnary)(val pos: Position) extends ExprMul

    // ========== Unary (highest precedence) ==========
    // <unary-expr> ::= ('!' | '-' | 'len' | 'ord' | 'chr') <expr>
    sealed trait ExprUnary extends ExprMul
    case class Not(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Neg(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Len(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Ord(expr: ExprUnary)(val pos: Position) extends ExprUnary
    case class Chr(expr: ExprUnary)(val pos: Position) extends ExprUnary

    // ========== Atoms ==========
    // <atom> ::= <int-liter> | <bool-liter> | <char-liter> | <str-liter> | <pair-liter> | <ident> | <array-elem> | '(' <expr> ')'
    sealed trait Atom extends ExprUnary
    case class IntLit(value: Int)(val pos: Position) extends Atom
    case class BoolLit(value: Boolean)(val pos: Position) extends Atom
    case class CharLit(value: Char)(val pos: Position) extends Atom
    case class StrLit(value: String)(val pos: Position) extends Atom
    case object PairLit extends Atom with ParserBridge0[Atom]
    case class Id(value: String)(val pos: Position) extends Atom with LValue
    case class ArrayElem(id: Id, indices: List[Expr])(val pos: Position) extends Atom with LValue
    case class ParensExpr(expr: Expr)(val pos: Position) extends Atom

    
    // ========== RValue ==========
    sealed trait RValue
    case class ArrayLit(exprs: List[Expr])(val pos: Position) extends RValue
    case class NewPair(fst: Expr, snd: Expr)(val pos: Position) extends RValue
    case class Call(func: Id, args: List[Expr])(val pos: Position) extends RValue

    // ========== LValue ==========
    // <lvalue> ::= <ident> | <array-elem> | <pair-elem> 
    sealed trait LValue
    
    // ========== Pair Elements ==========
    // <pair-elem> ::= 'fst' | 'snd'
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
