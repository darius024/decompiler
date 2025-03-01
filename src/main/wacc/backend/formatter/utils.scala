package wacc.backend.formatter

import wacc.backend.ir.*
import flags.*
import registers.*

/** 
 * Formats a comparison flag for use in conditional instructions.
 * These flags determine when conditional operations are executed.
 */
def formatCompFlag(flag: CompFlag): String = flag match {
    case CompFlag.E  => "e"   // equal
    case CompFlag.NE => "ne"  // not equal
    case CompFlag.G  => "g"   // greater than (signed)
    case CompFlag.GE => "ge"  // greater than or equal (signed)
    case CompFlag.L  => "l"   // less than (signed)
    case CompFlag.LE => "le"  // less than or equal (signed)
}

/** 
 * Formats a jump flag for unconditional jumps or jumps on overflow.
 */
def formatJumpFlag(flag: JumpFlag): String = flag match {
    case JumpFlag.Overflow      => "o"   // jump if overflow
    case JumpFlag.Unconditional => "mp"  // unconditional jump
}

/** 
 * Returns the size specifier string for memory access operations.
 * This determines how many bytes are read/written in memory operations.
 */
def sizePtr(size: RegSize): String = size match {
    case RegSize.BYTE        => "byte"   // 1 byte
    case RegSize.WORD        => "word"   // 2 bytes
    case RegSize.DOUBLE_WORD => "dword"  // 4 bytes
    case RegSize.QUAD_WORD   => "qword"  // 8 bytes
}

/** 
 * Formats a parameter register (RAX, RBX, RCX, RDX) according to its size.
 * The register name changes based on how many bytes are being accessed.
 */
def parameterRegister(reg: String, size: RegSize): String = size match {
    case RegSize.BYTE        => s"${reg}l"   // low byte (al, bl, cl, dl)
    case RegSize.WORD        => s"${reg}x"   // 16-bit (ax, bx, cx, dx)
    case RegSize.DOUBLE_WORD => s"e${reg}x"  // 32-bit (eax, ebx, ecx, edx)
    case RegSize.QUAD_WORD   => s"r${reg}x"  // 64-bit (rax, rbx, rcx, rdx)
}

/** 
 * Formats a special register (RDI, RSI, RBP, RIP, RSP) according to its size.
 * The register name changes based on how many bytes are being accessed.
 */
def specialRegister(reg: String, size: RegSize): String = size match {
    case RegSize.BYTE        => s"${reg}l"   // low byte
    case RegSize.WORD        => s"${reg}"    // 16-bit
    case RegSize.DOUBLE_WORD => s"e${reg}"   // 32-bit
    case RegSize.QUAD_WORD   => s"r${reg}"   // 64-bit
}

/** 
 * Formats a numbered register (R8-R15) according to its size.
 * The register name changes based on how many bytes are being accessed.
 */
def numberedRegister(reg: String, size: RegSize): String = size match {
    case RegSize.BYTE        => s"r${reg}b"  // low byte
    case RegSize.WORD        => s"r${reg}w"  // 16-bit
    case RegSize.DOUBLE_WORD => s"r${reg}d"  // 32-bit
    case RegSize.QUAD_WORD   => s"r${reg}"   // 64-bit
}

/** 
 * Escapes special characters in string literals for proper assembly output.
 * Ensures that control characters are properly represented in the assembly.
 */
def formatString(name: String): String = name
    .replace("\u0000", "\\0")   // null character
    .replace("\b", "\\b")       // backspace
    .replace("\t", "\\t")       // tab
    .replace("\n", "\\n")       // newline
    .replace("\f", "\\f")       // form feed
    .replace("\r", "\\r")       // carriage return
    .replace("\\", "\\\\")      // backslash

/** 
 * Determines the appropriate size for operands in an instruction.
 * Ensures that operands have compatible sizes.
 */
def matchSize(operands: Seq[RegImmMemLabel]): RegSize = operands.match {
    case Seq(reg1: Register, reg2: Register) =>
        sizeToReg(Seq(reg1.size.size, reg2.size.size).min)
    case Seq(reg: Register, _)               => reg.size
    case Seq(_, reg: Register)               => reg.size
    case _                                   => RegSize.QUAD_WORD
}

/** 
 * Converts a byte count to the corresponding register size enum.
 */
def sizeToReg(size: Int): RegSize = size match {
    case 1 => RegSize.BYTE
    case 2 => RegSize.WORD
    case 4 => RegSize.DOUBLE_WORD
    case 8 => RegSize.QUAD_WORD
}
