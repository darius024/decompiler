package wacc.error

import wacc.semantics.scoping.semanticTypes.*
import wacc.syntax.bridges.Position
import ErrorLines.*

sealed trait SemanticErrorItem extends ErrorItem
case class SemanticVar(item: String) extends SemanticErrorItem {
    override def toString: String = s"variable ${item}"
}
case class SemanticFunc(item: String) extends SemanticErrorItem {
    override def toString: String = s"function call ${item}"
}
case class SemanticType(item: SemType) extends SemanticErrorItem {
    override def toString: String = s"type $item"
}
case class SemanticRedecl(item: ErrorItem) extends SemanticErrorItem {
    override def toString: String = s"redeclaration of ${item}"
}
case class SemanticNumArg(num: Int) extends SemanticErrorItem {
    override def toString: String = s"$num number of arguments"
}
case object SemanticReturnMain extends SemanticErrorItem {
    override def toString: String = "return statement in the main body"
}

sealed trait PartialSemanticError {
    val errorType: String
    val pos: Position
    val unexpected: Option[ErrorItem] = None
    val expected: Set[ErrorItem] = Set()
    val reasons: Set[String] = Set()
}

/** Semantic errors that can occur during scope checking. */
sealed trait ScopeError extends PartialSemanticError {
    override val errorType: String = "Scope Error"
}

case class VariableNotInScope(name: String)(val pos: Position) extends ScopeError {
    override val unexpected = Some(SemanticVar(name))
    override val reasons = Set("variable has not been declared in this scope")
}
case class VariableAlreadyDeclared(name: String)(val pos: Position) extends ScopeError {
    override val unexpected = Some(SemanticRedecl(SemanticVar(name)))
    override val reasons = Set("variable has been previously declared in this scope")
}
case class FunctionNotDefined(funcName: String)(val pos: Position) extends ScopeError {
    override val unexpected = Some(SemanticFunc(funcName))
    override val reasons = Set("function has not been declared in this program")
}
case class FunctionAlreadyDeclared(funcName: String)(val pos: Position) extends ScopeError {
    override val unexpected = Some(SemanticRedecl(SemanticFunc(funcName)))
    override val reasons = Set("function has been previously declared in this scope")
}

/** Semantic errors that can occur during type checking. */
sealed trait TypeError extends PartialSemanticError {
    override val errorType: String = "Type Error"
}

case class TypeMismatch(unexpectedTy: SemType, expectedTy: Set[SemType])(val pos: Position) extends TypeError {
    override val unexpected = Some(SemanticType(unexpectedTy))
    override val expected = expectedTy.map(SemanticType(_))
    override val reasons = Set("ensure that the types of all values match")
}
case class TypeCannotBeInfered(val pos: Position) extends TypeError {
    override val reasons = Set(
        "attempting to exchange values between pairs of unknown types",
        "pair exchange is only legal when the type of at least one of the sides is known or specified",
    )
}
case class NumberArgumentsMismatch(unexpectedNum: Int, expectedNum: Int)(val pos: Position) extends TypeError {
    override val unexpected = Some(SemanticNumArg(unexpectedNum))
    override val expected = Set(SemanticNumArg(expectedNum))
    override val reasons = Set("ensure that the number of arguments match the function definition")
}
case class ReturnInMainBody(val pos: Position) extends TypeError {
    override val unexpected = Some(SemanticReturnMain)
    override val reasons = Set("return outside of function is not allowed")
}
