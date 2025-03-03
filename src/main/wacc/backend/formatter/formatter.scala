package wacc.backend.formatter

import java.io.{File, OutputStream}
import os.*

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

/**
 * Formats the assembly instructions produced by the code generator.
 * 
 * This module handles the conversion from IR instructions to actual assembly text,
 * writing the output to a .s file with the same name as the input WACC file.
 */
def format(codeGen: CodeGenerator, file: File): Unit = {
    // create output file with the same name as the input but with .s extension
    val outputPath = os.pwd / s"${file.getName.stripSuffix(".wacc")}.s"
    given outputStream: OutputStream = os.write.outputStream(outputPath)

    try {
        formatHeader
        formatBlock(codeGen.data)
        format(codeGen.ir)
        formatWidgets(codeGen.dependencies)
    } finally {
        outputStream.close()
    }
}

/** Formats a block of instructions by writing each one on a new line. */
def format(instructions: List[Instruction])
          (using outputStream: OutputStream): Unit = instructions.foreach { instr =>
    // write directly to the output stream to save memory
    // (avoids accumulating the entire text in memory)
    outputStream.write(formatInstruction(instr).getBytes)
    outputStream.write("\n".getBytes)
}

/** Writes the assembly header with Intel syntax and global entry point. */
def formatHeader(using outputStream: OutputStream): Unit = {
    format(List(IntelSyntax, Global("main")))
}

/** 
 * Formats the data segment containing string literals.
 * Creates the .rodata section and adds all string constants.
 */
def formatBlock(directives: Set[StrLabel])
               (using outputStream: OutputStream): Unit = {
    // mark the beginning of the data segment
    format(List(SectionRoData))
    if (!directives.isEmpty) {
        directives.map(formatDirective)
    }
    // mark the beginning of the code segment
    format(List(Text))
}

/** Formats a string directive with its size and value. */
def formatDirective(strLabel: StrLabel)
                   (using outputStream: OutputStream): Unit = {
    val StrLabel(label, name) = strLabel

    format(List(DirInt(name.length), label, Asciz(name)))
}

/** 
 * Formats runtime support functions (widgets).
 * Each widget has its own data and code segments.
 */
def formatWidgets(widgets: Set[Widget])
                 (using outputStream: OutputStream): Unit = widgets.foreach { widget =>
    formatBlock(widget.directives)
    format(List(widget.label))
    format(widget.instructions)
}

/** Transforms IR instructions into their corresponding assembly text. */
def formatInstruction(instr: Instruction): String = {
    /** Helper function to format instructions with uniform spacing. */
    def format(opcode: String, operands: RegImmMemLabel*): String = {
        val size = matchSize(operands)
        operands match {
            case Seq(reg1: Register, reg2: Register) if reg1.size.size > reg2.size.size => 
                f"    $opcode%-6s ${List(formatOperand(reg1), formatOperand(reg2)).mkString(", ")}"
            case _ =>
                f"    $opcode%-6s ${operands.map(formatOperand(_, size)).mkString(", ")}"
        }
        // f"    $opcode%-6s ${operands.map(formatOperand(_, size)).mkString(", ")}"
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
    }
}

/** Checks if a function is an external C library function that needs @plt suffix. */
def isExternalFunction(name: String): Boolean = {
    val externalFunctions = Set("exit", "fflush", "free", "malloc", "printf", "puts", "scanf")
    externalFunctions.contains(name)
}

/** Formats an operand according to its type (register, immediate, memory, or label). */
def formatOperand(op: RegImmMemLabel, size: RegSize = RegSize.QUAD_WORD): String = op match {
    case reg: Register     => formatRegister(reg, reg.size)
    case imm: Immediate    => formatImmediate(imm)
    case mem: MemoryAccess => formatMemAccess(mem, size)
    case label: Label      => label.name
    case str: String       => str
}

/** Formats an immediate value. */
def formatImmediate(imm: Immediate): String = imm match {
    case Imm(value) => s"$value"
}

/** 
 * Formats a memory access expression.
 * Handles different addressing modes including base+offset and base+index*scale.
 */
def formatMemAccess(mem: MemoryAccess, size: RegSize): String = mem match {
    case MemAccess(reg: Register, offset: Int) => 
        val operand = if (offset == 0) s"[${formatRegister(reg, reg.size)}]" else s"[${formatRegister(reg, reg.size)} ${if offset > 0 then "+" else ""} $offset]"
        s"${sizePtr(size)} ptr $operand"
    
    // RIP-relative addressing doesn't require size specifier
    case MemAccess(reg @ RIP(_), offset: Label) =>
        s"[${formatRegister(reg, reg.size)} + ${offset.name}]"
    case MemAccess(reg: Register, offset: Label) =>
        s"${sizePtr(size)} ptr [${formatRegister(reg, reg.size)} + ${offset.name}]"
    
    // base + index*scale addressing mode
    case MemRegAccess(base, reg, coeff) =>
        s"${sizePtr(size)} ptr [${formatRegister(base, base.size)} + ${formatRegister(reg, reg.size)} * $coeff]"
}

/** Formats a register name according to its size. */
def formatRegister(reg: Register, size: RegSize): String = reg match {
    // general purpose registers
    case RAX(_) => parameterRegister("a", size)
    case RBX(_) => parameterRegister("b", size)
    case RCX(_) => parameterRegister("c", size)
    case RDX(_) => parameterRegister("d", size)

    // special purpose registers
    case RDI(_) => specialRegister("di", size)
    case RSI(_) => specialRegister("si", size)
    case RBP(_) => specialRegister("bp", size)
    case RIP(_) => specialRegister("ip", size)
    case RSP(_) => specialRegister("sp", size)

    // extended registers (r8-r15)
    case R8 (_) => numberedRegister("8" , size)
    case R9 (_) => numberedRegister("9" , size)
    case R10(_) => numberedRegister("10", size)
    case R11(_) => numberedRegister("11", size)
    case R12(_) => numberedRegister("12", size)
    case R13(_) => numberedRegister("13", size)
    case R14(_) => numberedRegister("14", size)
    case R15(_) => numberedRegister("15", size)

    // TODO: temporary register (to be removed in future)
    case TempReg(num, size) => "TEMP_REG"
}
