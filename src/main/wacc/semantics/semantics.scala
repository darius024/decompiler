package wacc.semantics

import cats.data.NonEmptyList
import java.io.File
import os.*

import scoping.scopeCheck
import typing.*
import wacc.error.{ErrorLines, PartialSemanticError, WaccErrorBuilder}
import wacc.error.errors.*
import wacc.syntax.prog.Program

/** Performs partial semantic analysis on the program, only returning errors.
  * 
  * Checks the program for scope and type errors.
  */
def check(prog: Program): Either[NonEmptyList[PartialSemanticError], TyProg] =
    typeCheck(prog, scopeCheck(prog))

/** Formats the error messages to contain the code segment. */
def format(errs: NonEmptyList[PartialSemanticError], program: File): String =
    errs.map(augmentError(_, program).message).toList.mkString("\n")

/** Augments a partial semantic error with additional information. */
private def augmentError(err: PartialSemanticError, program: File): SemanticError = {
    val (row, col) = err.pos
    // adjust row and column information
    val errorRow = if (row >= 0) row - 1 else 0
    val adjustedCol = if (col >= 0) col - 1 else 0

    // ensure consistency in tab size (4 spaces)
    val lines = os.read.lines(os.Path(program.getAbsolutePath)).map(_.replace("\t", "    "))

    // fetch the lines information
    val line = if (errorRow < lines.length) {
        val previousLines = (math.max(0, errorRow - WaccErrorBuilder.NumLinesBefore) to errorRow - 1).map(lines)
        val nextLines = (errorRow + 1 to math.min(lines.length - 1, errorRow + WaccErrorBuilder.NumLinesAfter)).map(lines)
        WaccErrorBuilder.lineInfo(lines(errorRow), previousLines, nextLines, row, adjustedCol, 1)
    } else Nil
    val errorLines = ErrorLines.VanillaError(err.unexpected, err.expected, err.reasons, line)

    // build the semantic error
    return SemanticError(err.pos, program.getName(), errorLines, err.errorType)
}
