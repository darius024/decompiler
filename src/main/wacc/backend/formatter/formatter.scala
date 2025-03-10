package wacc.backend.formatter

import java.io.OutputStream

import wacc.backend.generator.*
import wacc.backend.ir.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*
import widgets.*

/** Shared type of all operands that can appear in assembly instructions. */
type RegImmMemLabel = RegImmMem | Label | String

enum SyntaxStyle {
    case Intel
    case ATT
}

/**
  * Formats the assembly instructions produced by the code generator.
  * 
  * This module handles the conversion from IR instructions to actual assembly text,
  * writing the output to a .s file with the same name as the input WACC file.
  */
def format(codeGen: CodeGenerator, syntax: SyntaxStyle)
          (using outputStream: OutputStream): Unit = {
    try {
        formatHeader(syntax)
        formatBlock(codeGen.data, syntax)
        format(codeGen.instrs, syntax)
        formatWidgets(codeGen.dependencies, syntax)
    } catch {
        case e: Throwable =>
            println(s"Could not write to the output stream: ${e.getMessage}")
    }
    finally {
        outputStream.close()
    }
}

/** Formats a block of instructions by writing each one on a new line. */
def format(instructions: List[Instruction], syntax: SyntaxStyle)
          (using outputStream: OutputStream): Unit = instructions.foreach { instr =>
    // write directly to the output stream to save memory
    // (avoids accumulating the entire text in memory)
    outputStream.write(formatInstruction(instr, syntax).getBytes)
    outputStream.write("\n".getBytes)
}

def formatHeader(syntax: SyntaxStyle) (using outputStream: OutputStream): Unit = {
    syntax match {
        case SyntaxStyle.Intel => format(List(IntelSyntax, Global("main")), syntax)
        case SyntaxStyle.ATT   => format(List(Global("main")), syntax)
    }
}

/** 
  * Formats the data segment containing string literals.
  * Creates the .rodata section and adds all string constants.
  */
def formatBlock(directives: Set[StrLabel], syntax: SyntaxStyle)
               (using outputStream: OutputStream): Unit = {
    // mark the beginning of the data segment
    format(List(SectionRoData), syntax)
    if (!directives.isEmpty) {
        directives.map(formatDirective(_, syntax))
    }
    // mark the beginning of the code segment
    format(List(Text), syntax)
}

/** Formats a string directive with its size and value. */
def formatDirective(strLabel: StrLabel, syntax: SyntaxStyle)
                   (using outputStream: OutputStream): Unit = {
    val StrLabel(label, name) = strLabel

    format(List(DirInt(name.length), label, Asciz(name)), syntax)
}

/** 
  * Formats runtime support functions (widgets).
  * Each widget has its own data and code segments.
  */
def formatWidgets(widgets: Set[Widget], syntax: SyntaxStyle)
                 (using outputStream: OutputStream): Unit = widgets.foreach { widget =>
    formatBlock(widget.directives, syntax)
    format(List(widget.label), syntax)
    format(widget.instructions, syntax)
}

/** Transforms IR instructions into their corresponding assembly text. */
def formatInstruction(instr: Instruction, syntax: SyntaxStyle): String = {
    /** Helper function to format instructions with uniform spacing. */
    def format(opcode: String, operands: RegImmMemLabel*): String = {
        var ops = operands
        val size = syntax match {
            case SyntaxStyle.Intel => ""
            case SyntaxStyle.ATT   =>
                ops = operands.toList.reverse
                ops.toList match {
                    case (reg: RegMem) :: _ => s"${sizePtrATT(reg.size)}"
                    case _                  => ""
                }
        }
        ops match {
            case Seq(reg1: Register, reg2: Register) if reg1.size.size > reg2.size.size => 
                f"    $opcode%-6s$size ${List(formatOperand(reg1, syntax), formatOperand(reg2, syntax)).mkString(", ")}"
            case _ =>
                f"    $opcode%-6s$size ${ops.map(formatOperand(_, syntax)).mkString(", ")}"
        }
    }

    instr match {
        // assembly directives
        case IntelSyntax            => ".intel_syntax noprefix"
        case SectionRoData          => ".section .rodata"
        case Text                   => ".text"
        case Label(name)            => s"$name:"
        case Global(label)          => s".globl $label"
        case StrLabel(label, _)     => s".L.${label.name}:"
        case DirInt(size)           => format(".int", size.toString)
        case Asciz(name)            => format(".asciz", s"\"${formatString(name)}\"")

        // stack operations
        case Push(reg)              => format("push", reg)
        case Pop(reg)               => format("pop" , reg)

        // arithmetic operations
        case Add(dest, src)         => format("add" , dest, src)
        case Sub(dest, src)         => format("sub" , dest, src)
        case Mul(dest, src)         => format("imul", dest, src)
        case MulImm(dest, src, imm) => format("imul", dest, src, imm)
        case Div(src)               => format("idiv", src)
        case Mod(src)               => format("idiv", src)
        case And(dest, src)         => format("and" , dest, src)
        case Or(dest, src)          => format("or"  , dest, src)

        // data movement
        case CMov(dest, src, cond)  => format(s"cmov${formatCompFlag(cond)}", dest, src)
        case Mov(dest, src)         => format(s"mov${flagSize(dest, src)}" , dest, src)
        case Lea(dest, addr)        => format("lea" , dest, addr)

        // control flow
        case Call(label)            =>
            val labelName = if (isExternalFunction(label.name)) s"${label.name}@plt" else label.name
            format("call", labelName)
        case Jump(label, flag)      => format(s"j${formatJumpFlag(flag)}", label)
        case Ret                    => format("ret\n")

        // comparison operations
        case Cmp(src1, src2)        => format("cmp" , src1, src2)
        case Test(src1, src2)       => format("test", src1, src2)
        case SetComp(dest, flag)    => format(s"set${formatCompFlag(flag)}", dest)
        case JumpComp(label, flag)  => format(s"j${formatCompFlag(flag)}", label)
        case ConvertDoubleToQuad    => format("cdq")

        case _                      => "" // no action required for AT&T headers
    }
}

/** Checks if a function is an external C library function that needs @plt suffix. */
def isExternalFunction(name: String): Boolean = {
    val externalFunctions = Set("exit", "fflush", "free", "malloc", "printf", "puts", "scanf")
    externalFunctions.contains(name)
}

/** Formats an operand according to its type (register, immediate, memory, or label). */
def formatOperand(op: RegImmMemLabel, syntax: SyntaxStyle): String = op match {
    case reg: Register     => formatRegister(reg, syntax)
    case imm: Immediate    => formatImmediate(imm, syntax)
    case mem: MemoryAccess => formatMemAccess(mem, syntax)
    case label: Label      => label.name
    case str: String       => str
}

/** Formats an immediate value. */
def formatImmediate(imm: Immediate, syntax: SyntaxStyle): String = syntax match {
    case SyntaxStyle.Intel => s"${imm.value}"
    case SyntaxStyle.ATT   => s"$$${imm.value}"
}

/** 
  * Formats a memory access expression.
  * Handles different addressing modes including base+offset and base+index*scale.
  */
def formatMemAccess(mem: MemoryAccess, syntax: SyntaxStyle): String = mem match {
    case MemAccess(reg: Register, offset: Int, size) =>
        val regFormat = formatRegister(reg, syntax)
        syntax match {
            case SyntaxStyle.Intel =>
                val operand = if (offset == 0) s"[$regFormat]"
                             else s"[$regFormat ${if offset > 0 then "+" else "-"} ${math.abs(offset)}]"
                s"${sizePtrIntel(size)} ptr $operand"
            case SyntaxStyle.ATT   =>
                if (offset == 0) s"(${regFormat})"
                else s"$offset(${regFormat})"
        }

    // RIP-relative addressing doesn't require size specifier
    case MemAccess(reg @ RIP(_), label: Label, size) =>
        val regFormat = formatRegister(reg, syntax)
        syntax match {
            case SyntaxStyle.Intel => s"[$regFormat + ${label.name}]"
            case SyntaxStyle.ATT   => s"${label.name}(${regFormat})"
        }
    case MemAccess(reg: Register, label: Label, size) =>
        val regFormat = formatRegister(reg, syntax)
        syntax match {
            case SyntaxStyle.Intel => s"${sizePtrIntel(size)} ptr [$regFormat + ${label.name}]"
            case SyntaxStyle.ATT   => s"${label.name}(${regFormat})"
        }
    
    // base + index * scale addressing mode
    case MemRegAccess(base, reg, coeff, size) =>
        val baseFormat = formatRegister(base, syntax)
        val regFormat = formatRegister(reg, syntax)
        syntax match {
            case SyntaxStyle.Intel => s"${sizePtrIntel(size)} ptr [$baseFormat + $regFormat * $coeff]"
            case SyntaxStyle.ATT   => s"(${baseFormat},${regFormat},$coeff)"
        }
}

/** Formats a register name according to its size. */
def formatRegister(reg: Register, syntax: SyntaxStyle): String = reg match {
    // general purpose registers
    case RAX(size) => parameterRegister("a", size, syntax)
    case RBX(size) => parameterRegister("b", size, syntax)
    case RCX(size) => parameterRegister("c", size, syntax)
    case RDX(size) => parameterRegister("d", size, syntax)

    // special purpose registers
    case RDI(size) => specialRegister("di", size, syntax)
    case RSI(size) => specialRegister("si", size, syntax)
    case RBP(size) => specialRegister("bp", size, syntax)
    case RIP(size) => specialRegister("ip", size, syntax)
    case RSP(size) => specialRegister("sp", size, syntax)

    // extended registers (r8-r15)
    case R8 (size) => numberedRegister("8" , size, syntax)
    case R9 (size) => numberedRegister("9" , size, syntax)
    case R10(size) => numberedRegister("10", size, syntax)
    case R11(size) => numberedRegister("11", size, syntax)
    case R12(size) => numberedRegister("12", size, syntax)
    case R13(size) => numberedRegister("13", size, syntax)
    case R14(size) => numberedRegister("14", size, syntax)
    case R15(size) => numberedRegister("15", size, syntax)

    // for debugging
    case TempReg(num, size) => "TEMP_REG"
}
