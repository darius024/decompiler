package wacc

import java.io.File
import parsley.{Success, Failure}

import wacc.semantics.checkSemantics

def main(args: Array[String]): Unit = {
    args.headOption match {
        case Some(program) => {
            val (errs, exitCode) = compile(new File(program))
            println(errs)
            System.exit(exitCode)
        }
        case None => println("please enter a program")
    }
}

def compile(program: File): (String, Int) =
    parser.parseFile(program) match {
        case Success(progAst) => checkSemantics(progAst) match {
            case Right(_)   => ("succeed", exitCodes.SuccessfulCompilation)
            case Left(errs) => (s"$errs", exitCodes.SemanticError)
        }
        case Failure(msg)     => (msg, exitCodes.SyntaxError)
    }

object exitCodes {
    val SuccessfulCompilation = 0
    val SyntaxError           = 100
    val SemanticError         = 200
}
