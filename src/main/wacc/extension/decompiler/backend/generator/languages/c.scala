package wacc.extension.decompiler

import representation.*

/** WACC language specification. */
object CLanguage extends ProgrammingLanguage {
    def libraries: List[String] = List("#include <stdio.h>", "#include <stdlib.h>")
    def fileExtension: String = "c"

    def functionBegin: String = "{"
    def functionEnd: String = "}"

    def programBegin: String = ""
    def programEnd: String = ""

    def blockBegin: String = "{"
    def blockEnd: String = "}"

    // types
    def intType: String = "int"
    def boolType: String = "bool"
    def charType: String = "char"
    def stringType: String = "string"
    def arrType(ty: String, dimension: Int): String = s"$ty${"[]" * dimension}"
    def pairType(fst: String, snd: String): String = s"struct { $fst; $snd; }"

    def boolLiteral(value: Boolean): String = if (value) "true" else "false"
    def pairLiteral: String = "nullptr"

    def arrayElement(id: String, indices: List[String]): String = s"$id${indices.map(i => s"[$i]")}"
    def arrayLit(exprs: List[String]): String = s"{${exprs.mkString(", ")}}"
    def newPair(fst: String, snd: String): String = s"{$fst, $snd}"
    def call(func: String, args: List[String]): String = s"$func(${args.mkString(", ")})"

    def fst(pair: String): String = s"${pair}.fst"
    def snd(pair: String): String = s"${pair}.snd"
    def pairAccessStart: String = ""
    def pairAccessEnd: String = ""

    // TODO: correct these
    def read: String = "read"
    def print: String = "print"
    def println: String = "println"
    def free: String = "free"
    def ret: String = "return"
    def exit: String = "exit"

    def thenStart: String = "{"
    def thenEnd: String = "}"
    def elseStart: String = "else {"
    def elseEnd: String = "}"

    def whileStart: String = "{"
    def whileEnd: String = "}"

    def skip: String = ""

    def condStart: String = "("
    def condEnd: String = ")"

    def betweenStatements: String = ";"
    def afterStatements: String = ";"

    def clean(program: Program): Program = program
}
