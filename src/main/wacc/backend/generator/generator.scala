package wacc.backend.generator

import scala.collection.mutable

import wacc.backend.ir.*
import instructions.*
import widgets.*

import wacc.syntax.*
import exprs.*
import prog.*
import stmts.*

class CodeGenerator(instructions: mutable.Builder[Instruction, List[Instruction]],
                    directives: mutable.Builder[Directive, List[Directive]],
                    widgets: mutable.Builder[Widget, List[Widget]]) {
    def ir: List[Instruction] = instructions.result()
    def data: List[Directive] = directives.result()
    def dependencies: List[Widget] = widgets.result()

    def addInstr(instruction: Instruction): Unit = {
        instructions += instruction
    }

    def addDirective(directive: Directive): Unit = {
        directives += directive
    }

    def addWidget(widget: Widget): Unit = {
        widgets += widget
    }
}

def generate(prog: Program): CodeGenerator = {
    given codeGen: CodeGenerator = CodeGenerator(List.newBuilder, List.newBuilder, List.newBuilder)

    codeGen
}

def generate(func: Function)
            (using codeGen: CodeGenerator): Unit = ???

def generate(stmts: StmtList)
            (using CodeGenerator): Unit = ???

def generate(stmt: Stmt)
            (using codeGen: CodeGenerator): Unit = ???

def generate(value: LValue | RValue)
            (using codeGen: CodeGenerator): Unit = ???


def generateExpr(expr: Expr)
                (using codeGen: CodeGenerator): Unit = ???

def generateArrayElem(arrayElem: ArrayElem)
                     (using codeGen: CodeGenerator): Unit = ???

def generateDivMod(expr: Expr)
                  (using codeGen: CodeGenerator): Unit = ???

def generateFstSnd(pairElem: PairElem)
                  (using codeGen: CodeGenerator): Unit = ???
