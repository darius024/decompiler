package wacc.backend.generator

import scala.collection.mutable

import wacc.semantics.scoping.semanticTypes.*
import wacc.semantics.typing.*

import wacc.backend.ir.*
import flags.*
import instructions.*
import registers.*
import widgets.*

/** Provides unique temporary registers for the first pass. */
class Temporary {
    private var number = 0

    def next(size: RegSize = RegSize.QUAD_WORD): TempReg = {
        number += 1
        TempReg(number, size)
    }
}

/** Keeps track of all the widgets that must be included in the final assembly file. */
class WidgetManager {
    private val activeWidgets: mutable.Set[Widget] = mutable.Set.empty

    /** Adds one widgets and its dependencies to the set. */
    def activate(widget: Widget): Unit = {
        activeWidgets += widget
        widget.dependencies.foreach { widget =>
            activeWidgets += widget
        }
    }

    /** Returns the used widgets of the program. */
    def usedWidgets: Set[Widget] = activeWidgets.toSet
}

/** Holds information thoughout the generation of the code.
  *
  * - instructions: list of IR instructions that is being build
  * - directives: all string literals that must be stored in the data segment
  * - labeller: formatted and unique names for the assembly
  * - temp: successive temporary registers
  * - widgets: handles all widgets that the instructions use
  */ 
class CodeGenerator(var instructions: mutable.Builder[Instruction, List[Instruction]],
                    directives: mutable.Builder[StrLabel, Set[StrLabel]],
                    labeller: Labeller,
                    temp: Temporary,
                    widgets: WidgetManager) {
    def ir: List[Instruction] = instructions.result()
    def data: Set[StrLabel] = directives.result()
    def dependencies: Set[Widget] = widgets.usedWidgets

    // parameter registers for function calls
    final val registers: Array[Register] = Array(RDI(), RSI(), RDX(), RCX(), R8(), R9())
    // tracks where variables are in the temporary registers
    private val varRegs: mutable.Map[String, Register] = mutable.Map.empty

    def addInstr(instruction: Instruction): Unit = {
        instructions += instruction
    }

    def addStrLabel(directive: StrLabel): Unit = {
        directives += directive
    }

    def nextLabel(labelType: LabelType): Label = {
        labeller.nextLabel(labelType)
    }

    def nextTemp(size: RegSize = RegSize.QUAD_WORD): TempReg = {
        temp.next(size)
    }

    def addVar(name: String, reg: Register): Register = {
        varRegs += name -> reg
        reg
    }

    def getVar(name: String): Register = {
        varRegs.getOrElse(name, addVar(name, nextTemp()))
    }
    
    def getWidgetLabel(widget: Widget): Label = {
        widgets.activate(widget)
        widget.label
    }
}

/** Utility functions for parsing typed AST nodes. */
object utils {
    /** Gets the base type of an array access. */
    def getArrayType(semTy: SemType): SemType = semTy match {
        case KType.Array(elemType, _) => elemType
        case ty                       => ty
    }

    /** Computes the size of a type in bytes. */
    def getTypeSize(semType: SemType): RegSize = semType match {
        case KType.Int  => RegSize.DOUBLE_WORD
        case KType.Bool => RegSize.BYTE
        case KType.Char => RegSize.BYTE
        case _          => RegSize.QUAD_WORD
    }

    /** Retrieves the right widget based on the size of the array elements. */
    def getArrayElementLoadWidget(elemSize: RegSize): Widget = elemSize match {
        case RegSize.BYTE        => ArrayLoad1
        case RegSize.WORD        => ArrayLoad2
        case RegSize.DOUBLE_WORD => ArrayLoad4
        case RegSize.QUAD_WORD   => ArrayLoad8  
    }

    /** Retrieves the right widget based on the size of the array elements. */
    def getArrayElementStoreWidget(elemSize: RegSize): Widget = elemSize match {
        case RegSize.BYTE        => ArrayStore1
        case RegSize.WORD        => ArrayStore2
        case RegSize.DOUBLE_WORD => ArrayStore4
        case RegSize.QUAD_WORD   => ArrayStore8 
    }

    /** Converts an AST operation to the corresponding assembly flag. */
    def convertToJump(op: TyExpr.OpComp): CompFlag = op match {
        case TyExpr.OpComp.Equal        => CompFlag.E
        case TyExpr.OpComp.NotEqual     => CompFlag.NE
        case TyExpr.OpComp.GreaterThan  => CompFlag.G
        case TyExpr.OpComp.GreaterEqual => CompFlag.GE
        case TyExpr.OpComp.LessThan     => CompFlag.L
        case TyExpr.OpComp.LessEqual    => CompFlag.LE
    }
}
