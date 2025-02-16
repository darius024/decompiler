package wacc

import java.io.File
import parsley.{Success, Failure}
import scala.annotation.unused

import wacc.frontend.*

// entry point
@main
def main(path: String): Unit = {
    val (errs, code) = compile(new File(path))
    println(errs)
    code.enforce()
}

// compilation pipeline
def compile(file: File): (String, ExitCode) = {
    // parsing and syntax analysis
    val ast = parser.parse(file) match {
        // on successful compilation, the AST is returned
        case Success(ast) => ast
        // otherwise, there is a syntax error
        case Failure(err) => return (s"${err.message}", ExitCode.SyntaxErr)
    }

    // semantic analysis: scope and type checking
    @unused val typedAst = semantics.check(ast) match {
        // on successful analysis, the typed AST is returned
        case Right(tyAst) => tyAst
        // otherwise, there is a semantic error
        case Left(errs) => return (s"${semantics.format(errs, file)}", ExitCode.SemanticErr)
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
