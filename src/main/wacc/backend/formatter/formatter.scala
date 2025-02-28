package wacc.backend.formatter

import java.io.File
import os.*

import wacc.backend.generator.*
import wacc.backend.ir.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*
import RegSize.*
import widgets.*

type RegImmMemLabel = RegImmMem | Label | String

/** Formats the assembly instructions produced by the code generator.
  * 
  * Constructs the assembly file at the root level of the project and prints to it.
  */
def format(codeGen: CodeGenerator, file: File): Unit = {
    // create and open a generic stream for IO
    val outputPath = os.pwd / s"${file.getName.stripSuffix(".wacc")}.s"
    val outputStream = os.write.outputStream(outputPath)

    try {
        formatIR(codeGen).foreach { instr =>
            outputStream.write(formatInstruction(instr).getBytes)
            outputStream.write("\n".getBytes)
        }
    } finally {
        outputStream.close()
    }
}

def formatIR(codeGen: CodeGenerator): List[Instruction] = {
       List(IntelSyntax, Global("main"), SectionRoData)
    ++ codeGen.data.flatMap(formatStrLabel) ++ List(Text) ++ codeGen.ir
    ++ codeGen.dependencies.toList.flatMap(formatWidget)
}

def formatStrLabel(strLabel: StrLabel): List[Instruction] = List(
    DirInt(strLabel.name.length), strLabel.label, Asciz(strLabel.name)
)

def formatWidget(widget: Widget): List[Instruction] =
    (if (widget.directives.isEmpty) {
        Nil
    } else {
        List(SectionRoData)
        ++ widget.directives.flatMap(formatStrLabel)
        ++ List(Text)
    })
    ++ List(widget.label)
    ++ widget.instructions

def formatRegister(reg: Register): String = reg match {
    // general purpose registers
    case RAX(size) => parameterRegister("a", size)
    case RBX(size) => parameterRegister("b", size)
    case RCX(size) => parameterRegister("c", size)
    case RDX(size) => parameterRegister("d", size)

    // parameter / special registers
    case RDI(size) => specialRegister("di", size)
    case RSI(size) => specialRegister("si", size)
    case RBP(size) => specialRegister("bp", size)
    case RIP(size) => specialRegister("ip", size)
    case RSP(size) => specialRegister("sp", size)

    // extended registers
    case R8 (size) => numberedRegister("8" , size)
    case R9 (size) => numberedRegister("9" , size)
    case R10(size) => numberedRegister("10", size)
    case R11(size) => numberedRegister("11", size)
    case R12(size) => numberedRegister("12", size)
    case R13(size) => numberedRegister("13", size)
    case R14(size) => numberedRegister("14", size)
    case R15(size) => numberedRegister("15", size)

    // TODO: remove this case
    case TempReg(num, size) => "TEMP_REG"
}

def formatOperand(op: RegImmMemLabel, size: RegSize = QUAD_WORD): String = op match {
    case reg: Register     => formatRegister(reg)
    case imm: Immediate    => formatImmediate(imm)
    case mem: MemoryAccess => formatMemAccess(mem, size)
    case label: Label      => label.name
    case str: String       => str
}

def formatDestOperand(op: RegMem): String = op match {
    case reg: Register     => formatRegister(reg)
    case mem: MemoryAccess => formatMemAccess(mem)
}

def formatInstruction(instr: Instruction): String = {
    def format(opcode: String, operands: RegImmMemLabel*): String =
        f"    $opcode%-6s ${operands.map(formatOperand(_)).mkString(", ")}"

    instr match {
        case IntelSyntax              => ".intel_syntax noprefix"
        case SectionRoData            => ".section .rodata"
        case Text                     => ".text"
        case Label(name)              => s"\n$name:"
        case Global(label)            => s".globl $label"
        case StrLabel(label, _)       => s".L.${label.name}:"
        case DirInt(size)             => format(".int", size.toString)
        case Asciz(name)              => format(".asciz", "\"$name\"")

        case Push(reg)                => format("push", reg)
        case Pop(reg)                 => format("pop" , reg)

        case Add(dest, src)           => format("add" , dest, src)
        case Sub(dest, src)           => format("sub" , dest, src)
        case Mul(dest, src1, src2)    => format("imul", dest, src1, src2)
        case Div(src)                 => format("idiv", src)
        case Mod(src)                 => format("idiv", src)
        case And(dest, src)           => format("and" , dest, src)
        case Or(dest, src)            => format("or"  , dest, src)

        // TODO: remove these unused instructions
        case Neg(dest, _)             => format("neg" , dest)
        case Not(dest, _)             => format("not" , dest)

        case CMov(dest, src, cond)    => format(s"cmov${formatCompFlag(cond)}", dest, src)
        case Mov(dest, src)           => format("mov" , dest, src)
        case Lea(dest, addr)          => format("lea" , dest, addr)

        case Call(label)              => format("call" , label)
        case Jump(label, flag)        => format(s"j${formatJumpFlag(flag)}", label)
        case Ret                      => format("ret")

        case Cmp(src1, src2)          => format("cmp" , src1, src2)
        case Test(src1, src2)         => format("test", src1, src2)
        case SetComp(dest, flag)      => format(s"set${formatCompFlag(flag)}", dest)
        case JumpComp(label, flag)    => format(s"j${formatCompFlag(flag)}", label)
        case ConvertDoubleToQuad      => format("cdq")
    }
}

def formatImmediate(imm: Immediate): String = imm match {
    case Imm(value) => s"$value"
}

def formatMemAccess(mem: MemoryAccess, size: RegSize = QUAD_WORD): String = {
    def sizePtr(dim: RegSize) = dim match {
        case BYTE        => "byte"
        case WORD        => "word"
        case DOUBLE_WORD => "dword"
        case QUAD_WORD   => "qword"
    }
    
    mem match {
        case MemAccess(reg: Register, offset: Int) => 
            val operand = if (offset == 0) s"[${formatRegister(reg)}]" else s"[${formatRegister(reg)} ${if offset > 0 then "+" else ""} $offset]"
            s"${sizePtr(size)} ptr $operand"
        
        case MemAccess(reg @ RIP(_), offset: Label) =>
            s"[${formatRegister(reg)} + ${offset.name}]"
        case MemAccess(reg: Register, offset: Label) =>
            s"${sizePtr(size)} ptr [${formatRegister(reg)} + ${offset.name}]"
        
        case MemRegAccess(base, reg, coeff) =>
            s"${sizePtr(size)} ptr [${formatRegister(base)} + ${formatRegister(reg)} * $coeff]"
    }
}

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

def formatString(name: String): String = name
    .replace("\u0000", "\\0")   // null character
    .replace("\b", "\\b")       // backspace
    .replace("\t", "\\t")       // tab
    .replace("\n", "\\n")       // newline
    .replace("\f", "\\f")       // form feed
    .replace("\r", "\\r")       // carriage return
    .replace("\\", "\\\\")      // backslash

def parameterRegister(reg: String, size: RegSize) = size match {
    case BYTE        => s"${reg}l"
    case WORD        => s"${reg}x"
    case DOUBLE_WORD => s"e${reg}x"
    case QUAD_WORD   => s"r${reg}x"
}

def specialRegister(reg: String, size: RegSize) = size match {
    case BYTE        => s"${reg}l"
    case WORD        => s"${reg}"
    case DOUBLE_WORD => s"e${reg}"
    case QUAD_WORD   => s"r${reg}"
}

def numberedRegister(reg: String, size: RegSize) = size match {
    case BYTE        => s"r${reg}b"
    case WORD        => s"r${reg}w"
    case DOUBLE_WORD => s"r${reg}d"
    case QUAD_WORD   => s"r${reg}"
}
