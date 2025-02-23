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
    final val QUAD_WORD = 64
    final val DOUBLE_WORD = 32
    final val WORD        = 16
    final val HALF_WORD   = 8

    abstract class Register(size: Int)
    case class RAX(val size: Int = QUAD_WORD) extends Register(size)
    case class RBX(val size: Int = QUAD_WORD) extends Register(size)
    case class RCX(val size: Int = QUAD_WORD) extends Register(size)
    case class RDX(val size: Int = QUAD_WORD) extends Register(size)
    case class RSI(val size: Int = QUAD_WORD) extends Register(size)
    case class RDI(val size: Int = QUAD_WORD) extends Register(size)
    case class RSP(val size: Int = QUAD_WORD) extends Register(size)
    case class RBP(val size: Int = QUAD_WORD) extends Register(size)
    case class R8 (val size: Int = QUAD_WORD) extends Register(size)
    case class R9 (val size: Int = QUAD_WORD) extends Register(size)
    case class R10(val size: Int = QUAD_WORD) extends Register(size)
    case class R11(val size: Int = QUAD_WORD) extends Register(size)
    case class R12(val size: Int = QUAD_WORD) extends Register(size)
    case class R13(val size: Int = QUAD_WORD) extends Register(size)
    case class R14(val size: Int = QUAD_WORD) extends Register(size)
    case class R15(val size: Int = QUAD_WORD) extends Register(size) 

    case class TempReg(num: Int, val size: Int = QUAD_WORD) extends Register(size)
    class Temporary {
        private var number = 0

        def next(size: Int = QUAD_WORD): TempReg = {
            number += 1
            TempReg(number, size)
        }
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
}

object instructions {
    type RegMem = Register | MemAccess
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

    // control flow
    case object BranchError extends Instruction
    case object Ret extends Instruction
    case class FuncCall(label: Label) extends Instruction
    case class Jump(label: Label, jumpFlag: JumpFlag) extends Instruction
    case class JumpComp(label: Label, compFlag: CompFlag) extends Instruction
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
