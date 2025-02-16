package wacc.syntax

import parsley.Parsley
import parsley.errors.combinator.*

import bridges.*
import stmts.*

/** Program structure AST nodes. */
object prog {
    /** <program> ::= 'begin' <func>* <stmt>+ 'end' */
    case class Program(funcs: List[Function], stmts: StmtList)(val pos: Position)
    
    /** <func> ::= <type> <ident> '(' <param-list>? ')' 'is' <stmt>* 'end'
      * <param-list> ::= <param> (',' <param>)*
      * <param> ::= <type> <ident>
      */
    case class Function(typeId: TypeId, params: Array[TypeId], stmts: StmtList)(val pos: Position)

    // companion objects
    
    object Program extends ParserBridgePos2[List[Function], StmtList, Program]
    object Function extends ParserBridgePos3[TypeId, List[TypeId], StmtList, Function] {
        // helper function to determine if the function has a returning block
        def endsWithReturnOrExit(stmts: StmtList): Boolean = stmts.last match {
            case Return(_) | Exit(_)         => true
            case If(_, thenStmts, elseStmts) => endsWithReturnOrExit(thenStmts) && endsWithReturnOrExit(elseStmts)
            case Block(blockStmts)           => endsWithReturnOrExit(blockStmts)
            case _                           => false
        }

        // override the apply method to reject non-returning function bodies
        override def apply(typeId: Parsley[TypeId],
                           params: => Parsley[List[TypeId]],
                           stmts: => Parsley[StmtList]): Parsley[Function] = super.apply(typeId, params, stmts).guardAgainst {
            case Function((_, name), _, stmts) if !endsWithReturnOrExit(stmts) =>
                Seq(s"function `${name.value}` must have a return on all exit paths")
        }

        override def apply(typeId: TypeId, params: List[TypeId], stmts: StmtList)(pos: Position): Function =
            Function(typeId, params.toArray, stmts)(pos)

        override def labels: List[String] = List("function")
    }
}
