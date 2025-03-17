package wacc.extension.decompiler

import wacc.backend.ir.registers.*
import VarType.*

/**
  * Defines the different types of variables used in the code.
  */
enum VarType {
    case ArrayElem
    case Parameter(reg: Register, name: String)
    case Variable
}

/**
  * Generates unique names for variables in the program.
  */
class Namer {
    // inital value of the counter
    private final val INITIAL = -1
    
    // counters for different variable types
    private var arrayElemCount = INITIAL
    private var variableCount  = INITIAL

    /**
      * Creates a new variable of the specified type with a unique name.
      */
    def nextVariable(variableType: VarType): String = variableType match {
        case ArrayElem      => s"arr_${arrayElemCount += 1; arrayElemCount}"
        case Parameter(reg, name) => s"${name}_${reg match {
            case RDI(_) => "first"
            case RSI(_) => "second"
            case RDX(_) => "third"
            case RCX(_) => "forth"
            case R8(_)  => "fifth"
            case R9(_)  => "sixth"
            case _      => "NOT_PARAM"
        }}"
        case Variable       => s"x${variableCount += 1; variableCount}"
    }
}

