package wacc.error

import parsley.Parsley
import parsley.Parsley.eof
import parsley.errors.combinator.*
import parsley.errors.patterns.{PreventativeErrors, VerifiedErrors}
import parsley.errors.VanillaGen
import parsley.token.errors.*
import parsley.quick.unit

/** Error configurations for the lexer. */
object errorConfig {
    val errConfig = new ErrorConfig {
        override def labelSymbol = List(
            List("!", "+", "-", "len", "ord", "chr")
                .map(_ -> Label("unary operator")),
            List("*", "%", "/", "+", ">", ">=", "<", "<=", "==", "!=", "&&", "||")
                .map(_ -> Label("binary operator")),
            List("skip", "read", "free", "return", "exit", "print", "println", "if", "while", "begin")
                .map(_ -> Label("statement")),
            List("int", "bool", "char", "string", "pair")
                .map(_ -> Label("type")),
            List("fst", "snd")
                .map(_ -> Label("pair element")),
            List("newpair", "null")
                .map(_ -> Label("pair literal")),
            List("call")
                .map(_ -> Label("function call")),
            List("then" -> LabelAndReason(
                    reason = "the condition of an if statement must be closed with `then`", 
                    label  = "then branch"
                )),
            List("else" -> LabelAndReason(
                    reason = "all if statements must have an else clause", 
                    label  = "else branch"
                )),
            List("do" -> LabelAndReason(
                    reason = "the condition of a while loop must be closed with `do`", 
                    label  = "do body"
                )),
            List("fi" -> LabelAndReason(
                    reason = "unclosed if statement", 
                    label  = "fi"
                )),
            List("done" -> LabelAndReason(
                    reason = "unclosed while loop", 
                    label  = "done"
                )),
            List("end" -> Label("end")),
            List(";" -> LabelAndReason( 
                    reason = "semicolon required to separate statements", 
                    label  = "semicolon"
                )),
            List("=" -> Label("assignment")),
            List("[" -> Label("array index")),
            List("(" -> Label("open paranthesis")),
        ).flatten.toMap

        override def labelEscapeEnd = LabelAndReason(
            reason = "valid escape sequences are \\0, \\n, \\t, \\b, \\f, \\r, \\\", \\\' or \\\\", 
            label  = "escape sequence"
        )

        override def verifiedCharBadCharsUsedInLiteral = BadCharsReason(Map(
            '\"'.toInt -> "double quotes must be escaped inside character literals",
            '\''.toInt -> "single quotes must be escaped inside character literals",
        ))

        override def labelIntegerDecimalEnd = Label("end of integer")
        override def labelCharAsciiEnd = Label("end of character literal")
        override def labelStringAsciiEnd(mult: Boolean, row: Boolean) = Label("end of string literal")
    }
}

/** Advanced errors used by Parsley. */
object advancedErrors {
    import wacc.frontend.lexer.implicits.implicitSymbol

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
