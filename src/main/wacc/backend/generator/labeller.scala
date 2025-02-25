package wacc.backend.generator

import wacc.backend.ir.instructions.*

import LabelType.*

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

class Labeller {
    private var ifCount        = 0
    private var endIfCount     = 0
    private var whileBodyCount = 0
    private var whileCondCount = 0
    private var labelCount     = 0
    private var strCount       = 0

    // TODO: Improve labelling
    def nextLabel(labelType: LabelType): Label = labelType match {
        case Main           => Label("main")
        case Function(name) => Label(s"wacc_$name")
        case Str            => Label(s".L.str${strCount += 1; strCount}")
        case Widget(name)   => Label(s".L.$name")

        case If        => Label(s".L_if_${ifCount += 1; ifCount}")
        case IfEnd     => Label(s".L_end_if_${endIfCount += 1; endIfCount}")
        case WhileBody => Label(s".L_while_body_${whileBodyCount += 1; whileBodyCount}")
        case WhileCond => Label(s".L_while_cond_${whileCondCount += 1; whileCondCount}")
        case AnyLabel  => Label(s".L${labelCount += 1; labelCount}")
    }
}
