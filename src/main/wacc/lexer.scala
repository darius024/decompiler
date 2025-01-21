package wacc

import parsley.Parsley
import parsley.token.{Lexer, Basic}
import parsley.token.descriptions.*

object lexer {
    private val escDesc = EscapeDesc.plain.copy(
        literals = Set( '\"', '\'', '\\'),
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
                "newpair", "fst", "snd", "call", "null", 
                "true", "false", 
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
    private val lexer = Lexer(desc)

    val identifier = lexer.lexeme.names.identifier
    val integer = lexer.lexeme.integer.decimal32
    val character = lexer.lexeme.character.ascii
    val string = lexer.lexeme.string.ascii

    val implicits = lexer.lexeme.symbol.implicits
    def fully[A](p: Parsley[A]): Parsley[A] = lexer.fully(p)
}
