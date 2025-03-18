package wacc.extension.decompiler

import java.io.{File, OutputStream}
import scala.collection.mutable

import wacc.error.errors.*
import representation.*

private final val NO_INDENT = 0
private final val INDENT = 4

/** Stores the errors of the program. */
class Generator(errs: mutable.Builder[WaccError, List[WaccError]]) {
    def errors: List[WaccError] = errs.result()

    // add an error to the context
    def error(err: WaccError) = {
        errs += err
        None
    }
}

/** Generates code in the provided target language. */
def generate(program: Program, file: File, flags: Seq[String]): Option[WaccError] = {
    given language: ProgrammingLanguage =
        if (flags.isEmpty || flags(0) != "--language=C") WaccLanguage else CLanguage
    given generator: Generator =
        Generator(List.newBuilder)

    given outputStream: OutputStream = os.write.outputStream(
        os.pwd / s"${file.getName.stripSuffix(".s")}.${language.fileExtension}"
    )

    val Program(funcs, main) = appendTypes(language.clean(program))

    try {
        outputStream.write(language.libraries.mkString("\n").getBytes)
        outputStream.write("\n".getBytes)
        outputStream.write(language.programBegin.getBytes)
        outputStream.write("\n\n".getBytes)

        // generate the functions
        funcs.foreach(generate)
        if (funcs.nonEmpty) {
            outputStream.write("\n\n".getBytes)
        }

        // generate the main body
        generateStmts(main, NO_INDENT)

        outputStream.write("\n".getBytes)
        outputStream.write(language.programEnd.getBytes)
        outputStream.write("\n".getBytes)
    } catch {
        case e: Throwable =>
            println(s"Could not write to the output stream: ${e.getMessage}")
    }
    finally {
        outputStream.close()
    }

    generator.errors match {
        case Nil => None
        case error :: _ => Some(error)
    }
}

/** Generates code for a function. */
def generate(function: Func)
            (using generator: Generator)
            (using outputStream: OutputStream, language: ProgrammingLanguage): Unit = {
    val Func((ty, name), params, stmts) = function

    // write the signature of the function
    outputStream.write(s"${generate(ty)} ${generate(name)} (${params.map(generate).mkString(", ")}) ${language.functionBegin}\n".getBytes)
    generateStmts(stmts, INDENT)
    outputStream.write(s"${language.functionEnd}\n".getBytes)
}

/** Generates code for a block of statements. */
def generateStmts(statements: StatementList, indent: Int)
                 (using generator: Generator)
                 (using outputStream: OutputStream, language: ProgrammingLanguage): Unit = {
    if (statements.isEmpty) {
        // if the body is empty, use a no-operation
        outputStream.write(language.skip.getBytes)
    } else {
        // generate the first statement
        outputStream.write(generate(statements(0), indent).getBytes)

        statements.drop(1).foreach { stmt =>
            outputStream.write(language.betweenStatements.getBytes)
            outputStream.write("\n".getBytes)
            outputStream.write(generate(stmt, indent).getBytes)
        }

        outputStream.write(language.afterStatements.getBytes)
    }
    outputStream.write("\n".getBytes)
}

/** Generates code for a statement with the provided indentation. */
def generate(statement: Statement, indent: Int)
            (using generator: Generator)
            (using outputStream: OutputStream, language: ProgrammingLanguage): String = (" " * indent).concat(statement match {
    case Declaration((ty, id), rvalue) =>
        s"${generate(ty)} ${generate(id)} = ${generate(rvalue)}"
    case Assignment(id, rvalue) =>
        s"${generate(id)} = ${generate(rvalue)}"
    case Read(lvalue) => s"${language.read} ${generate(lvalue)}"
    case Print(expr) => s"${language.print} ${generate(expr)}"
    case Println(expr) => s"${language.println} ${generate(expr)}"
    case Free(expr) => s"${language.free} ${generate(expr)}"
    case Return(expr) => s"${language.ret} ${generate(expr)}"
    case Exit(expr) => s"${language.exit} ${generate(expr)}"

    case If(cond, thenStatements, elseStatements) =>
        outputStream.write(s"${(" " * indent)}if ${language.condStart}${generate(cond)}${language.condEnd} ${language.thenStart}\n".getBytes)
        generateStmts(thenStatements, indent + INDENT)
        outputStream.write(s"${(" " * indent)}${language.elseStart}\n".getBytes)
        generateStmts(elseStatements, indent + INDENT)
        s"${language.elseEnd}"
        
    case While(cond, doStatements) =>
        outputStream.write(s"${(" " * indent)}while ${language.condStart}${generate(cond)}${language.condEnd} ${language.whileStart}\n".getBytes)
        generateStmts(doStatements, indent + INDENT)
        s"${language.whileEnd}"
    
    case Block(block) =>
        outputStream.write(s"${(" " * indent)}${language.blockBegin}\n".getBytes)
        generateStmts(block, indent + INDENT)
        s"${language.blockEnd}"
})

/** Generates code for an expression. */
def generate(expr: Expression)
            (using generator: Generator)
            (using language: ProgrammingLanguage): String = expr match {
    case IntLit(value) => s"$value"
    case BoolLit(value) => language.boolLiteral(value)
    case CharLit(value) => s"\'$value\'"
    case StrLit(value) => s"\"$value\""
    case PairLit => language.pairLiteral
    case Id(value) => value
    case ArrayElem(id, exprs) => language.arrayElement(generate(id), exprs.map(generate))
    case ParensExpression(expr) => s"(${generate(expr)})"
    
    case ArrayLit(exprs: List[Expression]) => language.arrayLit(exprs.map(generate))
    case NewPair(fst: Expression, snd: Expression) => language.newPair(generate(fst), generate(snd))
    case Call(func: Id, args: List[Expression]) => language.call(generate(func), args.map(generate))
    case Fst(value) => s"${language.fst(generate(value))}"
    case Snd(value) => s"${language.snd(generate(value))}"

    case BinaryOp(lhs, rhs, op) => s"${generate(lhs)} ${generate(op)} ${generate(rhs)}"
    case UnaryOp(expr, op)      => s"${generate(op)}${generate(expr)}"
}

/** Generates code for a type. */
def generate(ty: Type)
            (using generator: Generator)
            (using language: ProgrammingLanguage): String = ty match {
    case IntType => language.intType
    case BoolType => language.boolType
    case CharType => language.charType
    case StrType => language.stringType
    case ArrayType(ty) => language.arrType(generate(ty), 1)
    case PairType(fst, snd) => language.pairType(generate(fst), generate(snd))
    case Unset => language.intType
    // if type cannot be inferred, consider: generator.error(DecompilerError)
}

/** Generates code for a typed variable. */
def generate(typeId: TypeId)
            (using generator: Generator)
            (using language: ProgrammingLanguage): String =
    s"${generate(typeId._1)} ${generate(typeId._2)}"

/** Generates a binary operation. */
def generate(op: BinaryOperation): String = op match {
    case BinaryOperation.Or        => "||"
    case BinaryOperation.And       => "&&"
    case BinaryOperation.Equal     => "=="
    case BinaryOperation.NotEqual  => "!="
    case BinaryOperation.Greater   => ">"
    case BinaryOperation.GreaterEq => ">="
    case BinaryOperation.Less      => "<"
    case BinaryOperation.LessEq    => "<="
    case BinaryOperation.Add       => "+"
    case BinaryOperation.Sub       => "-"
    case BinaryOperation.Mul       => "*"
    case BinaryOperation.Div       => "/"
    case BinaryOperation.Mod       => "%"
}

/** Generates a unary operation. */
def generate(op: UnaryOperation): String = op match {
    case UnaryOperation.Not => "!"
    case UnaryOperation.Neg => "-"
    case UnaryOperation.Len => "len "
    case UnaryOperation.Ord => "ord "
    case UnaryOperation.Chr => "chr "
}
