package wacc.extension.decompiler

import parsley.generic.*

import wacc.backend.ir.*
import flags.*
import instructions.*
import memory.*
import registers.*

object syntax {
    object MemoryAcc extends ParserBridge3[Register, Option[Int | Label], Option[Register], MemoryAccess] {
        def apply(base: Register, offset: Option[Int | Label], reg: Option[Register]): MemoryAccess = offset match {
            case Some(offset) => reg match {
                case Some(reg) => MemRegAccess(base, reg, offset)
                case None      => MemAccess(base, offset)
            }
            case None         => MemAccess(base, memoryOffsets.NO_OFFSET)
        }
    }

    object StrDirective extends ParserBridge3[Int, Label, String, StrLabel] {
        def apply(size: Int, label: Label, name: String): StrLabel =
            StrLabel(label, name)
    }

    object CMovInstr extends ParserBridge3[CompFlag, Register, Register, CMov] {
        def apply(compFlag: CompFlag, dest: Register, src: Register): CMov = {
            CMov(dest, src, compFlag)
        }
    }

    object JumpInstr extends ParserBridge2[JumpFlag, Label, Jump] {
        def apply(jumpFlag: JumpFlag, label: Label): Jump = {
            Jump(label, jumpFlag)
        }
    }

    object JumpCompInstr extends ParserBridge2[CompFlag, Label, JumpComp] {
        def apply(compFlag: CompFlag, label: Label): JumpComp = {
            JumpComp(label, compFlag)
        }
    }

    object SetCompInstr extends ParserBridge2[CompFlag, Register, SetComp] {
        def apply(compFlag: CompFlag, dest: Register): SetComp = {
            SetComp(dest, compFlag)
        }
    }
}
