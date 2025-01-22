package wacc

import parsley.Parsley
import parsley.token.{Lexer, Basic}
import parsley.token.descriptions.*
import parsley.token.errors.*

/** WACC Lexical Analyzer
 *
 * Implements lexical analysis for WACC language tokens:
 * - Identifiers (variables, functions)
 * - Keywords (control flow, types)
 * - Operators (arithmetic, logical)
 * - Literals (numbers, strings, characters)
 * - Comments (# to end of line)
 *
 * The lexer processes source text into a stream of tokens
 * that can be consumed by the parser.
 */

object lexer {
    // ========== Escape Sequences ==========
    // Handles special characters in strings/chars:
    // \0 (null), \b (backspace), \t (tab), \n (newline),
    // \f (form feed), \r (carriage return), \" (quote), \' (quote)
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
    // ========== Lexical Elements ==========
    // Configures identifier rules, keywords, operators
    private val desc = LexicalDesc(

        // Identifiers: Start with letter/underscore, followed by letters/digits/underscore
        NameDesc.plain.copy(
            identifierStart = Basic(c => Character.isLetter(c) || c == '_'),
            identifierLetter = Basic(c => Character.isLetterOrDigit(c) || c == '_'),
        ),

        // Keywords and Operators from WACC specification
        SymbolDesc.plain.copy(

            // Reserved keywords
            hardKeywords = Set(
                "int", "bool", "char", "string", "pair", 
                "begin", "end", "is", "skip", "return", "exit",
                "read", "free", "print", "println",
                "if", "then", "else", "fi", "while", "do", "done",
                "newpair", "fst", "snd", "call",
                "true", "false", "null", 
            ),
            // Operators for expressions
            hardOperators = Set(
                "+", "-", "*", "/", "%", 
                "==", "!=", "<", "<=", ">", ">=", 
                "&&", "||", 
                "!", "len", "ord", "chr",
            ),        
        ),

        // Numeric literals configured in WACC 
        NumericDesc.plain.copy(
            integerNumbersCanBeHexadecimal = false,
            integerNumbersCanBeOctal = false,
        ),

        // Textual literals for strings and characters
        TextDesc.plain.copy(
            escapeSequences = escDesc,
        ),


        SpaceDesc.plain.copy(
            lineCommentStart = "#",
        ),
    )

    // ========== Error Configurations ==========
    // Define custom error messages for different token types
    private val errConfig = new ErrorConfig {
        override def labelSymbol = List(
            List("!", "-", "len", "ord", "chr")
                .map(_ -> Label("unary operator")),
            List("*", "%", "/", "+", "-", ">", ">=", "<", "<=", "==", "!=", "&&", "||")
                .map(_ -> Label("binary operator")),
            List("read", "free", "return", "exit", "print", "println", "if", "while", "begin")
                .map(_ -> Label("statement")),
            List("then" -> Label("then branch")),
            List("else" -> Label("else branch")),
            List("do" -> Label("while body")),
            List("fi" -> Label("end of if statement")),
            List("done" -> Label("end of while statement")),
            List("end" -> Label("end of block")),
        ).flatten.toMap
    }
    
    // ========== Lexer Instance ==========
    // Create lexer with configured descriptions and error handling
    private val lexer = Lexer(desc, errConfig)

    // ========== Token Parsers ==========
    // Basic token type parsers
    val identifier = lexer.lexeme.names.identifier  
    val integer = lexer.lexeme.integer.decimal      
    val character = lexer.lexeme.character.ascii    
    val string = lexer.lexeme.string.ascii         

    // ========== Lexeme Combinators ==========
    // Higher-order parsers for common patterns
    def brackets[A](p: => Parsley[A]): Parsley[A] = lexer.lexeme.brackets(p)      
    def parens[A](p: => Parsley[A]): Parsley[A] = lexer.lexeme.parens(p)         
    def commaSep[A](p: Parsley[A]): Parsley[List[A]] = lexer.lexeme.commaSep(p)  
    def semiSep1[A](p: Parsley[A]): Parsley[List[A]] = lexer.lexeme.semiSep1(p)  

    // ========== Symbol Handling ==========
    // Handle symbols and whitespace
    val implicits = lexer.lexeme.symbol.implicits  
    def fully[A](p: Parsley[A]): Parsley[A] = lexer.fully(p)  
}
