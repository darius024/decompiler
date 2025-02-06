package wacc.syntax

import cats.data.NonEmptyList
import parsley.generic.*

import bridges.*
import exprs.*
import types.*

/** Statement AST nodes. */
object stmts {
    /** Alias to bundle parameter and type. */
    type TypeId = (IdType, Id)

    /** Alias for non-empty block of statements. */
    type StmtList = NonEmptyList[Stmt]

    /** <stmt> ::= 'skip'
      *          | <type> <ident> '=' <rvalue>
      *          | <lvalue> '=' <rvalue>
      *          | 'read' <lvalue>
      *          | 'free' <expr>
      *          | 'return' <expr>
      *          | 'exit' <expr>
      *          | 'print' <expr>
      *          | 'println' <expr>
      *          | 'if' <expr> 'then' <stmts> 'else' <stmts> 'fi'
      *          | 'while' <expr> 'do' <stmts> 'done'
      *          | 'begin' <stmts> 'end'
      * 
      *  <stmts> ::= <stmt> (';' <stmt>)*
      */
    sealed trait Stmt { val pos: Position }
    case object Skip extends Stmt with ParserBridge0[Stmt] { val pos = NoPosition }
    case class Declaration(typeId: TypeId, rvalue: RValue)(val pos: Position) extends Stmt
    case class Assignment(lvalue: LValue, rvalue: RValue)(val pos: Position) extends Stmt
    case class Read(lvalue: LValue)(val pos: Position) extends Stmt
    case class Print(expr: Expr)(val pos: Position) extends Stmt
    case class Println(expr: Expr)(val pos: Position) extends Stmt
    case class Free(expr: Expr)(val pos: Position) extends Stmt
    case class Return(expr: Expr)(val pos: Position) extends Stmt
    case class Exit(expr: Expr)(val pos: Position) extends Stmt
    case class If(cond: Expr, thenStmts: StmtList, elseStmts: StmtList)(val pos: Position) extends Stmt
    case class While(cond: Expr, doStmts: StmtList)(val pos: Position) extends Stmt
    case class Block(stmts: StmtList)(val pos: Position) extends Stmt

    // companion objects

    object Declaration extends ParserBridgePos2[TypeId, RValue, Declaration]
    object Assignment extends ParserBridgePos2[LValue, RValue, Assignment]
    object Read extends ParserBridgePos1[LValue, Read]
    object Free extends ParserBridgePos1[Expr, Free]
    object Return extends ParserBridgePos1[Expr, Return]
    object Exit extends ParserBridgePos1[Expr, Exit]
    object Print extends ParserBridgePos1[Expr, Print] 
    object Println extends ParserBridgePos1[Expr, Println]
    object If extends ParserBridgePos3[Expr, StmtList, StmtList, If]
    object While extends ParserBridgePos2[Expr, StmtList, While]
    object Block extends ParserBridgePos1[StmtList, Block]
}
