package wacc.error

import wacc.syntax.*
import ErrorLines.*

trait ErrorItem

enum ErrorLines {
    case VanillaError(unexpected: Option[ErrorItem], expected: Set[ErrorItem], reasons: Set[String], line: Seq[String])
    case SpecialisedError(msgs: Set[String], line: Seq[String])
}

object errors {
    sealed trait WaccError(pos: bridges.Position, source: String, lines: ErrorLines) {
        val errorType: String

        override def toString(): String = {
            val sb = new StringBuilder

            sb.append(s"$errorType in $source at position (${pos._1}, ${pos._2}):")
            lines match {
                case VanillaError(unexpected, expected, reasons, line) =>
                    sb.append(s"\n  unexpected: ${unexpected.getOrElse("end of input")}")
                    sb.append(s"\n  expected: ${expected.mkString(", ")}")
                    if (reasons.nonEmpty) {
                        sb.append(s"\n  reasons: ${reasons.mkString(", ")}")
                    }
                    sb.append(s"\n  ${line.mkString("\n  ")}")
                
                case SpecialisedError(msgs, line) =>
                    sb.append(s"\n  ${msgs.mkString(", ")}")
                    sb.append(s"\n  ${line.mkString("\n  ")}")
            }

            sb.toString()
        }
    }

    case class SyntaxError(pos: bridges.Position, source: String, lines: ErrorLines) extends WaccError(pos, source, lines) {
        override val errorType: String = "Syntax Error"
    }
    case class SemanticError(pos: bridges.Position, source: String, lines: ErrorLines, errTy: String) extends WaccError(pos, source, lines) {
        override val errorType: String = errTy
    }

    case object FileNotFound extends WaccError((0, 0), "", VanillaError(None, Set.empty, Set.empty, Seq.empty)) {
        override val errorType: String = "File Not Found"
    }
}

object WaccErrorBuilder {
    final val ErrorLineStart = "|"
    final val numLinesBefore = 1
    final val numLinesAfter = 1

    def lineInfo(line: String, linesBefore: Seq[String], linesAfter: Seq[String], lineNum: Int, errorPointsAt: Int, errorWidth: Int): Seq[String] = {
        Seq.concat(
            linesBefore.map(inputLine(_, lineNum - 1)),
            Seq(inputLine(line, lineNum), caretLine(errorPointsAt, errorWidth)),
            linesAfter.map(inputLine(_, lineNum + 1)),
        )
    }

    def inputLine(line: String, lineNum: Int): String = s"$lineNum: $ErrorLineStart$line"

    def caretLine(caretAt: Int, caretWidth: Int): String = s"${" " * (ErrorLineStart.length + caretAt)}${"^" * caretWidth}"
}
