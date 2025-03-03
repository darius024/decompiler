package wacc.backend.generator

import wacc.backend.ir.*
import flags.*
import immediate.*
import instructions.*
import memory.*
import registers.*

import errors.*
import widgets.*

/**
 * Utility for generating error messages with a consistent format.
 */
object errorMessages {
    def generateError(message: String): String = s"fatal error: $message\n"
}

/**
 * External library functions used by the runtime support code.
 */
object library {
    val exit   = Label("exit")    // process termination
    val fflush = Label("fflush")  // flush output buffers
    val free   = Label("free")    // free heap memory
    val malloc = Label("malloc")  // allocate heap memory
    val printf = Label("printf")  // formatted output
    val puts   = Label("puts")    // string output with newline
    val scanf  = Label("scanf")   // formatted input
}

/**
 * Shared implementation for storing elements into arrays.
 */
object arrStore {
    def instructions(size: Int): List[Instruction] = List(
        Push(RBX()),
        // check for negative index
        Test(R10(RegSize.DOUBLE_WORD), R10(RegSize.DOUBLE_WORD)),
        CMov(RSI(), R10(), CompFlag.L),
        JumpComp(ErrOutOfBounds.label, CompFlag.L),
        // check for index >= array length
        Mov(RBX(RegSize.DOUBLE_WORD), MemAccess(R9(), memoryOffsets.ARRAY_LENGTH_OFFSET)),
        Cmp(R10(RegSize.DOUBLE_WORD), RBX(RegSize.DOUBLE_WORD)),
        CMov(RSI(), R10(), CompFlag.GE),
        JumpComp(ErrOutOfBounds.label, CompFlag.GE),
        // store the element
        Mov(R9(RegSize.DOUBLE_WORD), MemRegAccess(R9(), R10(), size)),
        Pop(RBX()),
        Ret
    )
}

/**
 * Shared implementation for loading elements from arrays.
 */
object arrLoad {
    def instructions(size: Int): List[Instruction] = List(
        Push(RBX()),
        // check for negative index
        Test(R10(RegSize.DOUBLE_WORD), R10(RegSize.DOUBLE_WORD)),
        CMov(RSI(), R10(), CompFlag.L),
        JumpComp(ErrOutOfBounds.label, CompFlag.L),
        // check for index >= array length
        Mov(RBX(RegSize.DOUBLE_WORD), MemAccess(R9(), memoryOffsets.ARRAY_LENGTH_OFFSET)),
        Cmp(R10(RegSize.DOUBLE_WORD), RBX(RegSize.DOUBLE_WORD)),
        CMov(RSI(), R10(), CompFlag.GE),
        JumpComp(ErrOutOfBounds.label, CompFlag.GE),
        // load the element
        Mov(R9(RegSize.DOUBLE_WORD), MemRegAccess(R9(), R10(), size)),
        Pop(RBX()),
        Ret
    )
}

/**
 * Runtime support functions (widgets) used by the generated code.
 */
object widgets {
    // complete set of all available widgets
    val widgetSet: Set[Widget] = Set(
        ReadInt,
        ReadChar,
        PrintInt,
        PrintChar,
        PrintString,
        PrintPointer,
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

    /**
     * Base trait for all runtime support functions.
     */
    sealed trait Widget {
        /** The label used to call this widget */
        val label: Label
        /** String literals used by this widget */
        val directives: Set[StrLabel] = Set.empty
        /** The assembly instructions implementing this widget */
        def instructions: List[Instruction]
        /** Other widgets this widget depends on */
        def dependencies: Set[Widget] = Set.empty
    }

    /**
     * Reads an integer from standard input.
     */
    case object ReadInt extends Widget {
        val label = Label("_readi")
        override val directives = Set(StrLabel(Label(".L._readi_str0"), asciz.integer))
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Sub(RSP(), Imm(memoryOffsets.STACK_READ)),
            Mov(MemAccess(RSP(), memoryOffsets.NO_OFFSET), RDI(RegSize.DOUBLE_WORD)),
            Lea(RSI(), MemAccess(RSP(), memoryOffsets.NO_OFFSET)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._readi_str0"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.scanf),
            Mov(RAX(RegSize.DOUBLE_WORD), MemAccess(RSP(), memoryOffsets.NO_OFFSET)),
            Add(RSP(), Imm(memoryOffsets.STACK_READ)),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    /**
     * Reads a character from standard input.
     */
    case object ReadChar extends Widget {
        val label = Label("_readc")
        override val directives = Set(StrLabel(Label(".L._readc_str0"), asciz.character))
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Sub(RSP(), Imm(memoryOffsets.STACK_READ)),
            Mov(MemAccess(RSP(), memoryOffsets.NO_OFFSET), RDI(RegSize.BYTE)),
            Lea(RSI(), MemAccess(RSP(), memoryOffsets.NO_OFFSET)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._readc_str0"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.scanf),
            Mov(RAX(RegSize.BYTE), MemAccess(RSP(), memoryOffsets.NO_OFFSET)),
            Add(RSP(), Imm(memoryOffsets.STACK_READ)),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    /**
     * Prints an integer to standard output.
     */
    case object PrintInt extends Widget {
        val label = Label("_printi")
        override val directives = Set(StrLabel(Label(".L._printi_str0"), asciz.integer))
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

    /**
     * Prints a character to standard output.
     */
    case object PrintChar extends Widget {
        val label = Label("_printc")
        override val directives = Set(StrLabel(Label(".L._printc_str0"), asciz.character))
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

    /**
     * Prints a string to standard output.
     */
    case object PrintString extends Widget {
        val label = Label("_prints")
        override val directives = Set(StrLabel(Label(".L._prints_str0"), asciz.string))
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

    /**
     * Prints a pointer to standard output.
     */
    case object PrintPointer extends Widget {
        val label = Label("_printp")
        override val directives = Set(StrLabel(Label(".L._printp_str0"), asciz.pair))
        def instructions: List[Instruction] = List(
            Push(RBP()),
            Mov(RBP(), RSP()),
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Mov(RSI(), RDI()),
            Lea(RDI(), MemAccess(RIP(), Label(".L._printp_str0"))),
            Mov(RAX(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(RDI(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
            Mov(RSP(), RBP()),
            Pop(RBP()),
            Ret
        )
    }

    /**
     * Prints a boolean value ("true" or "false") to standard output.
     */
    case object PrintBool extends Widget {
        val label = Label("_printb")
        override val directives = Set(
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

    /**
     * Prints a newline to standard output.
     */
    case object PrintLn extends Widget {
        val label = Label("_println")
        override val directives = Set(StrLabel(Label(".L._println_str0"), asciz.endl))
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

    /**
     * Allocates memory on the heap.
     */
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
        override def dependencies: Set[Widget] = Set(ErrOutOfMemory)
    }

    /**
     * Frees memory allocated on the heap.
     */
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

    /**
     * Frees a pair, checking for null pointers.
     */
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
        override def dependencies: Set[Widget] = Set(ErrNull)
    }

    /**
     * Stores a 1-byte element in an array.
     */
    case object ArrayStore1 extends Widget {
        val label = Label("_arrStore1")
        def instructions: List[Instruction] = arrStore.instructions(memoryOffsets.ARR_STORE1)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
     * Stores a 2-byte element in an array.
     */
    case object ArrayStore2 extends Widget {
        val label = Label("_arrStore2")
        def instructions: List[Instruction] = arrStore.instructions(memoryOffsets.ARR_STORE2)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
     * Stores a 4-byte element in an array.
     */
    case object ArrayStore4 extends Widget {
        val label = Label("_arrStore4")
        def instructions: List[Instruction] = arrStore.instructions(memoryOffsets.ARR_STORE4)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
     * Stores an 8-byte element in an array.
     */
    case object ArrayStore8 extends Widget {
        val label = Label("_arrStore8")
        def instructions: List[Instruction] = arrStore.instructions(memoryOffsets.ARR_STORE8)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
     * Loads a 1-byte element from an array.
     */
    case object ArrayLoad1 extends Widget {
        val label = Label("_arrLoad1")
        def instructions: List[Instruction] = arrLoad.instructions(memoryOffsets.ARR_LOAD1)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
     * Loads a 2-byte element from an array.
     */
    case object ArrayLoad2 extends Widget {
        val label = Label("_arrLoad2")
        def instructions: List[Instruction] = arrLoad.instructions(memoryOffsets.ARR_LOAD2)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }
    
    /**
     * Loads a 4-byte element from an array.
     */
    case object ArrayLoad4 extends Widget {
        val label = Label("_arrLoad4")
        def instructions: List[Instruction] = arrLoad.instructions(memoryOffsets.ARR_LOAD4)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
     * Loads an 8-byte element from an array.
     */
    case object ArrayLoad8 extends Widget {
        val label = Label("_arrLoad8")
        def instructions: List[Instruction] = arrLoad.instructions(memoryOffsets.ARR_LOAD8)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
     * Exits the program with a status code.
     */
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

/**
 * Error handling widgets that report runtime errors.
 */
object errors {
    /**
     * Base trait for all error handling widgets.
     */
    sealed trait ErrorWidget extends Widget {
        /** The error message to display */
        def message: String
        // all error widgets depend on the string printing widget
    }

    /**
     * Reports a null pointer dereference error.
     */
    case object ErrNull extends ErrorWidget {
        val label = Label("_errNull")
        override val directives = Set(StrLabel(Label(".L._errNull_str0"), message))
        def message: String = errorMessages.generateError("null pointer")
        def instructions: List[Instruction] = List(
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._errNull_str0"))),
            Call(PrintString.label),
            Mov(RDI(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    /**
     * Reports an arithmetic overflow error.
     */
    case object ErrOverflow extends ErrorWidget {
        val label = Label("_errOverflow")
        override val directives = Set(StrLabel(Label(".L._errOverflow_str0"), message))
        def message: String = errorMessages.generateError("integer overflow or underflow occurred")
        def instructions: List[Instruction] = List(
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._errOverflow_str0"))),
            Call(PrintString.label),
            Mov(RDI(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    /**
     * Reports a division by zero error.
     */
    case object ErrDivZero extends ErrorWidget {
        val label = Label("_errDivZero")
        override val directives = Set(StrLabel(Label(".L._errDivZero_str0"), message))
        def message: String = errorMessages.generateError("division or modulo by zero")
        def instructions: List[Instruction] = List(
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._errDivZero_str0"))),
            Call(PrintString.label),
            Mov(RDI(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    /**
     * Reports an array index out of bounds error.
     */
    case object ErrOutOfBounds extends ErrorWidget {
        val label = Label("_errOutOfBounds")
        override val directives = Set(StrLabel(Label(".L._errOutOfBounds_str0"), message))
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
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    /**
     * Reports an out of memory error.
     */
    case object ErrOutOfMemory extends ErrorWidget {
        val label = Label("_errOutOfMemory")
        override val directives = Set(StrLabel(Label(".L._errOutOfMemory_str0"), message))
        def message: String = errorMessages.generateError("out of memory")
        def instructions: List[Instruction] = List(
            And(RSP(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(RDI(), MemAccess(RIP(), Label(".L._errOutOfMemory_str0"))),
            Call(PrintString.label),
            Mov(RDI(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    /**
     * Reports an invalid character error.
     */
    case object ErrBadChar extends ErrorWidget {
        val label = Label("_errBadChar")
        override val directives = Set(StrLabel(Label(".L._errBadChar_str0"), message))
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
        override def dependencies: Set[Widget] = Set(PrintString)
    }
}
