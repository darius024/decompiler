package wacc.extension.decompiler

import parsley.Parsley
import parsley.errors.ErrorBuilder
import parsley.errors.combinator.*
import parsley.errors.tokenextractors.LexToken
import parsley.errors.patterns.VerifiedErrors
import parsley.token.errors.*
import parsley.quick.unit

import wacc.error.*
import errors.*

/** Error configurations for the lexer. */
object errorConfig {
    val errConfig = new ErrorConfig {
        override def labelSymbol = List(
            List(".intel_syntax", ".globl", ".section", ".text", ".asciz", ".int")
                .map(_ -> Label("directive")),
            List("noprefix")
                .map(_ -> Label("intel syntax language")),
            List(".rodata")
                .map(_ -> Label("data segment")),
            List("qword", "dword", "word", "byte", "ptr")
                .map(_ -> Label("memory access pointer")),
            List("push", "pop", "mov", "movzx", "lea", "call", "ret")
                .map(_ -> Label("instruction")),
            List("add", "sub", "imul", "idiv", "and", "or", "cmp", "test", "cdq")
                .map(_ -> Label("instruction")),
        ).flatten.toMap

        override def labelStringAsciiEnd(mult: Boolean, row: Boolean) = Label("end of string literal")
    }
}

/** Advanced errors used by Parsley. */
object advancedErrors {
    import lexer.implicits.implicitSymbol

    lazy val _memory = "[".verifiedExplain("operand cannot be a memory access")
    lazy val _notEnough = unit.verifiedExplain("not enough operands")

    /** Helpers to improve errors. */
    def labelRegister[A](p: Parsley[A]): Parsley[A] =
        p.label("register")
         .explain("registers must be valid in the x86-64 sytax")
}

/** Contains the implicit definition of the error builder. */
object syntaxErrors {
    import wacc.extension.decompiler.lexer.*
    
    // provide better messages by using token information from the lexer
    implicit val errorBuilder: ErrorBuilder[SyntaxError] = new SyntaxErrorBuilder with LexToken {
        def tokens = tokensList
    }
}
