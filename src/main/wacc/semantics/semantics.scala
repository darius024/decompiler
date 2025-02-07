package wacc.semantics

import cats.data.NonEmptyList
import java.io.File
import scala.io.Source

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
    errs.map(augmentError(_, program)).toList.mkString("\n")

/** Augments a partial semantic error with additional information. */
private def augmentError(err: PartialSemanticError, program: File): SemanticError = {
    val (row, col) = err.pos
    val errorRow = row - 1

    val source = Source.fromFile(program)
    // ensure consistency in tab size (4 spaces)
    val lines = source.getLines().map(_.replace("\t", "    ")).toList
    source.close()

    // fetch the lines information
    val line = if (errorRow < lines.length) {
        val previousLines = (math.max(0, errorRow - WaccErrorBuilder.NumLinesBefore) to errorRow - 1).map(lines)
        val nextLines = (errorRow + 1 to math.min(lines.length - 1, errorRow + WaccErrorBuilder.NumLinesAfter)).map(lines)
        WaccErrorBuilder.lineInfo(lines(errorRow), previousLines, nextLines, errorRow, col, 1)
    } else Nil
    val errorLines = ErrorLines.VanillaError(err.unexpected, err.expected, err.reasons, line)

    return SemanticError(err.pos, program.getName(), errorLines, err.errorType)
}
