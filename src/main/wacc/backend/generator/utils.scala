package wacc.backend.generator

import scala.collection.mutable

import wacc.semantics.scoping.semanticTypes.*
import wacc.semantics.typing.*

import wacc.backend.ir.*
import flags.*
import instructions.*
import registers.*
import widgets.*

/**
 * Provides unique temporary registers for the first pass of code generation.
 */
class Temporary {
    private var number = 0

    /**
     * Creates a new temporary register with a unique identifier.
     * Optionally specifies the size of the register.
     */
    def next(size: RegSize = RegSize.QUAD_WORD): TempReg = {
        number += 1
        TempReg(number, size)
    }
}

/**
 * Tracks which runtime support functions (widgets) are used in the program.
 * Ensures that only the necessary widgets are included in the final assembly.
 */
class WidgetManager {
    private val activeWidgets: mutable.Set[Widget] = mutable.Set.empty

    /**
     * Adds a widget and all its dependencies to the active set.
     * This ensures that all required runtime functions are included.
     */
    def activate(widget: Widget): Unit = {
        activeWidgets += widget
        widget.dependencies.foreach { widget =>
            activeWidgets += widget
        }
    }

    /**
     * Returns the set of all widgets that have been activated.
     */
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
    def dependencies: Set[Widget] = widgets.usedWidgets ++ widgets.usedWidgets.flatMap(_.dependencies)

    final val registers: Array[Register] = Array(RDI(), RSI(), RDX(), RCX(), R8(), R9())
    val varRegs: mutable.Map[String, Register] = mutable.Map.empty

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

/**
 * Utility functions for working with semantic types and code generation.
 */
object utils {
    /**
     * Gets the element type of an array.
     */
    def getArrayType(semTy: SemType): SemType = semTy match {
        case KType.Array(elemType, _) => elemType
        case ty                       => ty
    }

    /**
     * Determines the appropriate register size for a semantic type.
     */
    def getTypeSize(semType: SemType): RegSize = semType match {
        case KType.Int  => RegSize.DOUBLE_WORD
        case KType.Bool => RegSize.BYTE
        case KType.Char => RegSize.BYTE
        case _          => RegSize.QUAD_WORD
    }

    /**
     * Gets the appropriate widget for loading array elements of a given size.
     */
    def getArrayElementLoadWidget(elemSize: RegSize): Widget = elemSize match {
        case RegSize.BYTE        => ArrayLoad1
        case RegSize.WORD        => ArrayLoad2
        case RegSize.DOUBLE_WORD => ArrayLoad4
        case RegSize.QUAD_WORD   => ArrayLoad8  
    }

    /**
     * Gets the appropriate widget for storing array elements of a given size.
     */
    def getArrayElementStoreWidget(elemSize: RegSize): Widget = elemSize match {
        case RegSize.BYTE        => ArrayStore1
        case RegSize.WORD        => ArrayStore2
        case RegSize.DOUBLE_WORD => ArrayStore4
        case RegSize.QUAD_WORD   => ArrayStore8 
    }

    /**
     * Gets the appropriate widget for printing elements of a given size.
     */
    def getPrintWidget(semTy: SemType): Widget = semTy match {
        case KType.Int                  => PrintInt
        case KType.Bool                 => PrintBool
        case KType.Char                 => PrintChar
        case KType.Str                  => PrintString
        case KType.Array(KType.Char, 1) => PrintString
        case _                          => PrintPointer
    }

    /**
     * Converts a comparison operator to the corresponding assembly flag.
     */
    def convertToJump(op: TyExpr.OpComp): CompFlag = op match {
        case TyExpr.OpComp.Equal        => CompFlag.E
        case TyExpr.OpComp.NotEqual     => CompFlag.NE
        case TyExpr.OpComp.GreaterThan  => CompFlag.G
        case TyExpr.OpComp.GreaterEqual => CompFlag.GE
        case TyExpr.OpComp.LessThan     => CompFlag.L
        case TyExpr.OpComp.LessEqual    => CompFlag.LE
    }

    def changeRegisterSize(reg: Register, size: RegSize): Register = reg match {
        case RAX(_) => RAX(size)
        case RBX(_) => RBX(size)
        case RCX(_) => RCX(size)
        case RDX(_) => RDX(size)

        // special purpose registers
        case RDI(_) => RDI(size)
        case RSI(_) => RSI(size)
        case RBP(_) => RBP(size)
        case RIP(_) => RIP(size)
        case RSP(_) => RSP(size)

        // extended registers (r8-r15)
        case R8 (_) => R8(size)
        case R9 (_) => R9(size)
        case R10(_) => R10(size)
        case R11(_) => R11(size)
        case R12(_) => R12(size)
        case R13(_) => R13(size)
        case R14(_) => R14(size)
        case R15(_) => R15(size)

        // temporary register
        case temp @ TempReg(num, _) => TempReg(num, size)
    }
}
