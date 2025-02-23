package wacc.backend.generator

import scala.collection.mutable

import wacc.backend.ir.*
import instructions.*
import registers.*
import widgets.*

import wacc.semantics.typing.*
import TyExpr.*
import TyStmt.*

class WidgetManager {
    private val activeWidgets: mutable.Set[Widget] = mutable.Set.empty

    def activate(widget: Widget): Unit = {
        activeWidgets += widget
    }

    def usedWidgets: Set[Widget] = activeWidgets.toSet
}

class CodeGenerator(instructions: mutable.Builder[Instruction, List[Instruction]],
                    directives: mutable.Builder[Directive, Set[Directive]],
                    labeller: Labeller,
                    temp: Temporary,
                    widgets: WidgetManager) {
    def ir: List[Instruction] = instructions.result()
    def data: Set[Directive] = directives.result()
    def dependencies: Set[Widget] = widgets.usedWidgets

    def addInstr(instruction: Instruction): Unit = {
        instructions += instruction
    }

    def addDirective(directive: Directive): Unit = {
        directives += directive
    }

    def nextLabel(labelType: LabelType): Label = {
        labeller.nextLabel(labelType)
    }

    def nextTemp(size: Int = QUAD_WORD): TempReg = {
        temp.next(size)
    }

    def getWidgetLabel(widget: Widget): Label = {
        widgets.activate(widget)
        widget.label
    }
}

def generate(prog: TyProg): CodeGenerator = {
    given codeGen: CodeGenerator =
        CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)

    codeGen
}

def generate(funcName: String, func: TyFunc)
            (using codeGen: CodeGenerator): Unit = ???

def generate(stmt: TyStmt)
            (using codeGen: CodeGenerator): Unit = ???

def generate(expr: TyExpr)
            (using codeGen: CodeGenerator): TempReg = ???

def generateCond(expr: TyExpr, label: Label)
                (using codeGen: CodeGenerator): Unit = ???

def generateDivMod(expr: TyExpr)
                  (using codeGen: CodeGenerator): TempReg = ???

def generateFstSnd(pairElem: TyPairElem)
                  (using codeGen: CodeGenerator): TempReg = ???

def generateArrayElem(arrayElem: ArrayElem)
                     (using codeGen: CodeGenerator): TempReg = ???
