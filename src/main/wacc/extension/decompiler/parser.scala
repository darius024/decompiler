package wacc.extension.decompiler

import java.io.File
import parsley.{Parsley, Result, Failure}
import parsley.Parsley.{atomic, many}
import parsley.combinator.option
import scala.util.{Success => TrySuccess, Failure => TryFailure}

import helpers.*
import lexer.*
import syntax.*
import implicits.implicitSymbol

import wacc.backend.ir.*
import immediate.*
import instructions.*
import memory.*
import registers.*

/** Formulates the grammar rules the parser should follow. */
object parser {
    type IRProgram = List[Instruction]

    private lazy val label: Parsley[Label] =
        atomic(Label(identifier <~ option(":" | "@plt")))

    private lazy val immediate: Parsley[Immediate] =
        Imm(integer)

    private lazy val register: Parsley[Register & SizedAs[RegSize]] =
        ( paramRegisters
        | specialRegisters
        | numberedRegisters
        )
    
    private lazy val memoryAccess: Parsley[MemoryAccess] = brackets(
        MemoryAcc(register, option(("+" | "-") ~> integer), option("*" ~> register))
    )

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

    private lazy val directive: Parsley[StrLabel] =
        StrDirective(".int" ~> integer, label, ".asciz" ~> string)

    private lazy val header: Parsley[Instruction] =
        ( IntelSyntax.from(".intel_syntax" <~> "noprefix")
        | SectionRoData.from(".section" <~> ".rodata")
        | Text.from(".text")
        | Global(".globl" ~> identifier)
        )

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
        | Mod ("mod"                   ~> registerImmediate)
        | Div ("div"                   ~> registerImmediate)
        | SetCompInstr(setFlag, register)
        )

    private lazy val movInstr: Parsley[Instruction] =
        ( Mov(("mov" | "movzx") ~> registerMemory, "," ~> registerImmediateMemory)
        | Lea("lea" ~> register, "," ~> memoryAccess)
        | CMovInstr(cmovFlag, register, "," ~> register)
        | ConvertDoubleToQuad.from("cdq")
        )

    private lazy val controlFlowInstr: Parsley[Instruction] =
        ( Ret.from("ret")
        | Call("call" ~> label)
        | JumpInstr(jumpJFlag, label)
        | JumpCompInstr(jumpCFlag, label)
        )

    private lazy val instruction: Parsley[Instruction] =
        ( label
        | directive
        | header
        | stackInstr
        | arithmeticInstr
        | movInstr
        | controlFlowInstr
        )

    private lazy val program: Parsley[IRProgram] = many(instruction)

    // top-level parsers
    
    private val parser = fully(program)

    def parse(file: File): Result[String, IRProgram] = parser.parseFile[String](file) match {
        case TrySuccess(result) => result
        case TryFailure(_)      => Failure("could not open file")
    }

    def parse(input: String): Result[String, IRProgram] = parser.parse[String](input)
}

object helpers {
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

    val specialRegisters: Parsley[Register & SizedAs[RegSize]] =
        ( "rdi".as(RDI(RegSize.QUAD_WORD))
        | "edi".as(RDI(RegSize.DOUBLE_WORD))
        | "di" .as(RDI(RegSize.WORD))
        | "dil".as(RDI(RegSize.BYTE))
        | "rsi".as(RSI(RegSize.QUAD_WORD))
        | "esi".as(RSI(RegSize.DOUBLE_WORD))
        | "si" .as(RSI(RegSize.WORD))
        | "sil".as(RSI(RegSize.BYTE))
        | "rbp".as(RBP(RegSize.QUAD_WORD))
        | "ebp".as(RBP(RegSize.DOUBLE_WORD))
        | "bp" .as(RBP(RegSize.WORD))
        | "bpl".as(RBP(RegSize.BYTE))
        | "rip".as(RIP(RegSize.QUAD_WORD))
        | "eip".as(RIP(RegSize.DOUBLE_WORD))
        | "ip" .as(RIP(RegSize.WORD))
        | "ipl".as(RIP(RegSize.BYTE))
        | "rsp".as(RSP(RegSize.QUAD_WORD))
        | "esp".as(RSP(RegSize.DOUBLE_WORD))
        | "sp" .as(RSP(RegSize.WORD))
        | "spl".as(RSP(RegSize.BYTE))
        )
    
    val numberedRegisters: Parsley[Register & SizedAs[RegSize]] =
        ( "r8"  .as(R8(RegSize.QUAD_WORD))
        | "r8d" .as(R8(RegSize.DOUBLE_WORD))
        | "r8w" .as(R8(RegSize.WORD))
        | "r8b" .as(R8(RegSize.BYTE))
        | "r9"  .as(R9(RegSize.QUAD_WORD))
        | "r9d" .as(R9(RegSize.DOUBLE_WORD))
        | "r9w" .as(R9(RegSize.WORD))
        | "r9b" .as(R9(RegSize.BYTE))
        | "r10" .as(R10(RegSize.QUAD_WORD))
        | "r10d".as(R10(RegSize.DOUBLE_WORD))
        | "r10w".as(R10(RegSize.WORD))
        | "r10b".as(R10(RegSize.BYTE))
        | "r11" .as(R11(RegSize.QUAD_WORD))
        | "r11d".as(R11(RegSize.DOUBLE_WORD))
        | "r11w".as(R11(RegSize.WORD))
        | "r11b".as(R11(RegSize.BYTE))
        | "r12" .as(R12(RegSize.QUAD_WORD))
        | "r12d".as(R12(RegSize.DOUBLE_WORD))
        | "r12w".as(R12(RegSize.WORD))
        | "r12b".as(R12(RegSize.BYTE))
        | "r13" .as(R13(RegSize.QUAD_WORD))
        | "r13d".as(R13(RegSize.DOUBLE_WORD))
        | "r13w".as(R13(RegSize.WORD))
        | "r13b".as(R13(RegSize.BYTE))
        | "r14" .as(R14(RegSize.QUAD_WORD))
        | "r14d".as(R14(RegSize.DOUBLE_WORD))
        | "r14w".as(R14(RegSize.WORD))
        | "r14b".as(R14(RegSize.BYTE))
        | "r15" .as(R15(RegSize.QUAD_WORD))
        | "r15d".as(R15(RegSize.DOUBLE_WORD))
        | "r15w".as(R15(RegSize.WORD))
        | "r15b".as(R15(RegSize.BYTE))
        )
}
