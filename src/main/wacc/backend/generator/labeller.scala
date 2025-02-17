package wacc.backend.generator

import wacc.backend.ir.instructions.*

import LabelType.*

enum LabelType {
    case Main
    case Function(name: String)
    case Directive(name: String)
    case Widget(name: String)

    case If
    case While
    case AnyLabel
}

object labeller {
    private var ifCount    = 0
    private var whileCount = 0
    private var labelCount = 0

    // TODO: Improve labelling
    def nextLabel(labelType: LabelType): Label = labelType match {
        case Main => Label("main")
        case Function(name) => Label(s"wacc_$name")
        case Directive(name) => Label(s".$name")
        case Widget(name) => Label(s".L.$name")

        case If => Label(s".L_if_${ifCount += 1; ifCount}")
        case While => Label(s".L_while_${whileCount += 1; whileCount}")
        case AnyLabel => Label(s".L${labelCount += 1; labelCount}")
    }
}
