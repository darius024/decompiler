package wacc.semantics

import java.io.File
import scala.io.Source

import scoping.scopeCheck
import typing.{typeCheck, TypeInfo}

import wacc.error.{ErrorLines, PartialSemanticError, WaccErrorBuilder}
import wacc.error.errors.*
import wacc.syntax.prog.Program

/** Perform partial semantic analysis on the program, only returning errors. */
def check(prog: Program): Either[List[PartialSemanticError], Program] = {
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

/** Format the error messages to contain the code segment. */
def format(errs: List[PartialSemanticError], program: File): String =
    errs.map(augmentError(_, program)).mkString("\n")

/** Augment a partial semantic error with additional information. */
private def augmentError(err: PartialSemanticError, program: File): SemanticError = {
    val (row, col) = err.pos
    val errorRow = row - 1

    val source = Source.fromFile(program)
    val lines = source.getLines().toList
    source.close()

    // fetch the lines information
    val line = if (errorRow < lines.length) {
        val previousLines = (math.max(0, errorRow - WaccErrorBuilder.NumLinesBefore) to errorRow - 1).map(lines)
        val nextLines = (errorRow + 1 to math.min(lines.length - 1, errorRow + WaccErrorBuilder.NumLinesAfter + 1)).map(lines)
        WaccErrorBuilder.lineInfo(lines(errorRow), previousLines, nextLines, errorRow, col, 1)
    } else Nil
    val errorLines = ErrorLines.VanillaError(err.unexpected, err.expected, err.reasons, line)

    return SemanticError(err.pos, program.getName(), errorLines, err.errorType)
}
