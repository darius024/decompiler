package wacc.syntax

import parsley.generic.*
import bridges.*
import exprs.*
import types.*

/** Statement Nodes of the Abstract Syntax Tree.
  *
  * Implements the statements that the WACC language supports.       
  */
object stmts {
    type TypeId = (IdType, Id)

    // ========== Base Statement Trait ==========
    // <stmts> ::= <stmt> ';' <stmt> | <stmt>
    sealed trait Stmt

    // ========== Basic Statements ==========
    // <stmt> ::= 'skip'
    case object Skip extends Stmt with ParserBridge0[Stmt]

    // ========== Variable Declarations and Assignments ==========
    // <stmt> ::= <type> <ident> '=' <rvalue>
    case class Declaration(typeId: TypeId, rvalue: RValue)(val pos: Position) extends Stmt
    // <stmt> ::= <lvalue> '=' <rvalue>
    case class Assignment(lvalue: LValue, rvalue: RValue)(val pos: Position) extends Stmt

    // ========== I/O Statements ==========
    // <stmt> ::= 'read' <lvalue>
    case class Read(lvalue: LValue)(val pos: Position) extends Stmt
    // <stmt> ::= 'print' <expr>
    case class Print(expr: Expr)(val pos: Position) extends Stmt
    // <stmt> ::= 'println' <expr>
    case class Println(expr: Expr)(val pos: Position) extends Stmt

    // ========== Memory Management ==========
    // <stmt> ::= 'free' <expr>
    case class Free(expr: Expr)(val pos: Position) extends Stmt

    // ========== Control Flow ==========
    // <stmt> ::= 'return' <expr>
    case class Return(expr: Expr)(val pos: Position) extends Stmt
    // <stmt> ::= 'exit' <expr>
    case class Exit(expr: Expr)(val pos: Position) extends Stmt
    
    // ========== Conditional and Looping ==========
    // <stmt> ::= 'if' <expr> 'then' <stmts> 'else' <stmts> 'fi'
    case class If(cond: Expr, thenStmts: List[Stmt], elseStmts: List[Stmt])(val pos: Position) extends Stmt
    // <stmt> ::= 'while' <expr> 'do' <stmts> 'done'
    case class While(cond: Expr, doStmts: List[Stmt])(val pos: Position) extends Stmt

    // ========== Scope ==========
    // <stmt> ::= 'begin' <stmts> 'end'
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
