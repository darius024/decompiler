package wacc.semantics

import java.io.File
import scala.io.Source

import scoping.scopeCheck
import typing.{typeCheck, TypeInfo}

import wacc.error.ErrorLines
import wacc.error.errors.*
import wacc.error.WaccErrorBuilder.*
import wacc.error.PartialSemanticError
import wacc.syntax.prog.Program

def checkSemantics(prog: Program): Either[List[PartialSemanticError], Program] = {
    val (scopeErrs, funcs, vars) = scopeCheck(prog)
    
    typeCheck(prog, TypeInfo(funcs, vars)) match {
        case Left(typeErrs) => Left(scopeErrs ++ typeErrs.toList)
        case Right(_)       => scopeErrs match {
            case Nil => Right(prog)
            case _   => Left(scopeErrs)
        }
    }
}

def semanticAnalysis(prog: Program, program: File): Either[List[SemanticError], Program] = checkSemantics(prog) match {
    case Left(errs) => Left(errs.map(augmentError(_, program)))
    case Right(_)   => Right(prog)
}

def augmentError(err: PartialSemanticError, program: File): SemanticError = {
    val (row, col) = err.pos

    val source = Source.fromFile(program)
    val lines = source.getLines().toList
    source.close()

    val line = if (row >= lines.length) {
        Nil
    } else {
        val previousLine = if (row > 0) Seq(lines(row - 1)) else Nil
        val nextLine = if (row < lines.length - 1) Seq(lines(row + 1)) else Nil
        lineInfo(lines(row), previousLine, nextLine, row, col, 1)
    }
    val errorLines = ErrorLines.VanillaError(err.unexpected, err.expected, err.reasons, line)

    return SemanticError(err.pos, program.getName(), errorLines, err.errorType)
}
