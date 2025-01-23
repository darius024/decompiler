package wacc.syntax

import bridges.*
import stmts.*

/** Program structure AST nodes. */
object prog {
    /** <program> ::= 'begin' <func>* <stmt>+ 'end' */
    case class Program(funcs: List[Function], stmts: List[Stmt])(val pos: Position)
    
    /** <func> ::= <type> <ident> '(' <param-list>? ')' 'is' <stmt>* 'end'
      * <param-list> ::= <param> (',' <param>)*
      * <param> ::= <type> <ident>
      */
    case class Function(typeId: TypeId, params: List[TypeId], stmts: List[Stmt])(val pos: Position)

    // companion objects
    
    object Program extends ParserBridgePos2[List[Function], List[Stmt], Program]
    object Function extends ParserBridgePos3[TypeId, List[TypeId], List[Stmt], Function]
}
