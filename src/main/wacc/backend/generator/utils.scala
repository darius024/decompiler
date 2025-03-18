package wacc.backend.generator

import scala.collection.mutable

import wacc.semantics.scoping.semanticTypes.*
import wacc.semantics.typing.*

import wacc.backend.ir.*
import constants.*
import flags.*
import instructions.*
import memory.*
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
class CodeGenerator(instructions: mutable.Builder[Instruction, List[Instruction]],
                    directives: mutable.Builder[StrLabel, Set[StrLabel]],
                    labeller: Labeller,
                    temp: Temporary,
                    widgets: WidgetManager) {
    private var instrNumber = 0
    def ir: List[Instruction] = instructions.result()
    var instrs: List[Instruction] = ir
    
    def data: Set[StrLabel] = directives.result()
    def dependencies: Set[Widget] = widgets.usedWidgets ++ widgets.usedWidgets.flatMap(_.dependencies)

    final val registers: Array[Register] = Array(ARG1(), ARG2(), ARG3(), ARG4(), ARG5(), ARG6())
    final val numRegisters: mutable.Map[Label, Int] = mutable.Map.empty
    val varRegs: mutable.Map[String, RegMem] = mutable.Map.empty
    val varLive: mutable.Map[RegMem, (Int, Int)] = mutable.Map.empty
    var currLabel: Label = Label("main")
    var inMain: Boolean = false

    /**
      * Appends one instruction to the intermediate representation.
      */ 
    def addInstr(instruction: Instruction): Int = {
        instruction.number = instrNumber
        instrNumber += 1
        instructions += instruction

        instrNumber - 1
    }

    /**
      * Updates the uses and defs set of the instruction.
      */  
    def updateUsesDefs(instr: Instruction): Unit = instr match {
        case mov: Mov => 
            // If destination is a register, it's defined
            // If destination is memory, the base/index registers are used
            mov.dest match {
                case regDest: Register => mov.addDef(regDest)
                case memDest: MemoryAccess => mov.addUse(memDest)  // This will extract any temp regs from the memory address
            }
    
            mov.addUse(mov.src)

        case div: Div  => 
            div.addUse(div.src)

        case mod: Mod  => 
            mod.addUse(mod.src)

        case cmp: Cmp => 
            cmp.addUse(cmp.dest)
            cmp.addUse(cmp.src) 
        
        case test: Test => 
            test.addUse(test.dest)
            test.addUse(test.src)

        // ADD Sub, AND, OR
        case binaryInstr: BinaryInstr => 
            binaryInstr.addDef(binaryInstr.dest)
            binaryInstr.addUse(binaryInstr.dest)
            binaryInstr.addUse(binaryInstr.src)
        
        case setComp: SetComp => 
            setComp.addDef(setComp.dest)
        
        case mul: Mul =>
            mul.addDef(mul.dest)
            mul.addUse(mul.src)
            // In x86, MUL might implicitly use RAX or other registers

        case mulImm: MulImm =>
            mulImm.addDef(mulImm.dest)
            mulImm.addUse(mulImm.src)
            // Immediate is already handled

        case lea: Lea =>
            lea.addDef(lea.dest)
            lea.addUse(lea.addr) 
        
        case cMov: CMov => 
            cMov.addDef(cMov.dest)
            cMov.addUse(cMov.src)

        case pop: Pop => 
            pop.addDef(pop.reg)
            
        case push: Push => 
            push.addUse(push.reg)
        
        case _ => // Other instructions may not need to update uses/defs
    }

    /**
      * Appends one string directive to the intermediate representation.
      */
    def addStrLabel(directive: StrLabel): Unit = {
        directives += directive
    }

    /**
      * Provides a next unique label.
      */
    def nextLabel(labelType: LabelType): Label = {
        labeller.nextLabel(labelType)
    }

    /**
      * Provides a next unique temporary register.
      */
    def nextTemp(size: RegSize = RegSize.QUAD_WORD): TempReg = {
        temp.next(size)
    }

    /**
      * Counts the stack size of each function scope.
      */
    def enterScope(label: Label): Unit = {
        numRegisters(label) = 1
        currLabel = label
    }

    /**
      * Map a variable to its location, either in a temporary or known register or memory location.
      */
    def addVar(name: String, regMem: RegMem, param: Boolean = false): RegMem = {
        varRegs += name -> regMem
        varLive += regMem -> (instrNumber, instrNumber)
        if (!param) {
            numRegisters(currLabel) = numRegisters(currLabel) + 1
        }
        regMem
    }

    /**
      * Returns the location of a variable, if it is known.
      * Otherwise, it adds the variable to the map for later accesses.
      */
    def getVar(name: String, size: RegSize = RegSize.QUAD_WORD): Register = {
        val regMem = if (varRegs.contains(name)) {
            val reg = varRegs(name)
            val initialNumber = varLive(reg)._1
            varLive(reg) = (initialNumber, instrNumber)
            reg
        } else {
            addVar(name, nextTemp(size))
        }
        
        regMem match {
            case memAccess: MemoryAccess => 
                val temp = nextTemp(memAccess.size)
                addInstr(Mov(temp, memAccess))
                temp
            case reg: Register           => reg
        }
    }
    
    /**
      * Activates and returns the label of the widget.
      */ 
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
        case KType.Array(elemType, 1)     => elemType
        case KType.Array(elemType, arity) => KType.Array(elemType, arity - 1)
        case ty                           => ty
    }

    /**
      * Determines the appropriate register size for a semantic type.
      */
    def getTypeSize(semType: SemType): RegSize = semType match {
        case KType.Bool => RegSize.BYTE
        case KType.Char => RegSize.BYTE
        case KType.Int  => RegSize.DOUBLE_WORD
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

    /**
      * Changes the size of a register immutably.
      */ 
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
        case TempReg(num, _) => TempReg(num, size)
    }

    /**
      * Computes the weight of an expression.
      */ 
    def computeSize(expr: TyExpr): Int = expr match {
        // expressions
        case TyExpr.BinaryComp(lhs, rhs, _)       => Seq(computeSize(lhs), computeSize(rhs)).max + 1
        case TyExpr.BinaryBool(lhs, rhs, _)       => Seq(computeSize(lhs), computeSize(rhs)).max + 1
        case TyExpr.BinaryArithmetic(lhs, rhs, _) => Seq(computeSize(lhs), computeSize(rhs)).max + 1
        case TyExpr.Not(expr) => computeSize(expr) + 1
        case TyExpr.Neg(expr) => computeSize(expr) + 1
        case TyExpr.Len(expr) => computeSize(expr) + 1
        case TyExpr.Ord(expr) => computeSize(expr) + 1
        case TyExpr.Chr(expr) => computeSize(expr) + 1
        
        // array and pair management
        case TyExpr.ArrayElem(id, idx, semTy)       => ARR_PAIR_WGHT
        case TyExpr.PairFst(lval, semTy)            => ARR_PAIR_WGHT
        case TyExpr.PairSnd(lval, semTy)            => ARR_PAIR_WGHT
        case TyExpr.ArrayLit(exprs, semTy)          => ARR_PAIR_WGHT
        case TyExpr.NewPair(fst, snd, fstTy, sndTy) => ARR_PAIR_WGHT

        // function call
        case TyExpr.Call(func, args, retTy, argTys) => args.length + 1

        // literals
        case _ => 1
    }
}
