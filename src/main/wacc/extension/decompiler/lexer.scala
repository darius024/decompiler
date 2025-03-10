package wacc.extension.decompiler

import parsley.Parsley
import parsley.token.{Lexer, Basic}
import parsley.token.descriptions.*

import wacc.backend.ir.flags.*

/** Describes the lexical rules of the parser. */
object lexer {
    // configure lexical description
    private val desc = LexicalDesc(
        NameDesc.plain.copy(
            identifierStart = Basic(c => Character.isLetter(c) || c == '_' || c == '.'),
            identifierLetter = Basic(c => Character.isLetterOrDigit(c) || c == '_' || c == '.'),
        ),

        SymbolDesc.plain.copy(
            // reserved keywords
            hardKeywords = Set(
                ".intel_syntax", "noprefix", ".globl",
                ".section", ".rodata", ".text", ".asciz", ".int",

                "qword", "dword", "word", "byte", "ptr",

                "push", "pop", "mov", "movzx", "lea",
                "call", "ret",

                "j", "set", "cmov",
                "jo", "jmp",

                "add", "sub", "imul", "idiv", "and", "or",
                "cmp", "test",
                "cdq",
            ),      
        ),

        NumericDesc.plain.copy(),

        TextDesc.plain.copy(),

        SpaceDesc.plain.copy(
            lineCommentStart = "#",
        ),
    )

    // lexer instance
    private val lexer = Lexer(desc)

    // basic token type parsers
    val identifier = lexer.lexeme.names.identifier
    val integer = lexer.lexeme.integer.decimal32
    val string = lexer.lexeme.string.ascii

    val comp = lexer.lexeme.symbol("e") .as(CompFlag.E)
             | lexer.lexeme.symbol("ne").as(CompFlag.NE)
             | lexer.lexeme.symbol("g") .as(CompFlag.G)
             | lexer.lexeme.symbol("ge").as(CompFlag.GE)
             | lexer.lexeme.symbol("l") .as(CompFlag.L)
             | lexer.lexeme.symbol("le").as(CompFlag.LE)

    val jump = lexer.lexeme.symbol("mp").as(JumpFlag.Unconditional)
             | lexer.lexeme.symbol("o") .as(JumpFlag.Overflow)

    val jumpCFlag = lexer.lexeme(lexer.nonlexeme.symbol("j")    ~> comp)
    val setFlag   = lexer.lexeme(lexer.nonlexeme.symbol("set")  ~> comp)
    val cmovFlag  = lexer.lexeme(lexer.nonlexeme.symbol("cmov") ~> comp)
    val jumpJFlag = lexer.lexeme(lexer.nonlexeme.symbol("j")    ~> jump)

    // higher-order parsers
    def brackets[A](p: => Parsley[A]): Parsley[A] = lexer.lexeme.brackets(p)
    def commaSep[A](p: Parsley[A]): Parsley[List[A]] = lexer.lexeme.commaSep(p)

    // symbols and whitespace parsers
    val implicits = lexer.lexeme.symbol.implicits
    def fully[A](p: Parsley[A]): Parsley[A] = lexer.fully(p)

    // information for the error builder
    def tokensList = Seq(
        lexer.nonlexeme.names.identifier.map(v => s"identifier $v"),
        lexer.nonlexeme.integer.decimal32.map(n => s"integer $n"),
        lexer.nonlexeme.string.ascii.map(s => s"string $s"),
        parsley.character.whitespace.map(_ => "whitespace"),
    ) ++ desc.symbolDesc.hardKeywords.map { k =>
        lexer.nonlexeme.symbol(k).as(s"keyword $k")
    }
}
