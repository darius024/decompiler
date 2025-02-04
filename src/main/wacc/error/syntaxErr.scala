package wacc.error

import parsley.errors.ErrorBuilder
import parsley.errors.tokenextractors.LexToken

import wacc.syntax.*
import errors.*
import ErrorLines.*

sealed trait SyntaxErrorItem extends ErrorItem
case class SyntaxRaw(item: String) extends SyntaxErrorItem {
    override def toString: String = s"\"$item\""
}
case class SyntaxNamed(item: String) extends SyntaxErrorItem {
    override def toString: String = item
}
case object SyntaxEndOfInput extends SyntaxErrorItem {
    override def toString: String = "end of input"
}

abstract class SyntaxErrorBuilder extends ErrorBuilder[SyntaxError] {
    override def build(pos: Position, source: Source, lines: ErrorInfoLines): SyntaxError =
        SyntaxError(pos, source, lines)

    type Position = bridges.Position
    override def pos(line: Int, col: Int): Position = (line, col)

    type Source = String
    override def source(sourceName: Option[String]): Source = sourceName.getOrElse("")

    type ErrorInfoLines = ErrorLines
    override def vanillaError(unexpected: UnexpectedLine, expected: ExpectedLine, reasons: Messages, line: LineInfo): ErrorInfoLines =
        VanillaError(unexpected, expected, reasons, line)
    
    override def specializedError(msgs: Messages, line: LineInfo): ErrorInfoLines =
        SpecialisedError(msgs, line)

    type ExpectedItems = Set[Item]
    override def combineExpectedItems(alts: Set[Item]): ExpectedItems = alts

    type Messages = Set[Message]
    override def combineMessages(alts: Seq[Message]): Messages = alts.toSet

    type UnexpectedLine = Option[Item]
    override def unexpected(item: Option[Item]): UnexpectedLine = item
    type ExpectedLine = ExpectedItems
    override def expected(alts: ExpectedItems): ExpectedLine = alts

    type Message = String
    override def reason(reason: String): Message = reason
    override def message(msg: String): Message = msg

    type LineInfo = Seq[String]
    override def lineInfo(line: String, linesBefore: Seq[String], linesAfter: Seq[String], lineNum: Int, errorPointsAt: Int, errorWidth: Int): LineInfo =
        WaccErrorBuilder.lineInfo(line, linesBefore, linesAfter, lineNum, errorPointsAt, errorWidth)

    override val numLinesBefore: Int = WaccErrorBuilder.numLinesBefore
    override val numLinesAfter: Int = WaccErrorBuilder.numLinesAfter

    type Item = ErrorItem
    type Raw = SyntaxRaw
    type Named = SyntaxNamed
    type EndOfInput = SyntaxEndOfInput.type
    override def raw(item: String): Raw = SyntaxRaw(item)
    override def named(item: String): Named = SyntaxNamed(item)
    override val endOfInput: EndOfInput = SyntaxEndOfInput
}

object syntaxErrors {
    import wacc.lexer.*
    
    implicit val errorBuilder: ErrorBuilder[SyntaxError] = new SyntaxErrorBuilder with LexToken {
        def tokens = tokensList
    }
}
