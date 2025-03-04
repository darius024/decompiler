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
    def generateError(messages: String*): String = s"fatal error: ${messages.mkString("\n")}\n"
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
    def instructions(regSize: RegSize): List[Instruction] = List(
        Push(RBX()),
        // check for negative index
        Test(R10(RegSize.DOUBLE_WORD), R10(RegSize.DOUBLE_WORD)),
        CMov(ARG2(), R10(), CompFlag.L),
        JumpComp(ErrOutOfBounds.label, CompFlag.L),
        // check for index >= array length
        Mov(RBX(RegSize.DOUBLE_WORD), MemAccess(R9(), memoryOffsets.ARRAY_LENGTH_OFFSET, RegSize.DOUBLE_WORD)),
        Cmp(R10(RegSize.DOUBLE_WORD), RBX(RegSize.DOUBLE_WORD)),
        CMov(ARG2(), R10(), CompFlag.GE),
        JumpComp(ErrOutOfBounds.label, CompFlag.GE),
        // store the element
        Mov(MemRegAccess(R9(), R10(), regSize.size, regSize), RETURN_REG(regSize)),
        Pop(RBX()),
        Ret
    )
}

/**
  * Shared implementation for loading elements from arrays.
  */
object arrLoad {
    def instructions(regSize: RegSize): List[Instruction] = List(
        Push(RBX()),
        // check for negative index
        Test(R10(RegSize.DOUBLE_WORD), R10(RegSize.DOUBLE_WORD)),
        CMov(ARG2(), R10(), CompFlag.L),
        JumpComp(ErrOutOfBounds.label, CompFlag.L),
        // check for index >= array length
        Mov(RBX(RegSize.DOUBLE_WORD), MemAccess(R9(), memoryOffsets.ARRAY_LENGTH_OFFSET, RegSize.DOUBLE_WORD)),
        Cmp(R10(RegSize.DOUBLE_WORD), RBX(RegSize.DOUBLE_WORD)),
        CMov(ARG2(), R10(), CompFlag.GE),
        JumpComp(ErrOutOfBounds.label, CompFlag.GE),
        // load the element
        Mov(R9(regSize), MemRegAccess(R9(), R10(), regSize.size, regSize)),
        Pop(RBX()),
        Ret
    )
}

/**
  * Runtime support functions (widgets) used by the generated code.
  */
object widgets {
    /** Complete set of all available widgets. */
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
      * Wrap all widgets uniformly to adhere to the calling convention.
      */ 
    private def adhereToCallingConvention(instructions: List[Instruction]): List[Instruction] =
        List(Push(FRAME_REG()), Mov(FRAME_REG(), STACK_REG()), And(STACK_REG(), Imm(memoryOffsets.STACK_ALIGNMENT)))
        :::
        instructions
        :::
        List(Mov(STACK_REG(), FRAME_REG()), Pop(FRAME_REG()), Ret)

    /**
      * Reads an integer from standard input.
      */
    case object ReadInt extends Widget {
        val label = Label("_readi")
        override val directives = Set(StrLabel(Label(".L._readi_str"), asciz.integer))
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Sub(STACK_REG(), Imm(memoryOffsets.STACK_READ)),
            Mov(MemAccess(STACK_REG(), memoryOffsets.NO_OFFSET, RegSize.DOUBLE_WORD), ARG1(RegSize.DOUBLE_WORD)),
            Lea(ARG2(), MemAccess(STACK_REG(), memoryOffsets.NO_OFFSET)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._readi_str"))),
            Mov(RETURN_REG(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.scanf),
            Mov(RETURN_REG(RegSize.DOUBLE_WORD), MemAccess(STACK_REG(), memoryOffsets.NO_OFFSET, RegSize.DOUBLE_WORD)),
            Add(STACK_REG(), Imm(memoryOffsets.STACK_READ)),
        ))
    }

    /**
      * Reads a character from standard input.
      */
    case object ReadChar extends Widget {
        val label = Label("_readc")
        override val directives = Set(StrLabel(Label(".L._readc_str"), asciz.characterRead))
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Sub(STACK_REG(), Imm(memoryOffsets.STACK_READ)),
            Mov(MemAccess(STACK_REG(), memoryOffsets.NO_OFFSET, RegSize.BYTE), ARG1(RegSize.BYTE)),
            Lea(ARG2(), MemAccess(STACK_REG(), memoryOffsets.NO_OFFSET)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._readc_str"))),
            Mov(RETURN_REG(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.scanf),
            Mov(RETURN_REG(RegSize.BYTE), MemAccess(STACK_REG(), memoryOffsets.NO_OFFSET, RegSize.BYTE)),
            Add(STACK_REG(), Imm(memoryOffsets.STACK_READ)),
        ))
    }

    /**
      * Prints an integer to standard output.
      */
    case object PrintInt extends Widget {
        val label = Label("_printi")
        override val directives = Set(StrLabel(Label(".L._printi_str"), asciz.integer))
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Mov(ARG2(RegSize.DOUBLE_WORD), ARG1(RegSize.DOUBLE_WORD)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._printi_str"))),
            Mov(RETURN_REG(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(ARG1(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
        ))
    }

    /**
      * Prints a character to standard output.
      */
    case object PrintChar extends Widget {
        val label = Label("_printc")
        override val directives = Set(StrLabel(Label(".L._printc_str"), asciz.characterPrint))
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Mov(ARG2(RegSize.BYTE), ARG1(RegSize.BYTE)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._printc_str"))),
            Mov(RETURN_REG(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(ARG1(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
        ))
    }

    /**
      * Prints a string to standard output.
      */
    case object PrintString extends Widget {
        val label = Label("_prints")
        override val directives = Set(StrLabel(Label(".L._prints_str"), asciz.string))
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Mov(ARG3(), ARG1()),
            Mov(ARG2(RegSize.DOUBLE_WORD), MemAccess(ARG1(), memoryOffsets.ARRAY_LENGTH_OFFSET, RegSize.DOUBLE_WORD)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._prints_str"))),
            Mov(RETURN_REG(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(ARG1(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
        ))
    }

    /**
      * Prints a pointer to standard output.
      */
    case object PrintPointer extends Widget {
        val label = Label("_printp")
        override val directives = Set(StrLabel(Label(".L._printp_str"), asciz.pair))
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Mov(ARG2(), ARG1()),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._printp_str"))),
            Mov(RETURN_REG(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(ARG1(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
        ))
    }

    /**
      * Prints a boolean value ("true" or "false") to standard output.
      */
    case object PrintBool extends Widget {
        val label = Label("_printb")
        override val directives = Set(
            StrLabel(Label(".L._printb_str"), "false"),
            StrLabel(Label(".L._printb_str_true"), "true"),
            StrLabel(Label(".L._printb_str_false"), asciz.string)
        )
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Cmp(ARG1(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            JumpComp(Label(".L_printb_true"), CompFlag.NE),
            Lea(ARG3(), MemAccess(RIP(), Label(".L._printb_str"))),
            Jump(Label(".L_printb_false"), JumpFlag.Unconditional),
            Label(".L_printb_true"),
            Lea(ARG3(), MemAccess(RIP(), Label(".L._printb_str_true"))),
            Label(".L_printb_false"),
            Mov(ARG2(RegSize.DOUBLE_WORD), MemAccess(ARG3(), memoryOffsets.ARRAY_LENGTH_OFFSET, RegSize.DOUBLE_WORD)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._printb_str_false"))),
            Mov(RETURN_REG(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(ARG1(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
        ))
    }

    /**
      * Prints a newline to standard output.
      */
    case object PrintLn extends Widget {
        val label = Label("_println")
        override val directives = Set(StrLabel(Label(".L._println_str"), asciz.endl))
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Lea(ARG1(), MemAccess(RIP(), Label(".L._println_str"))),
            Call(library.puts),
            Mov(ARG1(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
        ))
    }

    /**
      * Allocates memory on the heap.
      */
    case object Malloc extends Widget {
        val label = Label("_malloc")
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Call(library.malloc),
            Cmp(RETURN_REG(), Imm(memoryOffsets.NO_OFFSET)),
            JumpComp(ErrOutOfMemory.label, CompFlag.E),
        ))
        override def dependencies: Set[Widget] = Set(ErrOutOfMemory)
    }

    /**
      * Frees memory allocated on the heap.
      */
    case object FreeProg extends Widget {
        val label = Label("_free")
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Call(library.free),
        ))
    }

    /**
      * Frees a pair, checking for null pointers.
      */
    case object FreePair extends Widget {
        val label = Label("_freepair")
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Cmp(ARG1(), Imm(memoryOffsets.NO_OFFSET)),
            JumpComp(ErrNull.label, CompFlag.E),
            Call(library.free),
        ))
        override def dependencies: Set[Widget] = Set(ErrNull)
    }

    /**
      * Stores a 1-byte element in an array.
      */
    case object ArrayStore1 extends Widget {
        val label = Label("_arrStore1")
        def instructions: List[Instruction] = arrStore.instructions(RegSize.BYTE)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
      * Stores a 2-byte element in an array.
      */
    case object ArrayStore2 extends Widget {
        val label = Label("_arrStore2")
        def instructions: List[Instruction] = arrStore.instructions(RegSize.WORD)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
      * Stores a 4-byte element in an array.
      */
    case object ArrayStore4 extends Widget {
        val label = Label("_arrStore4")
        def instructions: List[Instruction] = arrStore.instructions(RegSize.DOUBLE_WORD)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
      * Stores an 8-byte element in an array.
      */
    case object ArrayStore8 extends Widget {
        val label = Label("_arrStore8")
        def instructions: List[Instruction] = arrStore.instructions(RegSize.QUAD_WORD)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
      * Loads a 1-byte element from an array.
      */
    case object ArrayLoad1 extends Widget {
        val label = Label("_arrLoad1")
        def instructions: List[Instruction] = arrLoad.instructions(RegSize.BYTE)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
      * Loads a 2-byte element from an array.
      */
    case object ArrayLoad2 extends Widget {
        val label = Label("_arrLoad2")
        def instructions: List[Instruction] = arrLoad.instructions(RegSize.WORD)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }
    
    /**
      * Loads a 4-byte element from an array.
      */
    case object ArrayLoad4 extends Widget {
        val label = Label("_arrLoad4")
        def instructions: List[Instruction] = arrLoad.instructions(RegSize.DOUBLE_WORD)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
      * Loads an 8-byte element from an array.
      */
    case object ArrayLoad8 extends Widget {
        val label = Label("_arrLoad8")
        def instructions: List[Instruction] = arrLoad.instructions(RegSize.QUAD_WORD)
        override def dependencies: Set[Widget] = Set(ErrOutOfBounds)
    }

    /**
      * Exits the program with a status code.
      */
    case object ExitProg extends Widget {
        val label = Label("_exit")
        def instructions: List[Instruction] = adhereToCallingConvention(List(
            Call(library.exit),
        ))
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
    }

    /**
      * Reports a null pointer dereference error.
      */
    case object ErrNull extends ErrorWidget {
        val label = Label("_errNull")
        override val directives = Set(StrLabel(Label(".L._errNull_str"), message))
        def message: String = errorMessages.generateError(
            "null pointer",
            "dereferencing a null pointer is not allowed"
        )
        def instructions: List[Instruction] = List(
            And(STACK_REG(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._errNull_str"))),
            Call(PrintString.label),
            Mov(ARG1(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    /**
      * Reports an arithmetic overflow error.
      */
    case object ErrOverflow extends ErrorWidget {
        val label = Label("_errOverflow")
        override val directives = Set(StrLabel(Label(".L._errOverflow_str"), message))
        def message: String = errorMessages.generateError(
            "integer overflow or underflow occurred",
            "ensure all operations can be computed within 32 bits"
        )
        def instructions: List[Instruction] = List(
            And(STACK_REG(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._errOverflow_str"))),
            Call(PrintString.label),
            Mov(ARG1(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    /**
      * Reports a division by zero error.
      */
    case object ErrDivZero extends ErrorWidget {
        val label = Label("_errDivZero")
        override val directives = Set(StrLabel(Label(".L._errDivZero_str"), message))
        def message: String = errorMessages.generateError(
            "division or modulo by zero",
            "enusre the divisor is non-zero"
        )
        def instructions: List[Instruction] = List(
            And(STACK_REG(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._errDivZero_str"))),
            Call(PrintString.label),
            Mov(ARG1(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    /**
      * Reports an array index out of bounds error.
      */
    case object ErrOutOfBounds extends ErrorWidget {
        val label = Label("_errOutOfBounds")
        override val directives = Set(StrLabel(Label(".L._errOutOfBounds_str"), message))
        def message: String = errorMessages.generateError(
            "array index %d is out of bounds",
            "ensure all memory accesses are within range"
        )
        def instructions: List[Instruction] = List(
            And(STACK_REG(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._errOutOfBounds_str"))),
            Mov(RETURN_REG(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(ARG1(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
            Mov(ARG1(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    /**
      * Reports an out of memory error.
      */
    case object ErrOutOfMemory extends ErrorWidget {
        val label = Label("_errOutOfMemory")
        override val directives = Set(StrLabel(Label(".L._errOutOfMemory_str"), message))
        def message: String = errorMessages.generateError(
            "out of memory",
            "allocating memory failed for the corresponding array or pair"
        )
        def instructions: List[Instruction] = List(
            And(STACK_REG(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._errOutOfMemory_str"))),
            Call(PrintString.label),
            Mov(ARG1(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
        override def dependencies: Set[Widget] = Set(PrintString)
    }

    /**
      * Reports an invalid character error.
      */
    case object ErrBadChar extends ErrorWidget {
        val label = Label("_errBadChar")
        override val directives = Set(StrLabel(Label(".L._errBadChar_str"), message))
        def message: String = errorMessages.generateError(
            "invalid character",
            "provided integer value for ASCII character is not within (0, 127)"
        )
        def instructions: List[Instruction] = List(
            And(STACK_REG(), Imm(memoryOffsets.STACK_ALIGNMENT)),
            Lea(ARG1(), MemAccess(RIP(), Label(".L._errBadChar_str"))),
            Mov(RETURN_REG(RegSize.BYTE), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.printf),
            Mov(ARG1(), Imm(memoryOffsets.NO_OFFSET)),
            Call(library.fflush),
            Mov(ARG1(RegSize.BYTE), Imm(errorCodes.FAILURE)),
            Call(library.exit)
        )
        override def dependencies: Set[Widget] = Set(PrintString)
    }
}
