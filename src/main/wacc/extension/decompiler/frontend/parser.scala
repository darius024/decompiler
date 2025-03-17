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
import register.*
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
        MemoryAcc(register, option(("+".as(true) | "-".as(false)) <~> (integer | label)), option("*" ~> register))
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
        ( BinaryInstruction(Add ("add"  ~> register, "," ~> registerImmediate))
        | BinaryInstruction(Sub ("sub"  ~> register, "," ~> registerImmediate))
        | BinaryInstruction(And ("and"  ~> register, "," ~> registerImmediate))
        | BinaryInstruction(Or  ("or"   ~> register, "," ~> registerImmediate))
        | BinaryInstruction(Cmp ("cmp"  ~> register, "," ~> registerImmediate))
        | BinaryInstruction(Test("test" ~> register, "," ~> registerImmediate))
        | atomic (MulImm("imul" ~> register, "," ~> register, "," ~> immediate))
        | Mul ("imul" ~> register, "," ~> registerImmediate)
        | Div ("idiv"                  ~> registerImmediate)
        | SetCompInstr(setFlag, register)
        )

    private lazy val movInstr: Parsley[Instruction] =
        ( MovInstr(Mov(("movzx" | "mov") ~> registerMemory, "," ~> registerImmediateMemory))
        | Lea("lea" ~> register, "," ~> memoryAccess)
        | CMovInstr(cmovFlag, register, "," ~> register)
        | ConvertDoubleToQuad.from("cdq")
        )

    private lazy val controlFlowInstr: Parsley[Instruction] =
        ( Ret.from("ret" | "leave")
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
