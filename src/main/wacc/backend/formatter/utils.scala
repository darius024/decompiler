package wacc.backend.formatter

import wacc.backend.ir.*
import flags.*
import registers.*

/** Prints a comparison flag. */
def formatCompFlag(flag: CompFlag): String = flag match {
    case CompFlag.E  => "e"
    case CompFlag.NE => "ne"
    case CompFlag.G  => "g"
    case CompFlag.GE => "ge"
    case CompFlag.L  => "l"
    case CompFlag.LE => "le"
}

/** Prints a jump flag. */
def formatJumpFlag(flag: JumpFlag): String = flag match {
    case JumpFlag.Overflow      => "o"
    case JumpFlag.Unconditional => "mp"
}

/** Prints the size of a memory dereference. */
def sizePtr(size: RegSize) = size match {
    case RegSize.BYTE        => "byte"
    case RegSize.WORD        => "word"
    case RegSize.DOUBLE_WORD => "dword"
    case RegSize.QUAD_WORD   => "qword"
}

/** Prints a parameter register: RAX, RBX, RCX, RDX. */
def parameterRegister(reg: String, size: RegSize) = size match {
    case RegSize.BYTE        => s"${reg}l"
    case RegSize.WORD        => s"${reg}x"
    case RegSize.DOUBLE_WORD => s"e${reg}x"
    case RegSize.QUAD_WORD   => s"r${reg}x"
}

/** Prints a special register: RDI, RSI, RBP, RIP, RSP. */
def specialRegister(reg: String, size: RegSize) = size match {
    case RegSize.BYTE        => s"${reg}l"
    case RegSize.WORD        => s"${reg}"
    case RegSize.DOUBLE_WORD => s"e${reg}"
    case RegSize.QUAD_WORD   => s"r${reg}"
}

/** Prints a numbered register. */
def numberedRegister(reg: String, size: RegSize) = size match {
    case RegSize.BYTE        => s"r${reg}b"
    case RegSize.WORD        => s"r${reg}w"
    case RegSize.DOUBLE_WORD => s"r${reg}d"
    case RegSize.QUAD_WORD   => s"r${reg}"
}

/** Replaces escaped characters from the string literals to be well printed. */
def formatString(name: String): String = name
    .replace("\u0000", "\\0")   // null character
    .replace("\b", "\\b")       // backspace
    .replace("\t", "\\t")       // tab
    .replace("\n", "\\n")       // newline
    .replace("\f", "\\f")       // form feed
    .replace("\r", "\\r")       // carriage return
    .replace("\\", "\\\\")      // backslash

/** Ensures all operands match in size. */
def matchSize(operands: Seq[RegImmMemLabel]): RegSize = operands.match {
    case Seq(reg1: Register, reg2: Register) =>
        sizeToReg(Seq(reg1.size.size, reg2.size.size).min)
    case Seq(reg: Register, _) => reg.size
    case Seq(_, reg: Register) => reg.size
    case _                 => RegSize.QUAD_WORD
}

/** Transforms the number of bytes into the corresponding type. */
def sizeToReg(size: Int): RegSize = size match {
    case 1 => RegSize.BYTE
    case 2 => RegSize.WORD
    case 4 => RegSize.DOUBLE_WORD
    case 8 => RegSize.QUAD_WORD
}
