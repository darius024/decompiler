package wacc.extension.decompiler

import parsley.Parsley
import parsley.errors.combinator.*
import parsley.generic.*

import wacc.backend.ir.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*

/** Defines parser-bridge objects to enable transformations of rules to IR nodes. */
object syntax {
    /** Alias for list of statements. */
    type IRProgram = List[Instruction]

    /** Creates a memory access operand from the rules. */
    object MemoryAcc extends ParserBridge3[Register, Option[(Boolean, Int | Label)], Option[Register], MemoryAccess] {
        def apply(base: Register, offset: Option[(Boolean, Int | Label)], reg: Option[Register]): MemoryAccess = offset match {
            case Some(offset) =>
                val off = if (offset._1 ) offset._2 else offset._2 match {case i: Int => -i; case label => label}
                reg match {
                    case Some(reg) => MemRegAccess(base, reg, off)
                    case None      => MemAccess(base, off)
                }
            case None         => MemAccess(base, memoryOffsets.NO_OFFSET)
        }
    }

    /** Creates a string directive from the three corresponding lines of assembly code. */
    object StrDirective extends ParserBridge3[Int, Label, String, StrLabel] {
        def apply(size: Int, label: Label, name: String): StrLabel =
            StrLabel(label, name)
    }

    /** Create a `cmov` instruction after parsing the flag. */
    object CMovInstr extends ParserBridge3[CompFlag, Register, Register, CMov] {
        def apply(compFlag: CompFlag, dest: Register, src: Register): CMov = {
            CMov(dest, src, compFlag)
        }
    }

    /** Create a jump instruction after parsing the flag. */
    object JumpInstr extends ParserBridge2[JumpFlag, Label, Jump] {
        def apply(jumpFlag: JumpFlag, label: Label): Jump = {
            Jump(label, jumpFlag)
        }
    }

    /** Create a jump-on-comparison instruction after parsing the flag. */
    object JumpCompInstr extends ParserBridge2[CompFlag, Label, JumpComp] {
        def apply(compFlag: CompFlag, label: Label): JumpComp = {
            JumpComp(label, compFlag)
        }
    }

    /** Create a `set` instruction after parsing the flag. */
    object SetCompInstr extends ParserBridge2[CompFlag, Register, SetComp] {
        def apply(compFlag: CompFlag, dest: Register): SetComp = {
            SetComp(dest, compFlag)
        }
    }

    /** Assigns the correct size to a memory access. */
    object MemoryPointer extends ParserBridge2[Option[RegSize], MemoryAccess, MemoryAccess] {
        def apply(size: Option[RegSize], memAccess: MemoryAccess): MemoryAccess = memAccess match {
            case MemAccess(base, offset, _)         => MemAccess(base, offset, size.getOrElse(RegSize.QUAD_WORD))
            case MemRegAccess(base, reg, offset, _) => MemRegAccess(base, reg, offset, size.getOrElse(RegSize.QUAD_WORD))
        }
    }

    object BinaryInstruction extends ParserBridge1[BinaryInstr, BinaryInstr] {
        override def apply(instr: Parsley[BinaryInstr]): Parsley[BinaryInstr] = super.apply(instr).guardAgainst {
            case binInstr if (binInstr.dest.size != binInstr.src.size && !binInstr.src.isInstanceOf[Immediate]) =>
                    Seq("operands do not match")
        }

        override def apply(instr: BinaryInstr): BinaryInstr = instr
    }

    object MovInstr extends ParserBridge1[Mov, Mov] {
        override def apply(mov: Parsley[Mov]): Parsley[Mov] = super.apply(mov).guardAgainst {
            case mov if (mov.dest.size.size < mov.src.size.size && !mov.src.isInstanceOf[Immediate]) =>
                    Seq("destination operand is smaller than source operand in move")
        }

        override def apply(mov: Mov): Mov = mov
    }
}
