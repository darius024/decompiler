package wacc.syntax

import bridges.*
import stmts.*

/** Program Nodes of the Abstract Syntax Tree.
  * 
  * Implements the main program data structure, which is the root of the AST.
  * Implements the function definition data structure.
  */
object prog {
    // ========== Program Structure ==========
    // <program> ::= 'begin' <func>* <stmt>* 'end'
    case class Program(funcs: List[Function], stmts: List[Stmt])(val pos: Position)
    
    // ========== Function Definition ==========
    // <func> ::= <type> <ident> '(' <param-list>? ')' 'is' <stmt>* 'end'
    case class Function(typeId: TypeId, params: List[TypeId], stmts: List[Stmt])(val pos: Position)

    
    // ========== Companion Objects ==========
    object Program extends ParserBridgePos2[List[Function], List[Stmt], Program]
    object Function extends ParserBridgePos3[TypeId, List[TypeId], List[Stmt], Function]
}
