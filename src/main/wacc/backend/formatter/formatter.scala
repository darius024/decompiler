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
    case RAX(size) => if (size == BYTE) "al" else if (size == DOUBLE_WORD) "eax" else "rax"
    case RBX(size) => if (size == BYTE) "bl" else if (size == DOUBLE_WORD) "ebx" else "rbx"
    case RCX(size) => if (size == BYTE) "cl" else if (size == DOUBLE_WORD) "ecx" else "rcx"
    case RDX(size) => if (size == BYTE) "dl" else if (size == DOUBLE_WORD) "edx" else "rdx"
    case RSI(size) => if (size == BYTE) "sil" else if (size == DOUBLE_WORD) "esi" else "rsi"
    case RDI(size) => if (size == BYTE) "dil" else if (size == DOUBLE_WORD) "edi" else "rdi"
    case RSP(size) => if (size == DOUBLE_WORD) "esp" else "rsp"
    case RBP(size) => if (size == DOUBLE_WORD) "ebp" else "rbp"
    case R8 (size) => if (size == BYTE) "r8b" else if (size == DOUBLE_WORD) "r8d" else "r8"
    case R9 (size) => if (size == BYTE) "r9b" else if (size == DOUBLE_WORD) "r9d" else "r9"
    case R10(size) => if (size == BYTE) "r10b" else if (size == DOUBLE_WORD) "r10d" else "r10"
    case R11(size) => if (size == BYTE) "r11b" else if (size == DOUBLE_WORD) "r11d" else "r11"
    case R12(size) => if (size == BYTE) "r12b" else if (size == DOUBLE_WORD) "r12d" else "r12"
    case R13(size) => if (size == BYTE) "r13b" else if (size == DOUBLE_WORD) "r13d" else "r13"
    case R14(size) => if (size == BYTE) "r14b" else if (size == DOUBLE_WORD) "r14d" else "r14"
    case R15(size) => if (size == BYTE) "r15b" else if (size == DOUBLE_WORD) "r15d" else "r15"
    case RIP(size) => if (size == DOUBLE_WORD) "eip" else "rip"
    // TODO: remove this case
    case TempReg(num, size) => "TEMP_REG"
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

def formatOperand(op: RegImmMem, size: RegSize = QUAD_WORD): String = op match {
    case reg: Register     => formatRegister(reg)
    case imm: Immediate    => formatImmediate(imm)
    case mem: MemoryAccess => formatMemAccess(mem, size)
}

def formatDestOperand(op: RegMem): String = op match {
    case reg: Register     => formatRegister(reg)
    case mem: MemoryAccess => formatMemAccess(mem)
}

def formatInstruction(instr: Instruction): String = instr match {
    case IntelSyntax                         => ".intel_syntax noprefix"
    case SectionRoData                       => ".section .rodata"
    case Text                                => ".text"
    case Label(name)                         => s"\n$name:"
    case Global(label)                       => s".globl $label"
    // TODO: correct label name by adding "db"
    case StrLabel(label: Label, _)           => s".L.${label.name}:"
    case DirInt(size)                        => s"    .int $size"
    case Asciz(name)                         => s"    .asciz \"${formatString(name)}\""
    
    case Push(reg)                           => s"    push ${formatRegister(reg)}"
    case Pop(reg)                            => s"    pop ${formatRegister(reg)}"
    
    case Add(dest, src)                      => s"    add ${formatRegister(dest)}, ${formatOperand(src)}"
    case Sub(dest, src)                      => s"    sub ${formatRegister(dest)}, ${formatOperand(src)}"
    case Mul(dest, src1, src2)               => s"    imul ${formatRegister(dest)}, ${formatOperand(src1)}, ${formatOperand(src2)}"
    case Div(src)                            => s"    idiv ${formatOperand(src)}"
    case Mod(src)                            => s"    idiv ${formatOperand(src)}"
    case And(dest, src)                      => s"    and ${formatRegister(dest)}, ${formatOperand(src)}"
    case Or(dest, src)                       => s"    or ${formatRegister(dest)}, ${formatOperand(src)}"
    
    case Neg(dest, src)                      => s"    neg ${formatRegister(dest)}"
    case Not(dest, src)                      => s"    not ${formatRegister(dest)}"
    
    case CMov(dest, src, cond)               => s"    cmov${formatCompFlag(cond)} ${formatRegister(dest)} ${formatRegister(src)}"
    case Mov(dest, src)                      => s"    mov ${formatDestOperand(dest)}, ${formatOperand(src)}"
    case Lea(dest, addr)                     => s"    lea ${formatRegister(dest)}, ${formatMemAccess(addr)}"
    
    case Call(label)                         => s"    call ${label.name}"
    case Jump(label, flag)                   => s"    j${formatJumpFlag(flag)} ${label.name}"
    case Ret                                 => s"    ret"
    
    case Cmp(op1, op2)                       => s"    cmp ${formatOperand(op1)}, ${formatOperand(op2)}"
    case Test(op1, op2)                      => s"    test ${formatOperand(op1)}, ${formatOperand(op2)}"
    case SetComp(dest, flag)                 => s"    set${formatCompFlag(flag)} ${formatRegister(dest)}"
    case JumpComp(label, flag)               => s"    j${formatCompFlag(flag)} ${label.name}"
    case ConvertDoubleToQuad                 => s"    cdq"
}

def formatImmediate(imm: Immediate): String = imm match {
    case Imm(value)                          => s"$value"
}

// TODO: pass the size of the other register
// size check should be performed in terms of the other
def formatMemAccess(mem: MemoryAccess, size: RegSize = QUAD_WORD): String = {
    def sizeCheck(dim: RegSize) = dim match {
        case BYTE        => "byte"
        case WORD        => "word"
        case DOUBLE_WORD => "dword"
        case QUAD_WORD   => "qword"
    }
    
    mem match {
        case MemAccess(reg: Register, offset: Int) => 
            val operand = if (offset == 0) s"[${formatRegister(reg)}]" else s"[${formatRegister(reg)} ${if offset > 0 then "+" else ""} $offset]"
            s"${sizeCheck(size)} ptr $operand"
        case MemAccess(reg @ RIP(_), offset: Label) =>
            s"[${formatRegister(reg)} + ${offset.name}]"
        case MemAccess(reg: Register, offset: Label) =>
            s"${sizeCheck(size)} ptr [${formatRegister(reg)} + ${offset.name}]"
        case MemRegAccess(base, reg, coeff) =>
            s"${sizeCheck(size)} ptr [${formatRegister(base)} + ${formatRegister(reg)} * $coeff]"
    }
}

def formatString(name: String): String = name
    .replace("\u0000", "\\0")   // null character
    .replace("\b", "\\b")       // backspace
    .replace("\t", "\\t")       // tab
    .replace("\n", "\\n")       // newline
    .replace("\f", "\\f")       // form feed
    .replace("\r", "\\r")       // carriage return
    .replace("\\", "\\\\")      // backslash
