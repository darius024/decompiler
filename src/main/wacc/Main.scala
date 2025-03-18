package wacc

import java.io.{File, OutputStream}
import parsley.{Success, Failure}

import wacc.backend.*
import wacc.extension.*
import wacc.frontend.*

private final val AssemblySyntax = formatter.SyntaxStyle.Intel

/** Entry point of the program. */
@main
def main(path: String, flags: String*): Unit = {
    val (errs, code) = if (path.endsWith(decompiler.WaccLanguage.fileExtension)) {
        compile(new File(path), flags)
    } else {
        decompile(new File(path), flags)
    }
    println(errs)
    code.enforce
}

/** Compile the given input file and transform it through all the pipeline steps. */
def compile(file: File, flags: Seq[String] = Seq.empty): (String, ExitCode) = {
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

    // create output file: {prog}.wacc --> {prog}.s
    given outputStream: OutputStream = os.write.outputStream(
        os.pwd / s"${file.getName.stripSuffix(".wacc")}.s"
    )

    // generate the intermediate representation
    val optimiseRegisters = optimisation.optimiseRegs(flags)
    val instructions = generator.generate(typedAst, optimiseRegisters)

    // perform optimisations on the intermediate representation
    val optimisedInstructions = optimisation.optimise(instructions, flags)

    // format the instructions into assembly
    formatter.format(optimisedInstructions, AssemblySyntax)

    ("Code compiled successfully.", ExitCode.Success)
}

/** Decompile the given input file and transform it through all the pipeline steps. */
def decompile(file: File, flags: Seq[String] = Seq.empty): (String, ExitCode) = {
    // parsing and syntax analysis
    val instructions = decompiler.parser.parse(file) match {
        // on successful compilation, the AST is returned
        case Success(instructions) => instructions
        // otherwise, there is a syntax error
        case Failure(err) => return (s"${err.message}", ExitCode.SyntaxErr)
    }

    // perform all the stages of the backend
    val controller = decompiler.controlFlow(instructions)
    val program = decompiler.transform(decompiler.disassemble(controller))
    val errors = decompiler.generate(program, file, flags)

    errors match {
        case None      => ("Code decompiled successfully.", ExitCode.Success)
        case Some(err) => (s"${err.message}", ExitCode.SyntaxErr)
    }
}

/** Using an enum of fixed codes to prevent invalid codes being used. */
enum ExitCode(val code: Int) {
    case Success     extends ExitCode(exitCodes.SUCCESS)
    case SyntaxErr   extends ExitCode(exitCodes.SYNTAX_ERROR)
    case SemanticErr extends ExitCode(exitCodes.SEMANTIC_ERROR)

    // exit with the provided code
    def enforce: Unit = System.exit(code)
}

/** Exit codes for program exit status. */
object exitCodes {
    final val SUCCESS = 0
    final val SYNTAX_ERROR = 100
    final val SEMANTIC_ERROR = 200
}
