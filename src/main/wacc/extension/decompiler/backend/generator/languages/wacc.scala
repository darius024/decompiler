package wacc.extension.decompiler

import representation.*

/** WACC language specification. */
object WaccLanguage extends ProgrammingLanguage {
    private final val PRINT = "_print"
    private final val PRINTLN = "_println"
    private final val READ = "_read"
    private final val EXIT = "_exit"
    private final val ERR = "_err"
    private final val FREE = "_free"

    def libraries: List[String] = Nil

    def fileExtension: String = "wacc"

    def functionBegin: String = "is"
    def functionEnd: String = "end"

    def programBegin: String = "begin"
    def programEnd: String = "end"

    def blockBegin: String = "begin"
    def blockEnd: String = "end"

    // types
    def intType: String = "int"
    def boolType: String = "bool"
    def charType: String = "char"
    def stringType: String = "string"
    def arrType(ty: String, dimension: Int): String = s"$ty${"[]" * dimension}"
    def pairType(fst: String, snd: String): String = s"pair($fst, $snd)"

    def boolLiteral(value: Boolean): String = if (value) "true" else "false"
    def pairLiteral: String = "null"

    def arrayElement(id: String, indices: List[String]): String = s"$id${indices.map(i => s"[$i]").mkString("")}"
    def arrayLit(exprs: List[String]): String = s"[${exprs.mkString(", ")}]"
    def newPair(fst: String, snd: String): String = s"newpair($fst, $snd)"
    def call(func: String, args: List[String]): String = s"call $func(${args.mkString(", ")})"

    def fst(pair: String): String = s"fst $pair"
    def snd(pair: String): String = s"snd $pair"
    def pairAccessStart: String = ""
    def pairAccessEnd: String = ""

    def read: String = "read"
    def print: String = "print"
    def println: String = "println"
    def free: String = "free"
    def ret: String = "return"
    def exit: String = "exit"

    def thenStart: String = "then"
    def thenEnd: String = ""
    def elseStart: String = "else"
    def elseEnd: String = "fi"

    def whileStart: String = "do"
    def whileEnd: String = "done"

    def skip: String = "skip"

    def condStart: String = "("
    def condEnd: String = ")"

    def betweenStatements: String = ";"
    def afterStatements: String = ""

    def clean(program: Program): Program = {
        val Program(funcs, main) = program

        val newFuncs = funcs.filter {
            case Func((ty, name), _, stmts) => !name.value.startsWith("_")
        }.map {
            case Func(typeId, params, stmts) => Func(typeId, params, clean(stmts))
        }

        def newMain = clean(main)

        Program(newFuncs, newMain)
    }

    def clean(statements: StatementList): StatementList = statements match {
        case Assignment(_, Call(Id(printName), args)) :: Assignment(_, Call(Id(PRINTLN), _)) :: rest
            if (printName.startsWith(PRINT)) => Println(args(0)) :: clean(rest)
        
        case Assignment(_, Call(Id(readName), args)) :: rest
            if (readName.startsWith(READ)) => Read(args(0)) :: clean(rest)

        case Assignment(_, Call(Id(EXIT), args)) :: rest => Exit(args(0)) :: clean(rest)

        case Assignment(_, Call(Id(errName), args)) :: rest
            if (errName.startsWith(ERR)) => clean(rest)

        case Assignment(_, Call(Id(FREE), args)) :: rest => Free(args(0)) :: clean(rest)

        case If(cond, thenStmts, elseStmts) :: rest => If(cond, clean(thenStmts), clean(elseStmts)) :: clean(rest)
        case While(cond, doStmts) :: rest => While(cond, clean(doStmts)) :: clean(rest)
        case Block(stmts) :: rest => Block(clean(stmts)) :: clean(rest)

        case instr :: rest => instr :: clean(rest)
        case Nil => Nil
    }
}
