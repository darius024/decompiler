package wacc.backend.generator

import wacc.backend.ir.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*

import errors.*
import widgets.*

object widgets {

    val widgetSet: Set[Widget] = Set(
        ReadInt,
        ReadChar,
        PrintInt,
        PrintChar,
        PrintString,
        PrintBool,
        PrintLn,
        Malloc,
        FreePair,
        ArrayStore1,
        ArrayStore2,
        ArrayStore4,
        ArrayStore8,
        ArrayLoad1,
        ArrayLoad2,
        ArrayLoad4,
        ArrayLoad8,
        ExitProg,

        ErrNull,
        ErrOverflow,
        ErrDivZero,
        ErrOutOfBounds,
        ErrOutOfMemory,
    )

    sealed trait Widget {
        val label: Label
        val directives: List[StrLabel] = List.empty
        def instructions: List[Instruction]
        def dependencies: Set[Widget] = Set.empty
    }

    case object ReadInt extends Widget {
        val label = Label("_readi")
        override val directives = List(StrLabel(Label(".L._readi_str0"), asciz.integer))
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Sub(RSP(), Imm(RegSize.WORD.size)),
            Mov(MemAccess(RSP(), memoryOffsets.NO_OFFSET), RDI(RegSize.DOUBLE_WORD)),
            Lea(RSI(), MemAccess(RSP(), memoryOffsets.NO_OFFSET)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._readi_str0"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.scanf),
            Mov(RAX(RegSize.DOUBLE_WORD), MemAccess(RSP(), memoryOffsets.NO_OFFSET)),
            Add(RSP(), Imm(RegSize.WORD.size)),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    case object ReadChar extends Widget {
        val label = Label("_readc")
        override val directives = List(StrLabel(Label(".L._readc_str0"), asciz.character))
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Sub(RSP(), Imm(RegSize.WORD.size)),
            Mov(MemAccess(RSP(), memoryOffsets.NO_OFFSET), RDI(RegSize.BYTE)),
            Lea(RSI(), MemAccess(RSP(), memoryOffsets.NO_OFFSET)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._readc_str0"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.scanf),
            Mov(RAX(RegSize.BYTE), MemAccess(RSP(), memoryOffsets.NO_OFFSET)),
            Add(RSP(), Imm(RegSize.WORD.size)),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    case object PrintInt extends Widget {
        val label = Label("_printi")
        override val directives = List(StrLabel(Label(".L._printi_str0"), asciz.integer))
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Mov(RSI(RegSize.DOUBLE_WORD), RDI(RegSize.DOUBLE_WORD)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._printi_str0"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(RDI(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    case object PrintChar extends Widget {
        val label = Label("_printc")
        override val directives = List(StrLabel(Label(".L._printc_str0"), asciz.character))
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Mov(RSI(RegSize.BYTE), RDI(RegSize.BYTE)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._printc_str0"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(RDI(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    case object PrintString extends Widget {
        val label = Label("_prints")
        override val directives = List(StrLabel(Label(".L._prints_str0"), asciz.string))
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Mov(RDX(), RDI()),
            Mov(RSI(RegSize.DOUBLE_WORD), MemAccess(RDI(), memoryOffsets.ARRAY_LENGTH_OFFSET)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._prints_str0"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(RDI(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    case object PrintBool extends Widget {
        val label = Label("_printb")
        override val directives = List(
            StrLabel(Label(".L._printb_str0"), "false"),
            StrLabel(Label(".L._printb_str1"), "true"),
            StrLabel(Label(".L._printb_str2"), asciz.string)
        )
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Cmp(RDI(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            JumpComp(Label(".L_printb0"), CompFlag.NE),
            Lea(RDX(), MemAccess(RIP(), Label(".L._printb_str0"))),
            Jump(Label(".L_printb1"), JumpFlag.Unconditional),
            Label(".L_printb0"),
            Lea(RDX(), MemAccess(RIP(), Label(".L._printb_str1"))),
            Label(".L_printb1"),
            Mov(RSI(RegSize.DOUBLE_WORD), MemAccess(RDX(), memoryOffsets.ARRAY_LENGTH_OFFSET)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._printb_str2"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(RDI(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    case object PrintLn extends Widget {
        val label = Label("_println")
        override val directives = List(StrLabel(Label(".L._println_str0"), asciz.endl))
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._println_str0"))),
            Call(library.puts),
            Mov(RDI(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    case object Malloc extends Widget {
        val label = Label("_malloc")
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Call(library.malloc),
            Cmp(RAX(), Imm(memoryOffsets.NO_OFFSET)),
            JumpComp(ErrOutOfMemory.label, CompFlag.E),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    case object FreeProg extends Widget {
        val label = Label("_free")
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Call(library.free),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    case object FreePair extends Widget {
        val label = Label("_freepair")
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Cmp(RDI(), Imm(memoryOffsets.NO_OFFSET)),
            JumpComp(ErrNull.label, CompFlag.E),
            Call(library.free),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    case object ArrayStore1 extends Widget {
        val label = Label("_arrStore1")
        def instructions: List[Instruction] = arrStore.instructions(memoryOffsets.ARR_STORE1)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    case object ArrayStore2 extends Widget {
        val label = Label("_arrStore2")
        def instructions: List[Instruction] = arrStore.instructions(memoryOffsets.ARR_STORE2)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    case object ArrayStore4 extends Widget {
        val label = Label("_arrStore4")
        def instructions: List[Instruction] = arrStore.instructions(memoryOffsets.ARR_STORE4)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    case object ArrayStore8 extends Widget {
        val label = Label("_arrStore8")
        def instructions: List[Instruction] = arrStore.instructions(memoryOffsets.ARR_STORE8)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    case object ArrayLoad1 extends Widget {
        val label = Label("_arrLoad1")
        def instructions: List[Instruction] = arrLoad.instructions(memoryOffsets.ARR_LOAD1)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    case object ArrayLoad2 extends Widget {
        val label = Label("_arrLoad2")
        def instructions: List[Instruction] = arrLoad.instructions(memoryOffsets.ARR_LOAD2)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }
    
    case object ArrayLoad4 extends Widget {
        val label = Label("_arrLoad4")
        def instructions: List[Instruction] = arrLoad.instructions(memoryOffsets.ARR_LOAD4)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    case object ArrayLoad8 extends Widget {
        val label = Label("_arrLoad8")
        def instructions: List[Instruction] = arrLoad.instructions(memoryOffsets.ARR_LOAD8)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    case object ExitProg extends Widget {
        val label = Label("_exit")
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Call(library.exit),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }
}


object errors {
    sealed trait ErrorWidget extends Widget {
        def message: String
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    case object ErrNull extends ErrorWidget {
        val label = Label("_errNull")
        override val directives = List(StrLabel(Label(".L._errNull_str0"), message))
        def message: String = errorMessages.generateError("null pointer")
        def instructions: List[Instruction] = List(
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._errNull_str0"))),
            Call(widgets.PrintString.label),
            Mov(RDI(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
    }

    case object ErrOverflow extends ErrorWidget {
        val label = Label("_errOverflow")
        override val directives = List(StrLabel(Label(".L._errOverflow_str0"), message))
        def message: String = errorMessages.generateError("integer overflow or underflow occurred")
        def instructions: List[Instruction] = List(
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._errOverflow_str0"))),
            Call(widgets.PrintString.label),
            Mov(RDI(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
    }

    case object ErrDivZero extends ErrorWidget {
        val label = Label("_errDivZero")
        override val directives = List(StrLabel(Label(".L._errDivZero_str0"), message))
        def message: String = errorMessages.generateError("division or modulo by zero")
        def instructions: List[Instruction] = List(
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._errDivZero_str0"))),
            Call(widgets.PrintString.label),
            Mov(RDI(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
    }

    case object ErrOutOfBounds extends ErrorWidget {
        val label = Label("_errOutOfBounds")
        override val directives = List(StrLabel(Label(".L._errOutOfBounds_str0"), message))
        def message: String = errorMessages.generateError("array index %d out of bounds")
        def instructions: List[Instruction] = List(
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._errOutOfBounds_str0"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(RDI(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
            Mov(RDI(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
    }

    case object ErrOutOfMemory extends ErrorWidget {
        val label = Label("_errOutOfMemory")
        override val directives = List(StrLabel(Label(".L._errOutOfMemory_str0"), message))
        def message: String = errorMessages.generateError("out of memory")
        def instructions: List[Instruction] = List(
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._errOutOfMemory_str0"))),
            Call(widgets.PrintString.label),
            Mov(RDI(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
    }

    case object ErrBadChar extends ErrorWidget {
        val label = Label("_errBadChar")
        override val directives = List(StrLabel(Label(".L._errBadChar_str0"), message))
        def message: String = errorMessages.generateError("invalid character")
        def instructions: List[Instruction] = List(
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._errBadChar_str0"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(RDI(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
            Mov(RDI(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.exit)
        )
    }
}

object errorMessages {
    def generateError(message: String): String = s"fatal error: $message\n"
}

object library {
    val exit   = Label("exit")
    val fflush = Label("fflush")
    val free   = Label("free")
    val malloc = Label("malloc")
    val printf = Label("printf")
    val puts   = Label("puts")
    val scanf  = Label("scanf")
}

object arrStore {
    def instructions(size: Int): List[Instruction] = List(
        Push(RBX()),
        Test(R10(RegSize.DOUBLE_WORD), R10(RegSize.DOUBLE_WORD)),
        CMov(RSI(), R10(), CompFlag.L),
        JumpComp(ErrOutOfBounds.label, CompFlag.L),
        Mov(RBX(RegSize.DOUBLE_WORD), MemAccess(R9(), memoryOffsets.ARRAY_LENGTH_OFFSET)),
        Cmp(R10(RegSize.DOUBLE_WORD), RBX(RegSize.DOUBLE_WORD)),
        CMov(RSI(), R10(), CompFlag.GE),
        JumpComp(ErrOutOfBounds.label, CompFlag.GE),
        Mov(R9(RegSize.DOUBLE_WORD), MemRegAccess(R9(), R10(), size)),
        Pop(RBX()),
        Ret
    )
}

object arrLoad {
    def instructions(size: Int): List[Instruction] = List(
        Push(RBX()),
        Test(R10(RegSize.DOUBLE_WORD), R10(RegSize.DOUBLE_WORD)),
        CMov(RSI(), R10(), CompFlag.L),
        JumpComp(ErrOutOfBounds.label, CompFlag.L),
        Mov(RBX(RegSize.DOUBLE_WORD), MemAccess(R9(), memoryOffsets.ARRAY_LENGTH_OFFSET)),
        Cmp(R10(RegSize.DOUBLE_WORD), RBX(RegSize.DOUBLE_WORD)),
        CMov(RSI(), R10(), CompFlag.GE),
        JumpComp(ErrOutOfBounds.label, CompFlag.GE),
        Mov(RAX(), MemRegAccess(R9(), R10(), size)),
        Pop(RBX()),
        Ret
    )
}
