package wacc

import java.io.{File, OutputStream}
import parsley.{Success, Failure}

import wacc.backend.*
import wacc.frontend.*

import wacc.backend.formatter.SyntaxStyle
import wacc.backend.generator.* 
import wacc.backend.ir.instructions.*
import wacc.backend.optimisation.*

/** Entry point of the program. */
@main
def main(path: String, opt: String = "none", syntax: String = "intel"): Unit = {
    val optimisationConfig = parseOptimisations(opt)

    val syntaxStyle = parseSyntaxStyle(syntax)
    val (errs, code) = compile(new File(path), optimisationConfig, syntaxStyle)
    println(errs)
    code.enforce
}

// parse comma-separated optmisation flags
def parseOptimisations(opt: String): OptimisationConfig = opt match {
    case "none" => OptimisationConfig()
    case "all" => OptimisationConfig().all
    case _ => 
    val flags = opt.split(",").map(_.trim.toLowerCase)
    val types = flags.flatMap { flag =>
      OptimisationType.values.find(_.flagName == flag) match {
        case Some(optType) => Some(optType)
        case None => 
          println(s"Unknown optimisation flag: $flag")
          None
      }
    }
    OptimisationConfig(enabledOpts = types.toSet)
}

// parse syntax style
def parseSyntaxStyle(syntax: String): SyntaxStyle = syntax match {
    case "intel" => SyntaxStyle.Intel
    case "att" => SyntaxStyle.ATT
    case _ => 
        println(s"Unknown syntax style: $syntax. Defaulting to Intel.")
        SyntaxStyle.Intel
}

// Apply the optmisation flags
def applyOptimisations(codeGen: CodeGenerator, config: OptimisationConfig): CodeGenerator = {
    var instructions = codeGen.ir

    if (config.isEnabled) {
        if (config.peephole) {
            instructions = peephole(instructions)
        }

        // Add more optimisations here


        // Update code
        val newInstructionBuilder = List.newBuilder[Instruction]
        instructions.foreach(newInstructionBuilder.addOne)
        codeGen.instructions = newInstructionBuilder
    } 
    codeGen

}



/** Compile the given input file and transform it through all the pipeline steps. */
def compile(file: File, 
            config: OptimisationConfig = OptimisationConfig(), 
            syntaxStyle: SyntaxStyle = SyntaxStyle.Intel 
            ): (String, ExitCode) = {
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

    // Output optmisation info if any optmisations were applied
    if (config.isEnabled) {
        val appliedOpts = config.enabledOptimisations.mkString(", ")
        println(s"Applying optimizations: $appliedOpts")
    }

    val irAssembly = generator.generate(typedAst)

    // create output file: {prog}.wacc --> {prog}.s
    given outputStream: OutputStream = os.write.outputStream(
        os.pwd / s"${file.getName.stripSuffix(".wacc")}.s"
    )

    // code generation and assembly formatter
    formatter.format(applyOptimisations(irAssembly, config), syntaxStyle)

    ("Code compiled successfully.", ExitCode.Success)
}

/** Using an enum of fixed codes to prevent invalid codes being used. */
enum ExitCode(val code: Int) {
    case Success     extends ExitCode(exitCodes.SUCCESS)
    case SyntaxErr   extends ExitCode(exitCodes.SYNTAX_ERROR)
    case SemanticErr extends ExitCode(exitCodes.SEMANTIC_ERROR)

    def enforce: Unit = System.exit(code)
}

/** Exit codes for program exit status. */
object exitCodes {
    final val SUCCESS = 0
    final val SYNTAX_ERROR = 100
    final val SEMANTIC_ERROR = 200
}
