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
def format(codeGen: CodeGenerator, file: File, syntax: SyntaxStyle): Unit = {
    // create output file with the same name as the input but with .s extension
    val outputPath = os.pwd / s"${file.getName.stripSuffix(".wacc")}.s"
    given outputStream: OutputStream = os.write.outputStream(outputPath)

    try {
        formatHeader(syntax)
        formatBlock(codeGen.data, syntax)
        format(codeGen.ir, syntax)
        formatWidgets(codeGen.dependencies, syntax)
    } finally {
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
        case SyntaxStyle.ATT => format(List(Global("main")), syntax)
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
    syntax match {
        case SyntaxStyle.Intel => formatIntelInstruction(instr)
        case SyntaxStyle.ATT => formatATTInstruction(instr)
    }
}

def formatIntelInstruction(instr: Instruction): String = {
    /** Helper function to format instructions with uniform spacing. */
    def format(opcode: String, operands: RegImmMemLabel*): String = {
        val size = matchSize(operands)
        operands match {
            case Seq(reg1: Register, reg2: Register) if reg1.size.size > reg2.size.size => 
                f"    $opcode%-6s ${List(formatOperand(reg1, reg1.size, SyntaxStyle.Intel), formatOperand(reg2, reg2.size, SyntaxStyle.Intel)).mkString(", ")}"
            case _ =>
                f"    $opcode%-6s ${operands.map(formatOperand(_, size, SyntaxStyle.Intel)).mkString(", ")}"
        }
        // f"    $opcode%-6s ${operands.map(formatOperand(_, size)).mkString(", ")}"
    }

    instr match {
        // assembly directives
        case IntelSyntax            =>  ".intel_syntax noprefix"
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

        case _                      => "" // no action required for AT&T headers
    }
}

def formatATTInstruction(instr: Instruction): String = {
    /** Helper function to format instructions with uniform spacing. */
    def format(opcode: String, operands: RegImmMemLabel*): String = {
        val size = matchSize(operands)
        f"    $opcode%-6s ${operands.map(formatOperand(_, size, SyntaxStyle.ATT)).mkString(", ")}"
    }
    
    instr match {
        // assembly directives
        case SectionRoData          => ".section .rodata"
        case Text                   => ".text"
        case Label(name)            => s"$name:"
        case Global(label)          => s".globl $label"
        case StrLabel(label, _)     => s".L.${label.name}:"
        case DirInt(size)           => format(".int", size.toString)
        case Asciz(name)            => format(".asciz", s"\"${formatString(name)}\"")

        // stack operations
        case Push(reg)              => format("pushq", reg)
        case Pop(reg)               => format("popq" , reg)

        // arithmetic operations
        case Add(dest, src)         => format(s"add${sizePtrATT(dest.size)}", src, dest)
        case Sub(dest, src)         => format(s"sub${sizePtrATT(dest.size)}", src, dest)
        case Mul(dest, src)         => format(s"imul${sizePtrATT(dest.size)}", src, dest)
        case Div(src)               => format("idivq", src)
        case Mod(src)               => format("idivq", src)
        case And(dest, src)         => format(s"and${sizePtrATT(dest.size)}", src, dest)
        case Or(dest, src)          => format(s"or${sizePtrATT(dest.size)}", src, dest)

        // data movement
        case CMov(dest, src, cond)  => format(s"cmov${formatCompFlag(cond)}${sizePtrATT(dest.size)}", src, dest)
        case Mov(dest, src)         => format(s"mov${dest match { case reg: Register => sizePtrATT(reg.size); case _ => "q" }}", src, dest)
        case Lea(dest, addr)        => format(s"lea${sizePtrATT(dest.size)}", addr, dest)

        // control flow
        case Call(label)            => format("call", label)
        case Jump(label, flag)      => format(s"j${formatJumpFlag(flag)}", label)
        case Ret                    => format("ret")

        // comparison operations
        case Cmp(src1, src2)        => format(s"cmp${sizePtrATT(src1.size)}", src2, src1)
        case Test(src1, src2)       => format(s"test${sizePtrATT(src1.size)}", src2, src1)
        case SetComp(dest, flag)    => format(s"set${formatCompFlag(flag)}${sizePtrATT(dest.size)}", dest)
        case JumpComp(label, flag)  => format(s"j${formatCompFlag(flag)}", label)
        case ConvertDoubleToQuad    => format("cltd")
        
        case _                      => "" // no action required for Intel or AT&T headers
    }
}

/** Checks if a function is an external C library function that needs @plt suffix. */
def isExternalFunction(name: String): Boolean = {
    val externalFunctions = Set("exit", "fflush", "free", "malloc", "printf", "puts", "scanf")
    externalFunctions.contains(name)
}

/** Formats an operand according to its type (register, immediate, memory, or label). */
def formatOperand(op: RegImmMemLabel, size: RegSize = RegSize.QUAD_WORD, syntax: SyntaxStyle): String = op match {
    case reg: Register     => formatRegister(reg, reg.size, syntax)
    case imm: Immediate    => formatImmediate(imm, syntax)
    case mem: MemoryAccess => formatMemAccess(mem, size, syntax)
    case label: Label      => label.name
    case str: String       => str
}

/** Formats an immediate value. */
def formatImmediate(imm: Immediate, syntax: SyntaxStyle): String = {
    (imm, syntax) match {
        case (Imm(value), SyntaxStyle.Intel) => s"$value"
        case (Imm(value), SyntaxStyle.ATT) => s"$$$value"
    }
}
/** 
 * Formats a memory access expression.
 * Handles different addressing modes including base+offset and base+index*scale.
 */
def formatMemAccess(mem: MemoryAccess, size: RegSize, syntax: SyntaxStyle): String = {
    syntax match {
        case SyntaxStyle.Intel => formatMemAccessIntel(mem, size)
        case SyntaxStyle.ATT => formatMemAccessATT(mem, size)
    }
}

def formatMemAccessIntel(mem: MemoryAccess, size: RegSize): String = mem match {
    case MemAccess(reg: Register, offset: Int) => 
        val operand = if (offset == 0) s"[${formatRegister(reg, reg.size, SyntaxStyle.Intel)}]" else s"[${formatRegister(reg, reg.size, SyntaxStyle.Intel)} ${if offset > 0 then "+" else ""} $offset]"
        s"${sizePtrIntel(size)} ptr $operand"

    // RIP-relative addressing doesn't require size specifier
    case MemAccess(reg @ RIP(_), offset: Label) =>
        s"[${formatRegister(reg, reg.size, SyntaxStyle.Intel)} + ${offset.name}]"
    case MemAccess(reg: Register, offset: Label) =>
        s"${sizePtrIntel(size)} ptr [${formatRegister(reg, reg.size, SyntaxStyle.Intel)} + ${offset.name}]"
    
    // base + index*scale addressing mode
    case MemRegAccess(base, reg, coeff) =>
        s"${sizePtrIntel(size)} ptr [${formatRegister(base, base.size, SyntaxStyle.Intel)} + ${formatRegister(reg, reg.size, SyntaxStyle.Intel)} * $coeff]"
}

def formatMemAccessATT(mem: MemoryAccess, size: RegSize): String = mem match {
    case MemAccess(reg: Register, offset: Int) => 
        // Remove the % from the register as it's already included in formatRegister for AT&T
        val regStr = formatRegister(reg, reg.size, SyntaxStyle.ATT)
        if (offset == 0) s"(${regStr})"
        else s"$offset(${regStr})"
    
    // RIP-relative addressing
    case MemAccess(reg @ RIP(_), offset: Label) =>
        val regStr = formatRegister(reg, reg.size, SyntaxStyle.ATT)
        s"${offset.name}(${regStr})"
    case MemAccess(reg: Register, offset: Label) =>
        val regStr = formatRegister(reg, reg.size, SyntaxStyle.ATT)
        s"${offset.name}(${regStr})"
    
    // base + index*scale addressing mode
    case MemRegAccess(base, reg, coeff) =>
        val baseStr = formatRegister(base, base.size, SyntaxStyle.ATT)
        val regStr = formatRegister(reg, reg.size, SyntaxStyle.ATT)
        s"(${baseStr},${regStr},$coeff)"
}

/** Formats a register name according to its size. */
def formatRegister(reg: Register, size: RegSize, syntax: SyntaxStyle): String = reg match {
    // general purpose registers
    case RAX(_) => parameterRegister("a", size, syntax)
    case RBX(_) => parameterRegister("b", size, syntax)
    case RCX(_) => parameterRegister("c", size, syntax)
    case RDX(_) => parameterRegister("d", size, syntax)

    // special purpose registers
    case RDI(_) => specialRegister("di", size, syntax)
    case RSI(_) => specialRegister("si", size, syntax)
    case RBP(_) => specialRegister("bp", size, syntax)
    case RIP(_) => specialRegister("ip", size, syntax)
    case RSP(_) => specialRegister("sp", size, syntax)

    // extended registers (r8-r15)
    case R8 (_) => numberedRegister("8" , size, syntax)
    case R9 (_) => numberedRegister("9" , size, syntax)
    case R10(_) => numberedRegister("10", size, syntax)
    case R11(_) => numberedRegister("11", size, syntax)
    case R12(_) => numberedRegister("12", size, syntax)
    case R13(_) => numberedRegister("13", size, syntax)
    case R14(_) => numberedRegister("14", size, syntax)
    case R15(_) => numberedRegister("15", size, syntax)

    // TODO: temporary register (to be removed in future)
    case TempReg(num, size) => "TEMP_REG"
}
