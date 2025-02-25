package wacc.backend.ir

import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*

/** Calling Conventions:
  * 
  * rax:                     --- caller-saved
  * rbx:                     --- callee-saved
  * rcx:   4th argument      --- caller-saved
  * rdx:   3rd argument      --- caller-saved
  * rsi:   2nd argument      --- caller-saved
  * rdi:   1st argument      --- caller-saved
  * rsp:   stack pointer     --- callee-saved
  * rbp:   base pointer      --- callee-saved
  * r8 :   5th argument      --- caller-saved
  * r9 :   6th argument      --- caller-saved
  * r10:                     --- caller-saved
  * r11:                     --- caller-saved
  * r12:                     --- callee-saved
  * r13:                     --- callee-saved
  * r14:                     --- callee-saved
  * r15:                     --- callee-saved
  */

object registers {
    final val QUAD_WORD   = 64
    final val DOUBLE_WORD = 32
    final val WORD        = 16
    final val BYTE        = 8

    abstract class Register(val size: Int)
    case class RAX(val dim: Int = QUAD_WORD) extends Register(dim)
    case class RBX(val dim: Int = QUAD_WORD) extends Register(dim)
    case class RCX(val dim: Int = QUAD_WORD) extends Register(dim)
    case class RDX(val dim: Int = QUAD_WORD) extends Register(dim)
    case class RSI(val dim: Int = QUAD_WORD) extends Register(dim)
    case class RDI(val dim: Int = QUAD_WORD) extends Register(dim)
    case class RSP(val dim: Int = QUAD_WORD) extends Register(dim)
    case class RBP(val dim: Int = QUAD_WORD) extends Register(dim)
    case class R8 (val dim: Int = QUAD_WORD) extends Register(dim)
    case class R9 (val dim: Int = QUAD_WORD) extends Register(dim)
    case class R10(val dim: Int = QUAD_WORD) extends Register(dim)
    case class R11(val dim: Int = QUAD_WORD) extends Register(dim)
    case class R12(val dim: Int = QUAD_WORD) extends Register(dim)
    case class R13(val dim: Int = QUAD_WORD) extends Register(dim)
    case class R14(val dim: Int = QUAD_WORD) extends Register(dim)
    case class R15(val dim: Int = QUAD_WORD) extends Register(dim)
    case class RIP(val dim: Int = QUAD_WORD) extends Register(dim)

    case class TempReg(num: Int, val dim: Int = QUAD_WORD) extends Register(dim)
    class Temporary {
        private var number = 0

        def next(size: Int = QUAD_WORD): TempReg = {
            number += 1
            TempReg(number, size)
        }
    }

    object initialValues {
        final val CLEAR = 0
    }
}

object immediate {
    sealed trait Immediate

    // TODO: Add more types of immediates and safety-check the parameters
    case class Imm(value: Int) extends Immediate
}

object memory {
    sealed trait MemoryAccess

    case class MemAccess(reg: Register, offset: Int | Label) extends MemoryAccess
    case class MemRegAccess(base: Register, reg: Register, coeff: Int) extends MemoryAccess
}

object instructions {
    type RegMem = Register | MemoryAccess
    type RegImm = Register | Immediate
    type RegImmMem = Register | Immediate | MemAccess

    sealed trait Instruction

    // directives
    sealed trait Directive extends Instruction

    case object IntelSyntax extends Directive
    case object SectionRoData extends Directive
    case object Text extends Directive
    case class Label(name: String) extends Directive
    case class Global(name: String) extends Directive
    case class StrLabel(label: Label, name: String) extends Directive
    case class DirInt(size: Int) extends Directive
    case class Asciz(name: String) extends Directive

    // stack
    case class Push(reg: Register) extends Instruction
    case class Pop(reg: Register) extends Instruction

    // comparison
    case class Cmp(dest: Register, src: RegImm) extends Instruction
    case class SetComp(dest: Register, compFlag: CompFlag) extends Instruction

    // arithmetic operations
    case class Add(dest: Register, src: RegImm) extends Instruction
    case class Sub(dest: Register, src: RegImm) extends Instruction

    case class Mul(dest: Register, src1: RegImm, src2: RegImm) extends Instruction
    case class Mod(src: RegImm) extends Instruction
    case class Div(src: RegImm) extends Instruction

    // boolean operations
    case class And(dest: Register, src: RegImm) extends Instruction
    case class Or (dest: Register, src: RegImm) extends Instruction
    case class Neg(dest: Register, src: RegImm) extends Instruction
    case class Not(dest: Register, src: RegImm) extends Instruction
    case class Test(dest: Register, src1: RegImm) extends Instruction

    // move
    case class Mov(dest: RegMem, src: RegImmMem) extends Instruction
    case class Lea(dest: Register, addr: MemAccess) extends Instruction
    case class CMov(dest: Register, src: Register, cond: CompFlag) extends Instruction

    // control flow
    case object Ret extends Instruction
    case class Call(label: Label) extends Instruction
    case class Jump(label: Label, jumpFlag: JumpFlag) extends Instruction
    case class JumpComp(label: Label, compFlag: CompFlag) extends Instruction

    // sign extend EAX into EDX
    case object ConvertDoubleToQuad extends Instruction
}

object flags {
    enum CompFlag {
        case E
        case NE
        case G
        case GE
        case L
        case LE
    }

    enum JumpFlag {
        case Overflow
        case Unconditional
    }
}

object errorCodes {
    final val FAILURE = -1
    final val ARRAY_OUT_OF_BOUNDS = 1
    final val NULL_POINTER = 0
}

object memoryOffsets {
    final val NO_OFFSET = 0
    final val ARRAY_LENGTH_OFFSET = -4
    final val STACK_ALIGNMENT = -16
    final val BOOL_PRINT_OFFSET = 24
    final val ARR_STORE1 = 1
    final val ARR_STORE2 = 2
    final val ARR_STORE4 = 4
    final val ARR_STORE8 = 8
}
