package wacc.extension.decompiler

import java.io.File
import parsley.{Parsley, Result, Failure}
import parsley.Parsley.{atomic, many}
import parsley.combinator.option
import scala.util.{Success => TrySuccess, Failure => TryFailure}

import wacc.backend.ir.*
import immediate.*
import instructions.*
import memory.*
import registers.*
import wacc.error.errors.*

import advancedErrors.*
import helpers.*
import lexer.*
import syntax.*
import syntaxErrors.*
import implicits.implicitSymbol

/** Formulates the grammar rules the parser should follow. */
object parser {
    // labels

    private lazy val label: Parsley[Label] =
        atomic(Label(identifier <~ option(":" | "@plt")))

    // immediates, register, and memory accesses

    private lazy val immediate: Parsley[Immediate] =
        Imm(integer)

    private lazy val register: Parsley[Register & SizedAs[RegSize]] = labelRegister
        ( paramRegisters
        | specialRegisters
        | numberedRegisters
        | _memory
        )
    
    private lazy val memoryAccess: Parsley[MemoryAccess] = MemoryPointer(option(
        ( "qword".as(RegSize.QUAD_WORD)
        | "dword".as(RegSize.DOUBLE_WORD)
        | "word".as(RegSize.WORD)
        | "byte".as(RegSize.BYTE)
        ) <~ "ptr"
    ), brackets(
        MemoryAcc(register, option(("+" | "-") ~> (integer | label)), option("*" ~> register))
    ))

    // combinations of operands

    private lazy val registerImmediate: Parsley[RegImm] =
        ( immediate
        | register
        )
        
    private lazy val registerMemory: Parsley[RegMem] =
        ( register
        | memoryAccess
        )

    private lazy val registerImmediateMemory: Parsley[RegImmMem] =
        ( registerImmediate
        | memoryAccess
        )

    // directives

    private lazy val directive: Parsley[StrLabel] =
        StrDirective(".int" ~> integer, label, ".asciz" ~> string)

    private lazy val header: Parsley[Instruction] =
        ( IntelSyntax.from(".intel_syntax" <~> "noprefix")
        | SectionRoData.from(".section" <~> ".rodata")
        | Text.from(".text")
        | Global(".globl" ~> identifier)
        )

    // instructions

    private lazy val stackInstr: Parsley[Instruction] =
        ( Push("push" ~> register)
        | Pop("pop" ~> register)
        )
    
    private lazy val arithmeticInstr: Parsley[Instruction] =
        ( Add ("add"  ~> register, "," ~> registerImmediate)
        | Sub ("sub"  ~> register, "," ~> registerImmediate)
        | And ("and"  ~> register, "," ~> registerImmediate)
        | Or  ("or"   ~> register, "," ~> registerImmediate)
        | Cmp ("cmp"  ~> register, "," ~> registerImmediate)
        | Test("test" ~> register, "," ~> registerImmediate)
        | atomic(MulImm("imul" ~> register, "," ~> register, "," ~> immediate))
        | Mul ("imul" ~> register, "," ~> registerImmediate)
        | Div ("idiv"                   ~> registerImmediate)
        | SetCompInstr(setFlag, register)
        )

    private lazy val movInstr: Parsley[Instruction] =
        ( Mov(("movzx" | "mov") ~> registerMemory, "," ~> registerImmediateMemory)
        | Lea("lea" ~> register, "," ~> memoryAccess)
        | CMovInstr(cmovFlag, register, "," ~> register)
        | ConvertDoubleToQuad.from("cdq")
        )

    private lazy val controlFlowInstr: Parsley[Instruction] =
        ( Ret.from("ret")
        | Call("call" ~> label)
        | atomic(JumpInstr(jumpJFlag, label))
        | JumpCompInstr(jumpCFlag, label)
        )

    private lazy val instruction: Parsley[Instruction] =
        ( movInstr
        | label
        | directive
        | header
        | stackInstr
        | arithmeticInstr
        | controlFlowInstr
        )

    // program

    private lazy val program: Parsley[IRProgram] = many(instruction)

    // top-level parsers
    
    private val parser = fully(program)

    def parse(file: File): Result[WaccError, IRProgram] = parser.parseFile[WaccError](file) match {
        case TrySuccess(result) => result
        case TryFailure(_)      => Failure(IOError)
    }

    def parse(input: String): Result[WaccError, IRProgram] = parser.parse[WaccError](input)
}

/** Helper functions used by the parser. */
object helpers {
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
