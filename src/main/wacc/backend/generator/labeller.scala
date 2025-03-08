package wacc.backend.generator

import wacc.backend.ir.instructions.*

import LabelType.*

/**
 * Defines the different types of labels used in the assembly code.
 */
enum LabelType {
    case Main
    case Function(name: String)
    case Str
    case Widget(name: String)

    case If
    case IfEnd
    case WhileBody
    case WhileCond
    case AnyLabel
}

/**
  * Generates unique labels for different parts of the program.
  */
class Labeller {
    // inital value of the counter
    private final val INITIAL = -1
    
    // counters for different label types
    private var ifCount        = INITIAL
    private var endIfCount     = INITIAL
    private var whileBodyCount = INITIAL
    private var whileCondCount = INITIAL
    private var labelCount     = INITIAL
    private var strCount       = INITIAL

    /**
      * Creates a new label of the specified type with a unique name.
      */
    def nextLabel(labelType: LabelType): Label = labelType match {
        // fixed labels
        case Main           => Label("main")
        case Function(name) => Label(s"wacc_$name")
        case Str            => Label(s".L.str${strCount += 1; strCount}")
        case Widget(name)   => Label(s"$name")

        // control flow labels with unique counters
        case If             => Label(s".L_if_${ifCount += 1; ifCount}")
        case IfEnd          => Label(s".L_end_if_${endIfCount += 1; endIfCount}")
        case WhileBody      => Label(s".L_while_body_${whileBodyCount += 1; whileBodyCount}")
        case WhileCond      => Label(s".L_while_cond_${whileCondCount += 1; whileCondCount}")
        case AnyLabel       => Label(s".L${labelCount += 1; labelCount}")
    }
}
