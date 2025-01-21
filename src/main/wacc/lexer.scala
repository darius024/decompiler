package wacc

import parsley.Parsley
import parsley.token.{Lexer, Basic}
import parsley.token.descriptions.*
import parsley.token.errors.*

object lexer {
    private val escDesc = EscapeDesc.plain.copy(
        literals = Set('\"', '\'', '\\'),
        mapping = Map(
            "0" -> 0x0000, 
            "b" -> 0x0008,
            "t" -> 0x0009,
            "n" -> 0x000a,
            "f" -> 0x000c,
            "r" -> 0x000d,
        ),
    )

    private val desc = LexicalDesc(
        NameDesc.plain.copy(
            identifierStart = Basic(c => Character.isLetter(c) || c == '_'),
            identifierLetter = Basic(c => Character.isLetterOrDigit(c) || c == '_'),
        ),

        SymbolDesc.plain.copy(
            hardKeywords = Set(
                "int", "bool", "char", "string", "pair", 
                "begin", "end", "is", "skip", "return", "exit",
                "read", "free", "print", "println",
                "if", "then", "else", "fi", "while", "do", "done",
                "newpair", "fst", "snd", "call",
                "true", "false", "null", 
            ),
            hardOperators = Set(
                "+", "-", "*", "/", "%", 
                "==", "!=", "<", "<=", ">", ">=", 
                "&&", "||", 
                "!", "len", "ord", "chr",
            ),        
        ),

        NumericDesc.plain.copy(
            integerNumbersCanBeHexadecimal = false,
            integerNumbersCanBeOctal = false,
        ),

        TextDesc.plain.copy(
            escapeSequences = escDesc,
        ),

        SpaceDesc.plain.copy(
            lineCommentStart = "#",
        ),
    )

    private val errConfig = new ErrorConfig {
        override def labelSymbol = List(
            List("!", "-", "len", "ord", "chr").map(_ -> Label("unary operator")),
            List("*", "%", "/", "+", "-", ">", ">=", "<", "<=", "==", "!=", "&&", "||").map(_ -> Label("binary operator")),
            List("read", "free", "return", "exit", "print", "println", "if", "while", "begin").map(_ -> Label("statement")),
            List("then" -> Label("then branch")),
            List("else" -> Label("else branch")),
            List("do" -> Label("while body")),
            List("fi" -> Label("end of if statement")),
            List("done" -> Label("end of while statement")),
            List("end" -> Label("end of block")),
        ).flatten.toMap
    }
        
    private val lexer = Lexer(desc, errConfig)

    val identifier = lexer.lexeme.names.identifier
    val integer = lexer.lexeme.integer.decimal
    val character = lexer.lexeme.character.ascii
    val string = lexer.lexeme.string.ascii

    // Lexeme combinators
    def brackets[A](p: => Parsley[A]): Parsley[A] = lexer.lexeme.brackets(p)
    def parens[A](p: => Parsley[A]): Parsley[A] = lexer.lexeme.parens(p)
    def commaSep[A](p: Parsley[A]): Parsley[List[A]] = lexer.lexeme.commaSep(p)
    def semiSep1[A](p: Parsley[A]): Parsley[List[A]] = lexer.lexeme.semiSep1(p)

    val implicits = lexer.lexeme.symbol.implicits
    def fully[A](p: Parsley[A]): Parsley[A] = lexer.fully(p)
}
