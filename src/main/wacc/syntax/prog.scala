package wacc.syntax

import parsley.errors.combinator.*

import bridges.*
import stmts.*
import parsley.Parsley

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
    object Function extends ParserBridgePos3[TypeId, List[TypeId], List[Stmt], Function] {
      // helper function to determine if a function ends with a return statement, exit statement or a terminating if statement
      def endsWithReturnOrExit(stmts: List[Stmt]): Boolean = stmts.lastOption match {
        case Some(Return(_)) | Some(Exit(_)) => true
        case Some(If(_, thenStmts, elseStmts)) => endsWithReturnOrExit(thenStmts) && endsWithReturnOrExit(elseStmts)
        case _ => false
      }

      /** override the apply method to guard against functions that do not end with a return statement, 
       *  exit statement or a terminating if statement
      */
      override def apply(x: Parsley[TypeId], y: => Parsley[List[TypeId]], z: => Parsley[List[Stmt]]): Parsley[Function] 
        = super.apply(x, y, z).guardAgainst{
          case Function(_, _, stmts) if !endsWithReturnOrExit(stmts) => Seq("Function does not end with a return statement, exit statement or a terminating if statement")
    
        } 
    }
}