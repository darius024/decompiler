package wacc.extension.decompiler

import java.io.File
import parsley.{Parsley, Result, Failure}
import parsley.Parsley.{many}
import scala.util.{Success => TrySuccess, Failure => TryFailure}

import lexer.*
import implicits.implicitSymbol

import wacc.backend.ir.*
import immediate.*
import instructions.*
import memory.*
import registers.*

/** Formulates the grammar rules the parser should follow. */
object parser {
    type IRProgram = List[Instruction]

    private lazy val label: Parsley[Label] = ???

    private lazy val immediate: Parsley[Immediate] = ???

    private lazy val register: Parsley[Register] = ???

    private lazy val memoryAccess: Parsley[MemoryAccess] = ???

    private lazy val directive: Parsley[StrLabel] = ???

    private lazy val instruction: Parsley[Instruction] = ???

    private lazy val program: Parsley[IRProgram] = many(instruction)

    // top-level parsers
    
    private val parser = fully(program)

    def parse(file: File): Result[String, IRProgram] = parser.parseFile[String](file) match {
        case TrySuccess(result) => result
        case TryFailure(_)      => Failure("could not open file")
    }

    def parse(input: String): Result[String, IRProgram] = parser.parse[String](input)
}
