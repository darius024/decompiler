package wacc.error

import wacc.semantics.scoping.semanticTypes.*
import wacc.syntax.bridges.Position
import ErrorLines.*

sealed trait PartialSemanticError {
    val errorType: String
    val pos: Position
    val unexpected: Seq[String] = Nil
    val expected: Seq[String] = Nil
    val reasons: Seq[String] = Nil
}

/** Semantic errors that can occur during scope checking. */
sealed trait ScopeError extends PartialSemanticError {
    override val errorType: String = "Scope Error"
}

case class VariableNotInScope(name: String)(val pos: Position) extends ScopeError {
    override val unexpected = Seq(s"variable $name")
    override val reasons = Seq("variable has not been declared in this scope")
}
case class VariableAlreadyDeclared(name: String)(val pos: Position) extends ScopeError {
    override val unexpected = Seq(s"redeclaration of variable $name")
    override val reasons = Seq("variable has been previously declared in this scope")
}
case class FunctionNotDefined(funcName: String)(val pos: Position) extends ScopeError {
    override val unexpected = Seq(s"function $funcName")
    override val reasons = Seq("function has not been declared in this program")
}
case class FunctionAlreadyDeclared(funcName: String)(val pos: Position) extends ScopeError {
    override val unexpected = Seq(s"redeclaration of function $funcName")
    override val reasons = Seq("function has been previously declared in this scope")
}

/** Semantic errors that can occur during type checking. */
sealed trait TypeError extends PartialSemanticError {
    override val errorType: String = "Type Error"
}

case class TypeMismatch(unexpectedTy: SemType, expectedTy: Set[SemType])(val pos: Position) extends TypeError {
    override val unexpected = Seq(s"type $unexpectedTy")
    override val expected = Seq(s"type ${expectedTy.mkString(" or ")}")
    override val reasons = Seq("ensure that the types of all values match")
}
case class TypeCannotBeInfered(val pos: Position) extends TypeError {
    override val expected = Nil
    override val unexpected = Nil
    override val reasons = Seq("attempting to exchange values between pairs of unknown types",
                      "pair exchange is only legal when the type of at least one of the sides is known or specified")
}
case class NumberArgumentsMismatch(unexpectedNum: Int, expectedNum: Int)(val pos: Position) extends TypeError {
    override val unexpected = Seq(s"$unexpectedNum number of arguments")
    override val expected = Seq(s"$expectedNum number of arguments")
    override val reasons = Seq("ensure that the number of arguments match the function definition")
}
case class ReturnInMainBody(val pos: Position) extends TypeError {
    override val unexpected = Seq("return statement in the main body")
    override val expected = Nil
    override val reasons = Seq("return outside of function is not allowed")
}
