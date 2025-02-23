package wacc.backend.ir

import immediate.*
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
    private final val QUAD_WORD = 64
    private final val DOUBLE_WORD = 32

    abstract class Register(size: Int = QUAD_WORD)
    case class RAX(val size: Int) extends Register(size)
    case class RBX(val size: Int) extends Register(size)
    case class RCX(val size: Int) extends Register(size)
    case class RDX(val size: Int) extends Register(size)
    case class RSI(val size: Int) extends Register(size)
    case class RDI(val size: Int) extends Register(size)
    case class RSP(val size: Int) extends Register(size)
    case class RBP(val size: Int) extends Register(size)
    case class R8 (val size: Int) extends Register(size)
    case class R9 (val size: Int) extends Register(size)
    case class R10(val size: Int) extends Register(size)
    case class R11(val size: Int) extends Register(size)
    case class R12(val size: Int) extends Register(size)
    case class R13(val size: Int) extends Register(size)
    case class R14(val size: Int) extends Register(size)
    case class R15(val size: Int) extends Register(size) 
}

object immediate {
    sealed trait Immediate

    // TODO: Add more types of immediates and safety-check the parameters
    case class Imm(value: Int) extends Immediate
}

object memory {
    sealed trait MemoryAccess

    case class MemAccess(reg: Register, offset: Int) extends MemoryAccess
}

object instructions {
    type RegMem = Register | MemAccess
    type RegImm = Register | Immediate
    type RegImmMem = Register | Immediate | MemAccess

    sealed trait Instruction

    // labels and directives
    sealed trait Directive extends Instruction

    case object SectionRoData extends Directive
    case object Text extends Directive
    case class Label(text: String) extends Directive
    case class Global(label: String) extends Directive
    case class StrLabel(name: String, size: Int) extends Directive

    // stack
    case class Push(reg: Register) extends Instruction
    case class Pop(reg: Register) extends Instruction

    // operations
    case class Add(dest: Register, src: RegImm) extends Instruction
    case class Sub(dest: Register, src: RegImm) extends Instruction
    case class Mul(dest: Register, src: RegImm) extends Instruction
    case class Mod(dest: Register, src: RegImm) extends Instruction
    case class Div(dest: Register, src: RegImm) extends Instruction
    case class And(dest: Register, src: RegImm) extends Instruction
    case class Or (dest: Register, src: RegImm) extends Instruction

    case class Neg(dest: Register, src1: RegImm) extends Instruction
    case class Not(dest: Register, src1: RegImm) extends Instruction

    // move
    case class Mov(dest: RegMem, src: RegImmMem) extends Instruction
    case class Lea(dest: Register, addr: MemAccess) extends Instruction

    // control flow
    case class Call(label: Label) extends Instruction
    case class Jump(label: Label) extends Instruction
    case object BranchError extends Instruction
    case object Return extends Instruction
}
