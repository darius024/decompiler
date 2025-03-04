package wacc

import java.io.File
import parsley.{Success, Failure}
import wacc.backend.*
import wacc.frontend.*
import wacc.backend.formatter.SyntaxStyle
import java.io.OutputStream

// compilation pipeline
def compile(file: File): (String, ExitCode) = {
    // parsing and syntax analysis
    val ast = parser.parse(file) match {
        case Success(ast) => ast
        case Failure(err) => return (s"${err.message}", ExitCode.SyntaxErr)
    }

    // semantic analysis: scope and type checking
    val tyAst = semantics.check(ast) match {
        case Right(tyAst) => tyAst
        case Left(errs) => return (s"${semantics.format(errs, file)}", ExitCode.SemanticErr)
    }

    // code generation
    val gen = generator.generate(tyAst)

    // open output file: {prog}.wacc --> {prog}.s
    given out: OutputStream = os.write.outputStream(
        os.pwd / s"${file.getName.stripSuffix(".wacc")}.s"
    )

    // asm formatting
    try {
        formatter.format(gen, SyntaxStyle.Intel)
    } finally {
        out.close()
    }

    ("Code compiled successfully.", ExitCode.Success)
}

/** Using an enum of fixed codes to prevent invalid codes being used. */
enum ExitCode(val code: Int) {
    case Success     extends ExitCode(0)
    case SyntaxErr   extends ExitCode(100)
    case SemanticErr extends ExitCode(200)

    def enforce(): Unit = System.exit(code)
}

// entry point
@main
def main(path: String): Unit = {
    val (errs, code) = compile(new File(path))
    println(errs)
    code.enforce()
}