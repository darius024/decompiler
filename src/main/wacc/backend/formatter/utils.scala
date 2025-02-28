package wacc.backend.formatter

import wacc.backend.ir.*
import flags.*
import registers.*

def formatCompFlag(flag: CompFlag): String = flag match {
    case CompFlag.E  => "e"
    case CompFlag.NE => "ne"
    case CompFlag.G  => "g"
    case CompFlag.GE => "ge"
    case CompFlag.L  => "l"
    case CompFlag.LE => "le"
}

def formatJumpFlag(flag: JumpFlag): String = flag match {
    case JumpFlag.Overflow => "o"
    case JumpFlag.Unconditional => "mp"
}

def sizePtr(dim: RegSize) = dim match {
    case RegSize.BYTE        => "byte"
    case RegSize.WORD        => "word"
    case RegSize.DOUBLE_WORD => "dword"
    case RegSize.QUAD_WORD   => "qword"
}

def parameterRegister(reg: String, size: RegSize) = size match {
    case RegSize.BYTE        => s"${reg}l"
    case RegSize.WORD        => s"${reg}x"
    case RegSize.DOUBLE_WORD => s"e${reg}x"
    case RegSize.QUAD_WORD   => s"r${reg}x"
}

def specialRegister(reg: String, size: RegSize) = size match {
    case RegSize.BYTE        => s"${reg}l"
    case RegSize.WORD        => s"${reg}"
    case RegSize.DOUBLE_WORD => s"e${reg}"
    case RegSize.QUAD_WORD   => s"r${reg}"
}

def numberedRegister(reg: String, size: RegSize) = size match {
    case RegSize.BYTE        => s"r${reg}b"
    case RegSize.WORD        => s"r${reg}w"
    case RegSize.DOUBLE_WORD => s"r${reg}d"
    case RegSize.QUAD_WORD   => s"r${reg}"
}

def formatString(name: String): String = name
    .replace("\u0000", "\\0")   // null character
    .replace("\b", "\\b")       // backspace
    .replace("\t", "\\t")       // tab
    .replace("\n", "\\n")       // newline
    .replace("\f", "\\f")       // form feed
    .replace("\r", "\\r")       // carriage return
    .replace("\\", "\\\\")      // backslash
