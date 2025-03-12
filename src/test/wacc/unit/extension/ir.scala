package wacc.unit

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inside.*

import wacc.error.errors.*
import wacc.extension.decompiler.*
import syntax.*
import wacc.backend.ir.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*

/** Helper function to parse an instruction. */
private def parseInstr(str: String): Either[WaccError, IRProgram] = parser.parse(str).toEither

/** Tests the parsing of expressions. */
class IRParserTests extends AnyFlatSpec {
    "Registers" should "be able to parse parameter registers" in {
        inside(parseInstr("push rax")) {
            case Right(List(
                Push(RAX(RegSize.QUAD_WORD))
            )) => succeed
        }
        inside(parseInstr("push eax")) {
            case Right(List(
                Push(RAX(RegSize.DOUBLE_WORD))
            )) => succeed
        }
        inside(parseInstr("push ax")) {
            case Right(List(
                Push(RAX(RegSize.WORD))
            )) => succeed
        }
        inside(parseInstr("push al")) {
            case Right(List(
                Push(RAX(RegSize.BYTE))
            )) => succeed
        }
    }

    they should "be able to parse special registers" in {
        inside(parseInstr("push rdi")) {
            case Right(List(
                Push(RDI(RegSize.QUAD_WORD))
            )) => succeed
        }
        inside(parseInstr("push edi")) {
            case Right(List(
                Push(RDI(RegSize.DOUBLE_WORD))
            )) => succeed
        }
        inside(parseInstr("push di")) {
            case Right(List(
                Push(RDI(RegSize.WORD))
            )) => succeed
        }
        inside(parseInstr("push dil")) {
            case Right(List(
                Push(RDI(RegSize.BYTE))
            )) => succeed
        }
    }

    they should "be able to parse numbered registers" in {
        inside(parseInstr("push r12")) {
            case Right(List(
                Push(R12(RegSize.QUAD_WORD))
            )) => succeed
        }
        inside(parseInstr("push r12d")) {
            case Right(List(
                Push(R12(RegSize.DOUBLE_WORD))
            )) => succeed
        }
        inside(parseInstr("push r12w")) {
            case Right(List(
                Push(R12(RegSize.WORD))
            )) => succeed
        }
        inside(parseInstr("push r12b")) {
            case Right(List(
                Push(R12(RegSize.BYTE))
            )) => succeed
        }
    }

    "Memory accesses" should "be able to parse simple memory access" in {
        inside(parseInstr("mov qword ptr [rsp], rbx")) {
            case Right(List(
                Mov(MemAccess(RSP(RegSize.QUAD_WORD), memoryOffsets.NO_OFFSET, RegSize.QUAD_WORD), RBX(RegSize.QUAD_WORD))
            )) => succeed
        }
    }

    "Memory accesses" should "be able to parse immediate memory access" in {
        inside(parseInstr("mov qword ptr [rsp + 4], rbx")) {
            case Right(List(
                Mov(MemAccess(RSP(RegSize.QUAD_WORD), 4, RegSize.QUAD_WORD), RBX(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "be able to parse register memory access" in {
        inside(parseInstr("mov r9, qword ptr [r9 + 4 * r10]")) {
            case Right(List(
                Mov(R9(RegSize.QUAD_WORD), MemRegAccess(R9(RegSize.QUAD_WORD), R10(RegSize.QUAD_WORD), 4, RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "be able to parse all memory pointer sizes" in {
        inside(parseInstr("mov qword ptr [rsp], rbx")) {
            case Right(List(
                Mov(MemAccess(RSP(RegSize.QUAD_WORD), memoryOffsets.NO_OFFSET, RegSize.QUAD_WORD), RBX(RegSize.QUAD_WORD))
            )) => succeed
        }
        inside(parseInstr("mov dword ptr [rsp], ebx")) {
            case Right(List(
                Mov(MemAccess(RSP(RegSize.QUAD_WORD), memoryOffsets.NO_OFFSET, RegSize.DOUBLE_WORD), RBX(RegSize.DOUBLE_WORD))
            )) => succeed
        }
        inside(parseInstr("mov word ptr [rsp], bx")) {
            case Right(List(
                Mov(MemAccess(RSP(RegSize.QUAD_WORD), memoryOffsets.NO_OFFSET, RegSize.WORD), RBX(RegSize.WORD))
            )) => succeed
        }
        inside(parseInstr("mov byte ptr [rsp], bl")) {
            case Right(List(
                Mov(MemAccess(RSP(RegSize.QUAD_WORD), memoryOffsets.NO_OFFSET, RegSize.BYTE), RBX(RegSize.BYTE))
            )) => succeed
        }
    }

    "Immediates" should "be able to parse positive numbers" in {
        inside(parseInstr("mov eax, 4")) {
            case Right(List(
                Mov(RAX(RegSize.DOUBLE_WORD), Imm(4))
            )) => succeed
        }
    }
    they should "be able to parse negative numbers" in {
        inside(parseInstr("mov eax, -4")) {
            case Right(List(
                Mov(RAX(RegSize.DOUBLE_WORD), Imm(-4))
            )) => succeed
        }
    }

    "Header instructions" should "parse intel syntax directive" in {
        inside(parseInstr(".intel_syntax noprefix")) {
            case Right(List(
                IntelSyntax
            )) => succeed
        }
    }
    they should "parse rodata section directive" in {
        inside(parseInstr(".section .rodata")) {
            case Right(List(
                SectionRoData
            )) => succeed
        }
    }
    they should "parse text section directive" in {
        inside(parseInstr(".text")) {
            case Right(List(
                Text
            )) => succeed
        }
    }
    they should "parse global directive" in {
        inside(parseInstr(".globl main")) {
            case Right(List(
                Global("main")
            )) => succeed
        }
    }

    "Stack instructions" should "parse push with quad word register" in {
        inside(parseInstr("push rax")) {
            case Right(List(
                Push(RAX(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse push with double word register" in {
        inside(parseInstr("push ebx")) {
            case Right(List(
                Push(RBX(RegSize.DOUBLE_WORD))
            )) => succeed
        }
    }
    they should "parse pop with quad word register" in {
        inside(parseInstr("pop rdx")) {
            case Right(List(
                Pop(RDX(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse pop with double word register" in {
        inside(parseInstr("pop ecx")) {
            case Right(List(
                Pop(RCX(RegSize.DOUBLE_WORD))
            )) => succeed
        }
    }

    "Arithmetic instructions" should "parse add with register operands" in {
        inside(parseInstr("add rax, rbx")) {
            case Right(List(
                Add(RAX(RegSize.QUAD_WORD), RBX(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse add with immediate operand" in {
        inside(parseInstr("add rax, 10")) {
            case Right(List(
                Add(RAX(RegSize.QUAD_WORD), Imm(10))
            )) => succeed
        }
    }
    they should "parse sub with register operands" in {
        inside(parseInstr("sub rcx, rdx")) {
            case Right(List(
                Sub(RCX(RegSize.QUAD_WORD), RDX(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse sub with immediate operand" in {
        inside(parseInstr("sub rcx, 20")) {
            case Right(List(
                Sub(RCX(RegSize.QUAD_WORD), Imm(20))
            )) => succeed
        }
    }
    they should "parse and with register operands" in {
        inside(parseInstr("and rsi, rdi")) {
            case Right(List(
                And(RSI(RegSize.QUAD_WORD), RDI(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse and with immediate operand" in {
        inside(parseInstr("and rsi, 15")) {
            case Right(List(
                And(RSI(RegSize.QUAD_WORD), Imm(15))
            )) => succeed
        }
    }
    they should "parse or with register operands" in {
        inside(parseInstr("or r8, r9")) {
            case Right(List(
                Or(R8(RegSize.QUAD_WORD), R9(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse or with immediate operand" in {
        inside(parseInstr("or r8, 7")) {
            case Right(List(
                Or(R8(RegSize.QUAD_WORD), Imm(7))
            )) => succeed
        }
    }
    they should "parse cmp with register operands" in {
        inside(parseInstr("cmp r10, r11")) {
            case Right(List(
                Cmp(R10(RegSize.QUAD_WORD), R11(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse cmp with immediate operand" in {
        inside(parseInstr("cmp r10, 0")) {
            case Right(List(
                Cmp(R10(RegSize.QUAD_WORD), Imm(0))
            )) => succeed
        }
    }
    they should "parse test with register operands" in {
        inside(parseInstr("test r12, r13")) {
            case Right(List(
                Test(R12(RegSize.QUAD_WORD), R13(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse test with immediate operand" in {
        inside(parseInstr("test r12, 1")) {
            case Right(List(
                Test(R12(RegSize.QUAD_WORD), Imm(1))
            )) => succeed
        }
    }
    they should "parse imul with three operands" in {
        inside(parseInstr("imul r14, r15, 4")) {
            case Right(List(
                MulImm(R14(RegSize.QUAD_WORD), R15(RegSize.QUAD_WORD), Imm(4))
            )) => succeed
        }
    }
    they should "parse imul with two operands (register, register)" in {
        inside(parseInstr("imul rax, rbx")) {
            case Right(List(
                Mul(RAX(RegSize.QUAD_WORD), RBX(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse imul with two operands (register, immediate)" in {
        inside(parseInstr("imul rcx, 8")) {
            case Right(List(
                Mul(RCX(RegSize.QUAD_WORD), Imm(8))
            )) => succeed
        }
    }
    they should "parse idiv with register operand" in {
        inside(parseInstr("idiv rdx")) {
            case Right(List(
                Div(RDX(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse sete instruction" in {
        inside(parseInstr("sete al")) {
            case Right(List(
                SetComp(RAX(RegSize.BYTE), CompFlag.E)
            )) => succeed
        }
    }
    they should "parse setne instruction" in {
        inside(parseInstr("setne cl")) {
            case Right(List(
                SetComp(RCX(RegSize.BYTE), CompFlag.NE)
            )) => succeed
        }
    }

    "Move instructions" should "parse mov with register operands" in {
        inside(parseInstr("mov rax, rbx")) {
            case Right(List(
                Mov(RAX(RegSize.QUAD_WORD), RBX(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse mov with immediate operand" in {
        inside(parseInstr("mov rax, 42")) {
            case Right(List(
                Mov(RAX(RegSize.QUAD_WORD), Imm(42))
            )) => succeed
        }
    }
    they should "parse mov with memory source" in {
        inside(parseInstr("mov rax, qword ptr [rbx]")) {
            case Right(List(
                Mov(RAX(RegSize.QUAD_WORD), MemAccess(RBX(RegSize.QUAD_WORD), memoryOffsets.NO_OFFSET, RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse mov with memory destination" in {
        inside(parseInstr("mov qword ptr [rbx], rax")) {
            case Right(List(
                Mov(MemAccess(RBX(RegSize.QUAD_WORD), memoryOffsets.NO_OFFSET, RegSize.QUAD_WORD), RAX(RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse mov with memory offset" in {
        inside(parseInstr("mov rax, qword ptr [rbx + 8]")) {
            case Right(List(
                Mov(RAX(RegSize.QUAD_WORD), MemAccess(RBX(RegSize.QUAD_WORD), 8, RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse mov with memory size specifier" in {
        inside(parseInstr("mov rax, qword ptr [rbx]")) {
            case Right(List(
                Mov(RAX(RegSize.QUAD_WORD), MemAccess(RBX(RegSize.QUAD_WORD), memoryOffsets.NO_OFFSET, RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse movzx instruction" in {
        inside(parseInstr("movzx eax, bl")) {
            case Right(List(
                Mov(RAX(RegSize.DOUBLE_WORD), RBX(RegSize.BYTE))
            )) => succeed
        }
    }
    they should "parse lea instruction" in {
        inside(parseInstr("lea rax, [rbx + 8]")) {
            case Right(List(
                Lea(RAX(RegSize.QUAD_WORD), MemAccess(RBX(RegSize.QUAD_WORD), 8, RegSize.QUAD_WORD))
            )) => succeed
        }
    }
    they should "parse cmove instruction" in {
        inside(parseInstr("cmove rax, rbx")) {
            case Right(List(
                CMov(RAX(RegSize.QUAD_WORD), RBX(RegSize.QUAD_WORD), CompFlag.E)
            )) => succeed
        }
    }
    they should "parse cmovne instruction" in {
        inside(parseInstr("cmovne rcx, rdx")) {
            case Right(List(
                CMov(RCX(RegSize.QUAD_WORD), RDX(RegSize.QUAD_WORD), CompFlag.NE)
            )) => succeed
        }
    }
    they should "parse cdq instruction" in {
        inside(parseInstr("cdq")) {
            case Right(List(
                ConvertDoubleToQuad
            )) => succeed
        }
    }

    "Control Flow instructions" should "parse ret instruction" in {
        inside(parseInstr("ret")) {
            case Right(List(
                Ret
            )) => succeed
        }
    }
    they should "parse call instruction" in {
        inside(parseInstr("call printf")) {
            case Right(List(
                Call(Label("printf"))
            )) => succeed
        }
    }
    they should "parse call instruction with @plt" in {
        inside(parseInstr("call printf@plt")) {
            case Right(List(
                Call(Label("printf"))
            )) => succeed
        }
    }
    they should "parse jmp instruction" in {
        inside(parseInstr("jmp label")) {
            case Right(List(
                Jump(Label("label"), JumpFlag.Unconditional)
            )) => succeed
        }
    }
    they should "parse je instruction" in {
        inside(parseInstr("je label")) {
            case Right(List(
                JumpComp(Label("label"), CompFlag.E)
            )) => succeed
        }
    }
    they should "parse jne instruction" in {
        inside(parseInstr("jne label")) {
            case Right(List(
                JumpComp(Label("label"), CompFlag.NE)
            )) => succeed
        }
    }
    they should "parse jl instruction" in {
        inside(parseInstr("jl label")) {
            case Right(List(
                JumpComp(Label("label"), CompFlag.L)
            )) => succeed
        }
    }
    they should "parse jle instruction" in {
        inside(parseInstr("jle label")) {
            case Right(List(
                JumpComp(Label("label"), CompFlag.LE)
            )) => succeed
        }
    }
    they should "parse jg instruction" in {
        inside(parseInstr("jg label")) {
            case Right(List(
                JumpComp(Label("label"), CompFlag.G)
            )) => succeed
        }
    }
    they should "parse jge instruction" in {
        inside(parseInstr("jge label")) {
            case Right(List(
                JumpComp(Label("label"), CompFlag.GE)
            )) => succeed
        }
    }
}
