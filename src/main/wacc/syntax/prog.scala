package wacc.syntax

import bridges.*
import stmts.*

/** Program Structure Abstract Syntax Tree (AST) Nodes for WACC
 *
 * Implements the following BNF grammar rules:
 * 
 * <program> ::= 'begin' <func>* <stmt>* 'end'
 *
 * <func> ::= <type> <ident> '(' <param-list>? ')' 'is' <stmt>* 'end'
 *
 * <param-list> ::= <param> (',' <param>)*
 * <param> ::= <type> <ident>
 *
 * The program structure consists of:
 * 1. Optional function definitions that appear before main program
 * 2. Main program as sequence of statements
 * 3. Each function has:
 *    - Return type
 *    - Parameter list (can be empty)
 *    - Function body as sequence of statements
 *
 * Example program:
 * ```
 * begin
 *   int f(int x) is 
 *     return x + 1
 *   end
 *   int x = call f(10)
 * end
 * ```
 * Represented as:
 * Program(
 *   List(Function(IntType, List(Param(IntType, "x")), List(Return(Add(Id("x"), IntLit(1)))))),
 *   List(Decl(IntType, "x", Call(Id("f"), List(IntLit(10)))))
 * )
 */


object prog {
    // ========== Program Structure ==========
    // <program> ::= 'begin' <func>* <stmt>* 'end'
    // Top-level program container with functions and statements
    case class Program(funcs: List[Function], stmts: List[Stmt])(val pos: Position)
    
    // ========== Function Definition ==========
    // <func> ::= <type> <ident> '(' <param-list>? ')' 'is' <stmt>* 'end'
    // <param-list> ::= <param> (',' <param>)*
    // <param> ::= <type> <ident>
    case class Function(typeId: TypeId, params: List[TypeId], stmts: List[Stmt])(val pos: Position)

    // ========== Companion Objects ==========
    // Bridges for parser combinators to construct AST nodes
    object Program extends ParserBridgePos2[List[Function], List[Stmt], Program]
    object Function extends ParserBridgePos3[TypeId, List[TypeId], List[Stmt], Function]
}
