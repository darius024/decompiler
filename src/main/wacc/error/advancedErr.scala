package wacc.error

import parsley.Parsley
import parsley.Parsley.eof
import parsley.errors.combinator.*
import parsley.errors.patterns.{PreventativeErrors, VerifiedErrors}
import parsley.errors.VanillaGen
import parsley.quick.unit

import wacc.lexer.implicits.implicitSymbol

/** Advanced errors used by Parsley. */
object advancedErrors {
    lazy val _emptyMain = unit.verifiedExplain("missing main program body")
    lazy val _beforeMain = unit.verifiedExplain("all program body and function declarations must be within `begin` and `end`")
    lazy val _afterMain = eof
        | ";".verifiedExplain("semi-colons cannot follow the `end` of the program")
        | unit.verifiedExplain("all program body and function declarations must be within `begin` and `end`")

    lazy val _pointers = "*".preventWith(
        err = new VanillaGen[Unit] {
            override def reason(x: Unit) = Some("pointers are not supported by WACC")
            override def unexpected(x: Unit) = VanillaGen.NamedItem("pointer type")
        },
        labels = "identifier"
    )

    lazy val _func = "(".verifiedExplain("all functions must be declared at the top of the main block")
    lazy val _func_type = unit.verifiedExplain("function declaration has missing type")
    lazy val _func_expr = "(".preventativeExplain("function calls may not appear in expressions and must use `call`")
    lazy val _nested = "(".preventativeExplain("pair types may not be nested in WACC")
    lazy val _unclosed = unit.preventativeExplain("unclosed scope, function or main body")

    /** Helpers to improve errors. */
    def labelScope[A](p: Parsley[A]): Parsley[A] =
        p.explain("empty scopes are not allowed")

    def labelExpr[A](p: Parsley[A]): Parsley[A] =
        p.label("expression")
         .explain("expressions may start with: literals, identifier, unary operators, null, parantheses")
}
