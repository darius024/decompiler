package wacc.backend.ir

import instructions.*

enum Directive {
    case Data
    case Text
    case Align(size: Int)
    case Global(name: String)
    case Str(name: String, value: String)
}

object asciz {
    final val integer   = "%d"
    final val character = " %c"
    final val string    = "%.*s"
    final val endl      = ""
}
