package wacc.backend.generator

import wacc.backend.ir.*
import instructions.*

import widgets.*

object widgets {

    val widgets: Set[Widget] = Set(
        ReadInt,
        ReadChar,
        PrintInt,
        PrintChar,
        PrintString,
        PrintBool,
        PrintLn,
        Malloc,
        FreePair,
        ArrayStore,
        ExitProg,
        errors.ErrNull,
        errors.ErrOverflow,
        errors.ErrDivZero,
        errors.ErrOutOfBounds,
        errors.ErrOutOfMemory,
    )

    sealed trait Widget {
        val label: Label
        val directive: Directive
        def instructions: List[Instruction]
        def dependencies: Set[Widget] = Set.empty
    }

    case object ReadInt extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }

    case object ReadChar extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }

    case object PrintInt extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }

    case object PrintChar extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }

    case object PrintString extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }

    case object PrintBool extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }

    case object PrintLn extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }

    case object Malloc extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }

    case object FreePair extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }

    case object ArrayStore extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }

    case object ExitProg extends Widget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
    }
}

object errors {
    sealed trait ErrorWidget extends Widget {
        def message: String
        override def dependencies: Set[Widget] = Set(widgets.PrintString)
    }

    // TODO: Use the errorMessage to generate the errors

    case object ErrNull extends ErrorWidget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
        def message: String = ???
    }

    case object ErrOverflow extends ErrorWidget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
        def message: String = ???
    }

    case object ErrDivZero extends ErrorWidget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
        def message: String = ???
    }

    case object ErrOutOfBounds extends ErrorWidget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
        def message: String = ???
    }

    case object ErrOutOfMemory extends ErrorWidget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
        def message: String = ???
    }

    case object ErrBadChar extends ErrorWidget {
        val label = ???
        val directive = ???
        def instructions: List[Instruction] = ???
        def message: String = ???
    }
}

object errorMessages {
    final val overflow = ???
    final val divideByZero = ???
    final val nullPointer = ???
    final val arrayOutOfBounds = ???
    final val outOfMemory = ???
    final val badChar = ???

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
