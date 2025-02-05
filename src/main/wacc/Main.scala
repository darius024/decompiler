package wacc

import parsley.{Success, Failure}
import wacc.semantics.checkSemantics
import java.io.File

// entry point
def main(args: Array[String]): Unit = args.headOption match {
    case Some(path) => {
        val (errs, code) = compile(new File(path))
        println(errs)
        code.enforce()
    }
    case None => println("No program has been entered.")
}

// compilation pipeline
def compile(file: File): (String, ExitCode) = {
    // parse and check syntax
    var ast = parser.parseFile(file) match {
        case Success(ast) => ast
        case Failure(msg) => return (s"$msg", ExitCode.SyntaxErr)
    }

    // check semantics
    ast = checkSemantics(ast) match {
        case Right(ast) => ast
        case Left(errs) => return (s"$errs", ExitCode.SemanticErr)
    }

    // TODO further stages (perhaps optimising AST?)

    return ("Code compiled successfully", ExitCode.Success)
}

/** Using an enum of fixed values to prevent invalid codes being used. */
enum ExitCode(val value: Int) {
    case Success extends ExitCode(0)
    case SyntaxErr extends ExitCode(100)
    case SemanticErr extends ExitCode(200)

    def enforce(): Unit = System.exit(value)
}
