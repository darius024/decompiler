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
            List("!", "-", "len", "ord", "chr")
                .map(_ -> Label("unary operator")),
            List("*", "%", "/", "+", "-", ">", ">=", "<", "<=", "==", "!=", "&&", "||")
                .map(_ -> Label("binary operator")),
            List("read", "free", "return", "exit", "print", "println", "if", "while", "begin")
                .map(_ -> Label("statement")),
            List("then" -> LabelAndReason(
                                        reason = "then branch required", 
                                        label  = "then"
                                        )
                ),
            List("else" -> LabelAndReason(
                                        reason = "else branch required", 
                                        label  = "else"
                                        )
                ),
            List("do" -> LabelAndReason(
                                        reason = "do required to start while loop", 
                                        label  = "do"
                                        )
                ),
            List("fi" -> LabelAndReason(
                                        reason = "fi required to close if statement", 
                                        label  = "fi"
                                        )
                ),
            List("done" -> LabelAndReason(
                                        reason = "done required to close while loop", 
                                        label  = "done"
                                        )
                ),
            List("end" -> LabelAndReason(
                                        reason = "end required to close block", 
                                        label  = "end"
                                        )
                ),
            List("begin" -> LabelAndReason(
                                        reason = "begin required to start block", 
                                        label  = "begin"
                                        )
                ),
            List(";" -> LabelAndReason( 
                                        reason = "semicolon required to separate statements", 
                                        label  = "semicolon"
                                     )
                ),
            List("=" -> LabelAndReason(
                                        reason = "assignment operator required", 
                                        label  = "assignment"
                                        )
                ),
            List("[" -> LabelAndReason(
                                        reason = "array index/constructor operator required", 
                                        label  = "array index/constructor"
                                        )
                ),
            
            
                       
        ).flatten.toMap
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
