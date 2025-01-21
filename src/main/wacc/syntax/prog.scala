package wacc.syntax

import parsley.generic.*
import stmts.*

object prog {
    case class Program(funcs: List[Function], stmts: List[Stmt])
    case class Function(typeId: TypeId, params: List[TypeId], stmts: List[Stmt])

    object Program extends ParserBridge2[List[Function], List[Stmt], Program]
    object Function extends ParserBridge3[TypeId, List[TypeId], List[Stmt], Function]
}
