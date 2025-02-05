package wacc.semantics

import java.io.File
import scala.io.Source

import scoping.scopeCheck
import typing.{typeCheck, TypeInfo}

import wacc.error.{ErrorLines, PartialSemanticError, WaccErrorBuilder}
import wacc.error.errors.*
import wacc.syntax.prog.Program

/** Perform complete semantic analysis on the program. */
def semanticAnalysis(prog: Program, program: File): Either[List[SemanticError], Program] = checkSemantics(prog) match {
    case Left(errs) => Left(errs.map(augmentError(_, program)))
    case Right(_)   => Right(prog)
}

/** Perform partial semantic analysis on the program, only returning errors. */
def checkSemantics(prog: Program): Either[List[PartialSemanticError], Program] = {
    // perform scope checking
    val (scopeErrs, funcs, vars) = scopeCheck(prog)
    
    // perform type checking
    typeCheck(prog, TypeInfo(funcs, vars)) match {
        case Left(typeErrs) => Left(scopeErrs ++ typeErrs.toList)
        case Right(_)       => scopeErrs match {
            case Nil => Right(prog)
            case _   => Left(scopeErrs)
        }
    }
}

/** Augment a partial semantic error with additional information. */
def augmentError(err: PartialSemanticError, program: File): SemanticError = {
    val (row, col) = err.pos

    val source = Source.fromFile(program)
    val lines = source.getLines().toList
    source.close()

    // fetch the lines information
    val line = if (row < lines.length) {
        val previousLines = (math.max(0, row - WaccErrorBuilder.NumLinesBefore) to row).map(lines)
        val nextLines = (row + 1 to math.min(lines.length - 1, row + WaccErrorBuilder.NumLinesAfter + 1)).map(lines)
        WaccErrorBuilder.lineInfo(lines(row), previousLines, nextLines, row, col, 1)
    } else Nil
    val errorLines = ErrorLines.VanillaError(err.unexpected, err.expected, err.reasons, line)

    return SemanticError(err.pos, program.getName(), errorLines, err.errorType)
}
