package wacc.syntax

import bridges.*
import stmts.*

object prog {
    case class Program(funcs: List[Function], stmts: List[Stmt])(val pos: Position)
    case class Function(typeId: TypeId, params: List[TypeId], stmts: List[Stmt])(val pos: Position)


    object Program extends ParserBridgePos2[List[Function], List[Stmt], Program]
    object Function extends ParserBridgePos3[TypeId, List[TypeId], List[Stmt], Function]
}
