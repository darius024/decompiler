package wacc

import parsley.Parsley
import parsley.token.{Lexer, Basic}
import parsley.token.descriptions.*
import parsley.token.errors.*

/** Describes the lexical rules of the parser. */
object lexer {
    // configure lexical description
    private val desc = LexicalDesc(
        NameDesc.plain.copy(
            // start with letter/underscore
            identifierStart = Basic(c => Character.isLetter(c) || c == '_'),
            // filled with letters/digits/underscores
            identifierLetter = Basic(c => Character.isLetterOrDigit(c) || c == '_'),
        ),

        SymbolDesc.plain.copy(
            // reserved keywords
            hardKeywords = Set(
                "int", "bool", "char", "string", "pair",
                "begin", "end", "is", "skip", "return", "exit",
                "read", "free", "print", "println",
                "if", "then", "else", "fi", "while", "do", "done",
                "newpair", "fst", "snd", "call",
                "true", "false", "null",
                "len", "ord", "chr",
            ),
            // operators
            hardOperators = Set(
                "+", "-", "*", "/", "%",
                "==", "!=", "<", "<=", ">", ">=",
                "&&", "||",
                "!",
            ),        
        ),

        // disable hex and oct numbers
        NumericDesc.plain.copy(
            integerNumbersCanBeHexadecimal = false,
            integerNumbersCanBeOctal = false,
        ),

        TextDesc.plain.copy(escapeSequences = EscapeDesc.plain.copy(
            literals = Set('\"', '\'', '\\'),
            mapping = Map(
                "0" -> 0x0000,  // null
                "b" -> 0x0008,  // backspace
                "t" -> 0x0009,  // tab
                "n" -> 0x000a,  // new line
                "f" -> 0x000c,  // form feed
                "r" -> 0x000d,  // carriage return
            )),
            graphicCharacter = Basic(c => !Set('\"', '\'', '\\', '\n').contains(c))
        ),

        SpaceDesc.plain.copy(
            lineCommentStart = "#",
        ),
    )

    // configure errors
    private val errConfig = new ErrorConfig {
        override def labelSymbol = List(
            List("!", "+", "-", "len", "ord", "chr")
                .map(_ -> Label("unary operator")),
            List("*", "%", "/", "+", "-", ">", ">=", "<", "<=", "==", "!=", "&&", "||")
                .map(_ -> Label("binary operator")),
            List("read", "free", "return", "exit", "print", "println", "if", "while", "begin")
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
            // List("end" -> LabelAndReason(
            //         reason = "unclosed scope, function, or main body", 
            //         label  = "end"
            //     )),
            List("begin" -> LabelAndReason(
                    reason = "new block required", 
                    label  = "begin"
                )),
            List(";" -> LabelAndReason( 
                    reason = "semicolon required to separate statements", 
                    label  = "semicolon"
                )),
            List("=" -> Label("assignment")),
            List("[" -> Label("array index")),          
        ).flatten.toMap

        override def labelEscapeEnd = LabelAndReason(
            reason = "valid escape sequences are \\0, \\n, \\t, \\b, \\f, \\r, \\\", \\\' or \\\\", 
            label  = "escape sequence"
        )
    }

    // lexer instance
    private val lexer = Lexer(desc, errConfig)

    // basic token type parsers
    val identifier = lexer.lexeme.names.identifier
    val integer = lexer.lexeme.integer.decimal32   
    val character = lexer.lexeme.character.ascii
    val string = lexer.lexeme.string.ascii

    // higher-order parsers
    def brackets[A](p: => Parsley[A]): Parsley[A] = lexer.lexeme.brackets(p)      
    def parens[A](p: => Parsley[A]): Parsley[A] = lexer.lexeme.parens(p)         
    def commaSep[A](p: Parsley[A]): Parsley[List[A]] = lexer.lexeme.commaSep(p)  
    def semiSep1[A](p: Parsley[A]): Parsley[List[A]] = lexer.lexeme.semiSep1(p)  

    // symbols and whitespace parsers
    val implicits = lexer.lexeme.symbol.implicits  
    def fully[A](p: Parsley[A]): Parsley[A] = lexer.fully(p)  
}
