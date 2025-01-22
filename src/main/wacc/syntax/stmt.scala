package wacc.syntax

import parsley.generic.*
import bridges.*
import exprs.*
import types.*

/** Statement Abstract Syntax Tree (AST) Nodes for WACC
 *
 * Implements the following BNF grammar rules:
 *
 * <stmt> ::= 'skip'
 *          | <type> <ident> '=' <rvalue>
 *          | <lvalue> '=' <rvalue>
 *          | 'read' <lvalue>
 *          | 'free' <expr>
 *          | 'return' <expr>
 *          | 'exit' <expr>
 *          | 'print' <expr>
 *          | 'println' <expr>
 *          | 'if' <expr> 'then' <stmt>* 'else' <stmt>* 'fi'
 *          | 'while' <expr> 'do' <stmt>* 'done'
 *          | 'begin' <stmt>* 'end'
 *          | <stmt> ';' <stmt>
 *
 * Statements form the executable parts of WACC programs:
 * 1. Variable declarations and assignments
 * 2. Control flow (if, while)
 * 3. Scope blocks (begin/end)
 * 4. I/O operations (read, print)
 * 5. Program flow (return, exit)
 * 6. Memory management (free)
 *
 * Example:
 * ```
 * if x > 0 then
 *   println x
 *   x = x - 1
 * else
 *   exit 1
 * fi
 * ```
 * Represented as:
 * If(
 *   Greater(Id("x"), IntLit(0)),
 *   List(Println(Id("x")), Assign(Id("x"), Sub(Id("x"), IntLit(1)))),
 *   List(Exit(IntLit(1)))
 * )
 */

object stmts {
    type TypeId = (IdType, Id)


    // ========== Statement Nodes ==========
    sealed trait Stmt

    case object Skip extends Stmt with ParserBridge0[Stmt]
    case class Declaration(typeId: TypeId, rvalue: RValue)(val pos: Position) extends Stmt
    case class Assignment(lvalue: LValue, rvalue: RValue)(val pos: Position) extends Stmt
    case class Read(lvalue: LValue)(val pos: Position) extends Stmt
    case class Free(expr: Expr)(val pos: Position) extends Stmt
    case class Return(expr: Expr)(val pos: Position) extends Stmt
    case class Exit(expr: Expr)(val pos: Position) extends Stmt
    case class Print(expr: Expr)(val pos: Position) extends Stmt
    case class Println(expr: Expr)(val pos: Position) extends Stmt
    case class If(cond: Expr, thenStmts: List[Stmt], elseStmts: List[Stmt])(val pos: Position) extends Stmt
    case class While(cond: Expr, doStmts: List[Stmt])(val pos: Position) extends Stmt
    case class Block(stmts: List[Stmt])(val pos: Position) extends Stmt

    // ========== Companion Objects ==========
    object Declaration extends ParserBridgePos2[TypeId, RValue, Declaration]
    object Assignment extends ParserBridgePos2[LValue, RValue, Assignment]
    object Read extends ParserBridgePos1[LValue, Read]
    object Free extends ParserBridgePos1[Expr, Free]
    object Return extends ParserBridgePos1[Expr, Return]
    object Exit extends ParserBridgePos1[Expr, Exit]
    object Print extends ParserBridgePos1[Expr, Print]
    object Println extends ParserBridgePos1[Expr, Println]
    object If extends ParserBridgePos3[Expr, List[Stmt], List[Stmt], If]
    object While extends ParserBridgePos2[Expr, List[Stmt], While]
    object Block extends ParserBridgePos1[List[Stmt], Block]
}
