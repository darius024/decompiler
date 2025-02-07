package wacc.error

import wacc.semantics.scoping.semanticTypes.*
import wacc.syntax.bridges.Position

/** Semantic error that holds partial information about the error.
  * 
  * It deals with generating the error header, and not with the line information.
  */
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
    override val reasons = Set("the types of the values do not match")
}
case class TypeCannotBeInfered(val pos: Position) extends TypeError {
    override val reasons = Set("the type of the value unpacked from the pair cannot be inferred")
}
case class NumberArgumentsMismatch(unexpectedNum: Int, expectedNum: Int)(val pos: Position) extends TypeError {
    override val unexpected = Some(SemanticNumArg(unexpectedNum))
    override val expected = Set(SemanticNumArg(expectedNum))
    override val reasons = Set("the number of arguments does not match the function signature")
}
case class ReturnInMainBody(val pos: Position) extends TypeError {
    override val unexpected = Some(SemanticReturnMain)
    override val reasons = Set("return in the main body is not allowed")
}
