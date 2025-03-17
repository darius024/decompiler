package wacc.extension.decompiler

import parsley.Parsley
import parsley.token.{Lexer, Basic}
import parsley.token.descriptions.*

import wacc.backend.ir.*
import flags.*
import registers.*

import errorConfig.*

/** Describes the lexical rules of the parser. */
object lexer {
    // configure lexical description
    private val desc = LexicalDesc(
        // labels and registers
        NameDesc.plain.copy(
            identifierStart = Basic(c => Character.isLetter(c) || c == '_' || c == '.'),
            identifierLetter = Basic(c => Character.isLetterOrDigit(c) || c == '_' || c == '.'),
        ),
        
        // reserved keywords - directives, mnemonics
        SymbolDesc.plain.copy(
            hardKeywords = Set(
                ".intel_syntax", "noprefix", ".globl",
                ".section", ".rodata", ".text", ".asciz", ".int",

                "qword", "dword", "word", "byte", "ptr",

                "push", "pop", "mov", "movzx", "lea",
                "call", "ret",

                "cmove", "cmovne", "cmovge", "cmovg", "cmovle", "cmovl",
                "je", "jne", "jge", "jg", "jle", "jl",
                "sete", "setne", "setge", "setg", "setle", "setl",
                "jmp", "jo",

                "add", "sub", "imul", "idiv", "and", "or",
                "cmp", "test",
                "cdq",
                "leave"
            ),      
        ),

        NumericDesc.plain.copy(),

        // allow escape sequences within string literals
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
        ),

        // assembly comments
        SpaceDesc.plain.copy(
            lineCommentStart = "#",
        ),
    )

    // lexer instance
    private val lexer = Lexer(desc, errConfig)

    // basic token type parsers
    val identifier = lexer.lexeme.names.identifier
    val integer = lexer.lexeme.integer.decimal32
    val string = lexer.lexeme.string.ascii

    // comparison flag
    val comp = lexer.lexeme.symbol("e") .as(CompFlag.E)
             | lexer.lexeme.symbol("ne").as(CompFlag.NE)
             | lexer.lexeme.symbol("ge").as(CompFlag.GE)
             | lexer.lexeme.symbol("g") .as(CompFlag.G)
             | lexer.lexeme.symbol("le").as(CompFlag.LE)
             | lexer.lexeme.symbol("l") .as(CompFlag.L)

    // jump flag
    val jump = lexer.lexeme.symbol("mp").as(JumpFlag.Unconditional)
             | lexer.lexeme.symbol("o") .as(JumpFlag.Overflow)

    // mnemonics formed by comparison flags
    val jumpCFlag = lexer.lexeme(lexer.nonlexeme.symbol("j")    ~> comp)
    val jumpJFlag = lexer.lexeme(lexer.nonlexeme.symbol("j")    ~> jump)
    val setFlag   = lexer.lexeme(lexer.nonlexeme.symbol("set")  ~> comp)
    val cmovFlag  = lexer.lexeme(lexer.nonlexeme.symbol("cmov") ~> comp)

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

/** Helper functions used by the parser. */
object register {
    import lexer.implicits.implicitSymbol

    /** Parses a parameter register (ensure top-down matching order). */
    val paramRegisters: Parsley[Register & SizedAs[RegSize]] =
        ( "rax".as(RAX(RegSize.QUAD_WORD))
        | "eax".as(RAX(RegSize.DOUBLE_WORD))
        | "ax" .as(RAX(RegSize.WORD))
        | "al" .as(RAX(RegSize.BYTE))
        | "rbx".as(RBX(RegSize.QUAD_WORD))
        | "ebx".as(RBX(RegSize.DOUBLE_WORD))
        | "bx" .as(RBX(RegSize.WORD))
        | "bl" .as(RBX(RegSize.BYTE))
        | "rcx".as(RCX(RegSize.QUAD_WORD))
        | "ecx".as(RCX(RegSize.DOUBLE_WORD))
        | "cx" .as(RCX(RegSize.WORD))
        | "cl" .as(RCX(RegSize.BYTE))
        | "rdx".as(RDX(RegSize.QUAD_WORD))
        | "edx".as(RDX(RegSize.DOUBLE_WORD))
        | "dx" .as(RDX(RegSize.WORD))
        | "dl" .as(RDX(RegSize.BYTE))
        )

    /** Parses a special register (ensure top-down matching order). */
    val specialRegisters: Parsley[Register & SizedAs[RegSize]] =
        ( "rdi".as(RDI(RegSize.QUAD_WORD))
        | "edi".as(RDI(RegSize.DOUBLE_WORD))
        | "dil".as(RDI(RegSize.BYTE))
        | "di" .as(RDI(RegSize.WORD))
        | "rsi".as(RSI(RegSize.QUAD_WORD))
        | "esi".as(RSI(RegSize.DOUBLE_WORD))
        | "sil".as(RSI(RegSize.BYTE))
        | "si" .as(RSI(RegSize.WORD))
        | "rbp".as(RBP(RegSize.QUAD_WORD))
        | "ebp".as(RBP(RegSize.DOUBLE_WORD))
        | "bpl".as(RBP(RegSize.BYTE))
        | "bp" .as(RBP(RegSize.WORD))
        | "rip".as(RIP(RegSize.QUAD_WORD))
        | "eip".as(RIP(RegSize.DOUBLE_WORD))
        | "ipl".as(RIP(RegSize.BYTE))
        | "ip" .as(RIP(RegSize.WORD))
        | "rsp".as(RSP(RegSize.QUAD_WORD))
        | "esp".as(RSP(RegSize.DOUBLE_WORD))
        | "spl".as(RSP(RegSize.BYTE))
        | "sp" .as(RSP(RegSize.WORD))
        )
    
    /** Parses a numbered register (ensure top-down matching order). */
    val numberedRegisters: Parsley[Register & SizedAs[RegSize]] =
        ( "r8d" .as(R8(RegSize.DOUBLE_WORD))
        | "r8w" .as(R8(RegSize.WORD))
        | "r8b" .as(R8(RegSize.BYTE))
        | "r8"  .as(R8(RegSize.QUAD_WORD))
        | "r9d" .as(R9(RegSize.DOUBLE_WORD))
        | "r9w" .as(R9(RegSize.WORD))
        | "r9b" .as(R9(RegSize.BYTE))
        | "r9"  .as(R9(RegSize.QUAD_WORD))
        | "r10d".as(R10(RegSize.DOUBLE_WORD))
        | "r10w".as(R10(RegSize.WORD))
        | "r10b".as(R10(RegSize.BYTE))
        | "r10" .as(R10(RegSize.QUAD_WORD))
        | "r11d".as(R11(RegSize.DOUBLE_WORD))
        | "r11w".as(R11(RegSize.WORD))
        | "r11b".as(R11(RegSize.BYTE))
        | "r11" .as(R11(RegSize.QUAD_WORD))
        | "r12d".as(R12(RegSize.DOUBLE_WORD))
        | "r12w".as(R12(RegSize.WORD))
        | "r12b".as(R12(RegSize.BYTE))
        | "r12" .as(R12(RegSize.QUAD_WORD))
        | "r13d".as(R13(RegSize.DOUBLE_WORD))
        | "r13w".as(R13(RegSize.WORD))
        | "r13b".as(R13(RegSize.BYTE))
        | "r13" .as(R13(RegSize.QUAD_WORD))
        | "r14d".as(R14(RegSize.DOUBLE_WORD))
        | "r14w".as(R14(RegSize.WORD))
        | "r14b".as(R14(RegSize.BYTE))
        | "r14" .as(R14(RegSize.QUAD_WORD))
        | "r15d".as(R15(RegSize.DOUBLE_WORD))
        | "r15w".as(R15(RegSize.WORD))
        | "r15b".as(R15(RegSize.BYTE))
        | "r15" .as(R15(RegSize.QUAD_WORD))
        )
}
