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
        f"    $opcode%-6s ${operands.map(formatOperand(_, size)).mkString(", ")}"
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
        case Mul(dest, src1, src2)  => format("imul", dest, src1, src2)
        case Div(src)               => format("idiv", src)
        case Mod(src)               => format("idiv", src)
        case And(dest, src)         => format("and" , dest, src)
        case Or(dest, src)          => format("or"  , dest, src)

        // data movement
        case CMov(dest, src, cond)  => format(s"cmov${formatCompFlag(cond)}", dest, src)
        case Mov(dest, src)         => format("mov" , dest, src)
        case Lea(dest, addr)        => format("lea" , dest, addr)

        // control flow
        case Call(label)            => format("call" , label)
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

/** Formats an operand according to its type (register, immediate, memory, or label). */
def formatOperand(op: RegImmMemLabel, size: RegSize): String = op match {
    case reg: Register     => formatRegister(reg)
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
        val operand = if (offset == 0) s"[${formatRegister(reg)}]" else s"[${formatRegister(reg)} ${if offset > 0 then "+" else ""} $offset]"
        s"${sizePtr(size)} ptr $operand"
    
    // RIP-relative addressing doesn't require size specifier
    case MemAccess(reg @ RIP(_), offset: Label) =>
        s"[${formatRegister(reg)} + ${offset.name}]"
    case MemAccess(reg: Register, offset: Label) =>
        s"${sizePtr(size)} ptr [${formatRegister(reg)} + ${offset.name}]"
    
    // base + index*scale addressing mode
    case MemRegAccess(base, reg, coeff) =>
        s"${sizePtr(size)} ptr [${formatRegister(base)} + ${formatRegister(reg)} * $coeff]"
}

/** Formats a register name according to its size. */
def formatRegister(reg: Register): String = reg match {
    // general purpose registers
    case RAX(size) => parameterRegister("a", size)
    case RBX(size) => parameterRegister("b", size)
    case RCX(size) => parameterRegister("c", size)
    case RDX(size) => parameterRegister("d", size)

    // special purpose registers
    case RDI(size) => specialRegister("di", size)
    case RSI(size) => specialRegister("si", size)
    case RBP(size) => specialRegister("bp", size)
    case RIP(size) => specialRegister("ip", size)
    case RSP(size) => specialRegister("sp", size)

    // extended registers (r8-r15)
    case R8 (size) => numberedRegister("8" , size)
    case R9 (size) => numberedRegister("9" , size)
    case R10(size) => numberedRegister("10", size)
    case R11(size) => numberedRegister("11", size)
    case R12(size) => numberedRegister("12", size)
    case R13(size) => numberedRegister("13", size)
    case R14(size) => numberedRegister("14", size)
    case R15(size) => numberedRegister("15", size)

    // TODO: temporary register (to be removed in future)
    case TempReg(num, size) => "TEMP_REG"
}
