package wacc.semantics

import errors.SemanticError
import scoping.scopeCheck
import typing.{typeCheck, TypeInfo}
import wacc.syntax.prog.Program

def checkSemantics(prog: Program): Either[List[SemanticError], Program] = {
    val (scopeErrs, funcs, vars) = scopeCheck(prog)

    /** TODO: Correct the type checker
    typeCheck(prog, TypeInfo(funcs, vars)) match {
        case Left(typeErrs) => Left(scopeErrs ++ typeErrs.toList)
        case Right(_) => scopeErrs match {
            case Nil => Right(prog)
            case _ => Left(scopeErrs)
        }
    }
    */

    // dummy return
    Right(Program(Nil, Nil)(0, 0))
}

object errors {
    trait SemanticError
}
