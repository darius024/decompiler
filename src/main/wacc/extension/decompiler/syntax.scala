package wacc.extension.decompiler

import parsley.generic.*

import wacc.backend.ir.*
import instructions.*
import memory.*
import registers.*

object syntax {
    object MemoryAcc extends ParserBridge3[Register, Option[Int], Option[Register], MemoryAccess] {
        def apply(base: Register, offset: Option[Int], reg: Option[Register]): MemoryAccess = offset match {
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
}
