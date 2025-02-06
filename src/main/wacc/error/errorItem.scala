package wacc.error

import wacc.semantics.scoping.semanticTypes.*

/** Error data structure that holds information about the error.
  * 
  * Used by the syntactic and semantic analysis phases to generate structured errors.
  */
sealed trait ErrorItem

/** Error structures that are retrieved from the parser. */
sealed trait SyntaxErrorItem extends ErrorItem

/** Unlabelled error item. */
case class SyntaxRaw(item: String) extends SyntaxErrorItem {
    override def toString: String = s"\"$item\""
}
/** Labelled error item. */
case class SyntaxNamed(item: String) extends SyntaxErrorItem {
    override def toString: String = item
}
/** End of input error item. */
case object SyntaxEndOfInput extends SyntaxErrorItem {
    override def toString: String = "end of input"
}

/** Error structures that are retrieved from the semantic checker. */
sealed trait SemanticErrorItem extends ErrorItem

/** Variable error item. */
case class SemanticVar(item: String) extends SemanticErrorItem {
    override def toString: String = s"variable ${item}"
}
/** Function error item. */
case class SemanticFunc(item: String) extends SemanticErrorItem {
    override def toString: String = s"function call ${item}"
}
/** Type error item. */
case class SemanticType(item: SemType) extends SemanticErrorItem {
    override def toString: String = s"type $item"
}
/** Redeclaration error item. */
case class SemanticRedecl(item: ErrorItem) extends SemanticErrorItem {
    override def toString: String = s"redeclaration of ${item}"
}
/** Number of arguments error item. */
case class SemanticNumArg(num: Int) extends SemanticErrorItem {
    override def toString: String = s"$num number of arguments"
}
/** Return statement in the main body error item. */
case object SemanticReturnMain extends SemanticErrorItem {
    override def toString: String = "return statement in the main body"
}
