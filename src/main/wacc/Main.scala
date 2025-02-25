package wacc

import java.io.File
import parsley.{Success, Failure}
import wacc.backend.*
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
    val typedAst = semantics.check(ast) match {
        // on successful analysis, the typed AST is returned
        case Right(tyAst) => tyAst
        // otherwise, there is a semantic error
        case Left(errs) => return (s"${semantics.format(errs, file)}", ExitCode.SemanticErr)
    }

    // code generation and assembly formatter
    formatter.format(generator.generate(typedAst), file)

    ("Code compiled successfully.", ExitCode.Success)
}

/** Using an enum of fixed codes to prevent invalid codes being used. */
enum ExitCode(val code: Int) {
    case Success     extends ExitCode(exitCodes.SUCCESS)
    case SyntaxErr   extends ExitCode(exitCodes.SYNTAX_ERROR)
    case SemanticErr extends ExitCode(exitCodes.SEMANTIC_ERROR)

    def enforce(): Unit = System.exit(code)
}

object exitCodes {
    final val SUCCESS = 0
    final val SYNTAX_ERROR = 100
    final val SEMANTIC_ERROR = 200
}
