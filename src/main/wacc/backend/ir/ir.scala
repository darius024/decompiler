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

/** Register instances and sizes. */
object registers {
    /** Complete enumeration of allowed register sizes. */
    enum RegSize(val size: Int) {
        case QUAD_WORD   extends RegSize(8)
        case DOUBLE_WORD extends RegSize(4)
        case WORD        extends RegSize(2)
        case BYTE        extends RegSize(1)
    }

    abstract class Register(val size: RegSize)
    /** Each register is represented as an unique object. */
    case class RAX(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class RBX(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class RCX(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class RDX(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class RSI(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class RDI(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class RSP(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class RBP(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class R8 (val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class R9 (val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class R10(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class R11(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class R12(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class R13(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class R14(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class R15(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
    case class RIP(val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)

    /** Temporary register used to translate from the first pass to the second. */
    case class TempReg(num: Int, val dim: RegSize = RegSize.QUAD_WORD) extends Register(dim)
}

/** Immediate values that instructions use. */
object immediate {
    sealed trait Immediate

    // TODO: add more types of immediates and safety-check the parameters
    case class Imm(value: Int) extends Immediate
}

/** Memory accesses that instructions use. */
object memory {
    sealed trait MemoryAccess

    case class MemAccess(reg: Register, offset: Int | Label = memoryOffsets.NO_OFFSET) extends MemoryAccess
    case class MemRegAccess(base: Register, reg: Register, coeff: Int) extends MemoryAccess
}

/** Intermediate representation of the backend phase. */
object instructions {
    type RegMem = Register | MemoryAccess
    type RegImm = Register | Immediate
    type RegImmMem = Register | Immediate | MemoryAccess

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

/** Flags for the conditional instructions. */
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

/** Constants used during code generation. */
object errorCodes {
    final val FAILURE = -1
    final val ARRAY_OUT_OF_BOUNDS = 1
    final val NULL_POINTER = 0
}

object memoryOffsets {
    final val FALSE = 0
    final val TRUE = 1
    final val DIV_ZERO = 0
    final val NO_OFFSET = 0
    final val NULL = 0
    final val ARRAY_LENGTH_OFFSET = -4
    final val STACK_ALIGNMENT = -16
    final val BOOL_PRINT_OFFSET = 24
    final val ARR_STORE1 = RegSize.BYTE.size
    final val ARR_STORE2 = RegSize.WORD.size
    final val ARR_STORE4 = RegSize.DOUBLE_WORD.size
    final val ARR_STORE8 = RegSize.QUAD_WORD.size
    final val ARR_LOAD1 = RegSize.BYTE.size
    final val ARR_LOAD2 = RegSize.WORD.size
    final val ARR_LOAD4 = RegSize.DOUBLE_WORD.size
    final val ARR_LOAD8 = RegSize.QUAD_WORD.size
}

object constants {
    final val MAX_CALL_ARGS = 6
    final val CHR = -128
}
