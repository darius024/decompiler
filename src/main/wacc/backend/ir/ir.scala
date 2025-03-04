package wacc.backend.ir

import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*

/** 
  * Calling Conventions for x86-64 Intel:
  * 
  * rax:                     --- caller-saved, return value
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

/** Register definitions and sizes. */
object registers {
    /** 
      * Register sizes in x86-64 architecture.
      * Each size represents a different number of bytes that can be accessed.
      */
    enum RegSize(val size: Int) {
        case QUAD_WORD   extends RegSize(8)  // 64-bit registers
        case DOUBLE_WORD extends RegSize(4)  // 32-bit registers
        case WORD        extends RegSize(2)  // 16-bit registers
        case BYTE        extends RegSize(1)  // 8-bit registers
    }

    trait SizedAs[S <: RegSize] {
        val size: RegSize
    }   

    sealed trait Register extends SizedAs[RegSize] {
        val size: RegSize
    }
    
    /** Each register is represented as an unique object. */
    case class RAX(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class RBX(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class RCX(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class RDX(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class RSI(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class RDI(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class RSP(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class RBP(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class RIP(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class R8 (val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class R9 (val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class R10(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class R11(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class R12(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class R13(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class R14(val size: RegSize = RegSize.QUAD_WORD) extends Register
    case class R15(val size: RegSize = RegSize.QUAD_WORD) extends Register

    /** 
      * Temporary register used during code generation.
      * These are replaced with real registers during register allocation.
      */
    case class TempReg(num: Int, val size: RegSize = RegSize.QUAD_WORD) extends Register {
        override def equals(obj: Any): Boolean = obj match {
            case that: TempReg => this.num == that.num
            case _ => false
        }

        override def hashCode(): Int = num.hashCode()
    }

    def RESULT_REG(size: RegSize = RegSize.QUAD_WORD): Register = RAX(size)
    def AUX_REG(size: RegSize = RegSize.QUAD_WORD): Register = RBX(size)

    def ARG1(size: RegSize = RegSize.QUAD_WORD): Register = RDI(size)
    def ARG2(size: RegSize = RegSize.QUAD_WORD): Register = RSI(size)
    def ARG3(size: RegSize = RegSize.QUAD_WORD): Register = RDX(size)
    def ARG4(size: RegSize = RegSize.QUAD_WORD): Register = RCX(size)
    def ARG5(size: RegSize = RegSize.QUAD_WORD): Register = R8(size)
    def ARG6(size: RegSize = RegSize.QUAD_WORD): Register = R9(size)

    def PTR_REG(size: RegSize = RegSize.QUAD_WORD): Register = R11(size)
    def PTR_ARG(size: RegSize = RegSize.QUAD_WORD): Register = R10(size)
    def PTR    (size: RegSize = RegSize.QUAD_WORD): Register = R9(size)

    def STACK_REG(size: RegSize = RegSize.QUAD_WORD): Register = RSP(size)
    def FRAME_REG(size: RegSize = RegSize.QUAD_WORD): Register = RBP(size)

    def DIV_REG(size: RegSize = RegSize.QUAD_WORD): Register = RDX(size)
}

/** Immediate values used in assembly instructions. */
object immediate {
    /** Base trait for all immediate values. */
    sealed trait Immediate extends SizedAs[RegSize]
    /** integer immediate value. */
    case class Imm(value: Int) extends Immediate {
        val size = RegSize.DOUBLE_WORD
    }
}

/** Memory access expressions for assembly instructions. */
object memory {
    /** Base trait for all memory access expressions. */
    sealed trait MemoryAccess extends SizedAs[RegSize] { 
        val base: Register 
    }

    /** 
     * Base register + offset memory access.
     * Can use either an integer offset or a label.
     */
    case class MemAccess(base: Register, offset: Int | Label = memoryOffsets.NO_OFFSET, val size: RegSize = RegSize.QUAD_WORD) extends MemoryAccess
    
    /**
     * Base register + index register * coefficient memory access.
     * Used for array indexing operations.
     */
    case class MemRegAccess(base: Register, reg: Register, coeff: Int, val size: RegSize = RegSize.QUAD_WORD) extends MemoryAccess
}

/** Intermediate representation of assembly instructions. */
object instructions {
    /** type aliases for common operand combinations. */
    type RegMem = Register | MemoryAccess
    type RegImm = Register | Immediate
    type RegImmMem = Register | Immediate | MemoryAccess

    /** Base trait for all assembly instructions. */
    sealed trait Instruction

    /** Assembly directives that control the assembler behavior. */
    sealed trait Directive extends Instruction

    /** Intel syntax directive. */
    case object IntelSyntax extends Directive
    /** AT&T syntax directive. */
    case object ATTSyntax extends Directive
    /** read-only data section directive. */
    case object SectionRoData extends Directive
    /** code section directive. */
    case object Text extends Directive
    /** label definition. */
    case class Label(name: String) extends Directive
    /** global symbol declaration. */
    case class Global(name: String) extends Directive
    /** string label with its content. */
    case class StrLabel(label: Label, name: String) extends Directive
    /** integer directive. */
    case class DirInt(size: Int) extends Directive
    /** ASCII string with zero termination. */
    case class Asciz(name: String) extends Directive

    /** Stack operations */
    /** push a value onto the stack. */
    case class Push(reg: Register) extends Instruction {
        require(reg.size == RegSize.QUAD_WORD)
    }
    /** pop a value from the stack. */
    case class Pop(reg: Register) extends Instruction {
        require(reg.size == RegSize.QUAD_WORD)
    }

    /** Comparison operations */
    /** compare two values. */
    case class Cmp[S <: RegSize] (dest: Register & SizedAs[S], src: RegImm & SizedAs[S]) extends Instruction
    /** set a register based on a comparison result. */
    case class SetComp(dest: Register, compFlag: CompFlag) extends Instruction {
        require(dest.size == RegSize.BYTE)
    }

    /** Arithmetic operations */
    /** add source to destination. */
    case class Add[S <: RegSize] (dest: Register & SizedAs[S], src: RegImm & SizedAs[S]) extends Instruction
    /** subtract source from destination. */
    case class Sub[S <: RegSize] (dest: Register & SizedAs[S], src: RegImm & SizedAs[S]) extends Instruction
    /** multiply source1 by source2 and store in destination. */
    case class Mul[S <: RegSize] (dest: Register & SizedAs[S], src: RegImm & SizedAs[S]) extends Instruction
    /** compute remainder of division. */
    case class Mod(src: RegImm) extends Instruction
    /** divide by source. */
    case class Div(src: RegImm) extends Instruction

    /** Logical operations */
    /** bitwise AND of destination and source. */
    case class And [S <: RegSize] (dest: Register & SizedAs[S], src: RegImm & SizedAs[S]) extends Instruction
    /** bitwise OR of destination and source. */
    case class Or  [S <: RegSize] (dest: Register & SizedAs[S], src: RegImm & SizedAs[S]) extends Instruction
    /** bitwise AND test (sets flags but doesn't store result). */
    case class Test[S <: RegSize] (dest: Register & SizedAs[S], src1: RegImm & SizedAs[S]) extends Instruction

    /** Data movement */
    /** move data from source to destination. */
    case class Mov[S <: RegSize] (dest: RegMem & SizedAs[S], src: RegImmMem & SizedAs[S]) extends Instruction
    /** load effective address. */
    case class Lea(dest: Register, addr: MemAccess) extends Instruction {
        require(dest.size == RegSize.QUAD_WORD)
    }
    /** conditional move based on flag. */
    case class CMov[S <: RegSize] (dest: Register & SizedAs[S], src: Register & SizedAs[S], cond: CompFlag) extends Instruction 

    /** Control flow */
    /** return from function. */
    case object Ret extends Instruction
    /** call a function. */
    case class Call(label: Label) extends Instruction
    /** jump to a label. */
    case class Jump(label: Label, jumpFlag: JumpFlag) extends Instruction
    /** jump to a label if condition is met. */
    case class JumpComp(label: Label, compFlag: CompFlag) extends Instruction

    /** sign extend EAX into EDX (used for division). */
    case object ConvertDoubleToQuad extends Instruction
}

/** Flags used for conditional operations. */
object flags {
    /** Comparison flags for conditional operations. */
    enum CompFlag {
        case E   // equal
        case NE  // not equal
        case G   // greater than (signed)
        case GE  // greater than or equal (signed)
        case L   // less than (signed)
        case LE  // less than or equal (signed)
    }

    /** Jump flags for unconditional and overflow jumps. */
    enum JumpFlag {
        case Overflow      // jump if overflow
        case Unconditional // jump unconditionally
    }
}

/** Error codes returned by the program. */
object errorCodes {
    final val FAILURE = -1
    final val ARRAY_OUT_OF_BOUNDS = 1
    final val NULL_POINTER = 0
}

/** Constants used in code generation. */
object constants {
    final val MAX_CALL_ARGS = 6  // maximum number of arguments passed in registers
    final val CHR = -128         // character range check
    final val BYTE = 8           // number of bits in a byte
    final val SUCCESS = 0        // success exit code
    final val STACK_ADDR = 16    // stack address offset in functions
}

/** Memory offsets used in the code generation. */
object memoryOffsets {
    final val FALSE = 0                  // boolean false value
    final val TRUE = 1                   // boolean true value
    final val DIV_ZERO = 0               // division by zero check
    final val NO_OFFSET = 0              // no offset in memory access
    final val NULL = 0                   // null pointer value
    final val ARRAY_LENGTH_OFFSET = -4   // offset to array length field
    final val STACK_ALIGNMENT = -16      // stack alignment for function calls
    final val STACK_READ = 16            // stack space for reads
    final val BOOL_PRINT_OFFSET = 24     // offset for printing booleans
}
