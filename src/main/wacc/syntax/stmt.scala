package wacc.syntax

import parsley.generic.*
import exprs.*
import types.*

object stmts {
    type TypeId = (IdType, Id)

    sealed trait Stmt
    case object Skip extends Stmt with ParserBridge0[Stmt]
    case class Declaration(typeId: TypeId, rvalue: RValue) extends Stmt
    case class Assignment(lvalue: LValue, rvalue: RValue) extends Stmt
    case class Read(lvalue: LValue) extends Stmt
    case class Free(expr: Expr) extends Stmt
    case class Return(expr: Expr) extends Stmt
    case class Exit(expr: Expr) extends Stmt
    case class Print(expr: Expr) extends Stmt
    case class Println(expr: Expr) extends Stmt
    case class If(cond: Expr, thenStmts: List[Stmt], elseStmts: List[Stmt]) extends Stmt
    case class While(cond: Expr, doStmts: List[Stmt]) extends Stmt
    case class Block(stmts: List[Stmt]) extends Stmt

    object Declaration extends ParserBridge2[TypeId, RValue, Declaration]
    object Assignment extends ParserBridge2[LValue, RValue, Assignment]
    object Read extends ParserBridge1[LValue, Read]
    object Free extends ParserBridge1[Expr, Free]
    object Return extends ParserBridge1[Expr, Return]
    object Exit extends ParserBridge1[Expr, Exit]
    object Print extends ParserBridge1[Expr, Print]
    object Println extends ParserBridge1[Expr, Println]
    object If extends ParserBridge3[Expr, List[Stmt], List[Stmt], If]
    object While extends ParserBridge2[Expr, List[Stmt], While]
    object Block extends ParserBridge1[List[Stmt], Block]
}
