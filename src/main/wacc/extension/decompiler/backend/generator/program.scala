package wacc.extension.decompiler

import representation.*

/** Interface for programming languages to be decompiled to. */
trait ProgrammingLanguage {
    def libraries: List[String]

    def fileExtension: String

    def functionBegin: String
    def functionEnd: String

    def programBegin: String
    def programEnd: String

    def blockBegin: String
    def blockEnd: String

    // types
    def intType: String
    def boolType: String
    def charType: String
    def stringType: String
    def arrType(ty: String, dimension: Int): String
    def pairType(fst: String, snd: String): String

    def boolLiteral(value: Boolean): String
    def pairLiteral: String

    def arrayElement(id: String, indices: List[String]): String
    def arrayLit(exprs: List[String]): String
    def newPair(fst: String, snd: String): String
    def call(func: String, args: List[String]): String

    def fst(pair: String): String
    def snd(pair: String): String
    def pairAccessStart: String
    def pairAccessEnd: String

    def read: String
    def print: String
    def println: String
    def free: String
    def ret: String
    def exit: String

    def thenStart: String
    def thenEnd: String
    def elseStart: String
    def elseEnd: String

    def whileStart: String
    def whileEnd: String

    def skip: String

    def condStart: String
    def condEnd: String

    def betweenStatements: String
    def afterStatements: String

    def clean(instrs: Program): Program
}
