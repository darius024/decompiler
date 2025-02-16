package wacc.error

import wacc.semantics.scoping.semanticTypes.*

/** Error data structure that holds information about the error.
  * 
  * Used by the syntactic and semantic analysis phases to generate structured errors.
  */
sealed trait ErrorItem {
    /** Prints the error item in a human-readable format. */
    def print: String
}

/** Error structures that are retrieved from the parser. */
sealed trait SyntaxErrorItem extends ErrorItem

/** Unlabelled error item. */
case class SyntaxRaw(item: String) extends SyntaxErrorItem {
    def print: String = s"\"$item\""
}
/** Labelled error item. */
case class SyntaxNamed(item: String) extends SyntaxErrorItem {
    def print: String = item
}
/** End of input error item. */
case object SyntaxEndOfInput extends SyntaxErrorItem {
    def print: String = "end of input"
}

/** Error structures that are retrieved from the semantic checker. */
sealed trait SemanticErrorItem extends ErrorItem

/** Variable error item. */
case class SemanticVar(item: String) extends SemanticErrorItem {
    def print: String = s"variable ${item}"
}
/** Function error item. */
case class SemanticFunc(item: String) extends SemanticErrorItem {
    def print: String = s"function call ${item}"
}
/** Type error item. */
case class SemanticType(item: SemType) extends SemanticErrorItem {
    def print: String = s"type $item"
}
/** Redeclaration error item. */
case class SemanticRedecl(item: ErrorItem) extends SemanticErrorItem {
    def print: String = s"redeclaration of ${item.print}"
}
/** Number of arguments error item. */
case class SemanticNumArg(num: Int) extends SemanticErrorItem {
    def print: String = s"$num number of arguments"
}
/** Return statement in the main body error item. */
case object SemanticReturnMain extends SemanticErrorItem {
    def print: String = "return statement in the main body"
}
