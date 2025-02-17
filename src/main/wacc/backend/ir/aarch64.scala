package wacc.backend.ir

import immediate.*
import registers.*

/** Calling Conventions:
  * 
  * x0-x7:   argument registers        --- caller-saved
  * x9-x15:  general-purpose registers --- caller-saved
  * x19-x28: general-purpose registers --- callee-saved
  * x29:     frame pointer             --- special
  * x30:     link register             --- special
  * x31:     stack pointer             --- special
  */

object registers {
    sealed trait Register { val name: String }

    sealed trait XRegister extends Register
    sealed trait WRegister extends Register
    case object TempRegister extends Register { val name = "temp" }

    case object X0  extends XRegister { val name = "x0"  } 
    case object X1  extends XRegister { val name = "x1"  }
    case object X2  extends XRegister { val name = "x2"  }
    case object X3  extends XRegister { val name = "x3"  }
    case object X4  extends XRegister { val name = "x4"  }
    case object X5  extends XRegister { val name = "x5"  }
    case object X6  extends XRegister { val name = "x6"  }
    case object X7  extends XRegister { val name = "x7"  }
    case object X8  extends XRegister { val name = "x8"  }
    case object X9  extends XRegister { val name = "x9"  }
    case object X10 extends XRegister { val name = "x10" }
    case object X11 extends XRegister { val name = "x11" }
    case object X12 extends XRegister { val name = "x12" }
    case object X13 extends XRegister { val name = "x13" }
    case object X14 extends XRegister { val name = "x14" }
    case object X15 extends XRegister { val name = "x15" }
    case object X16 extends XRegister { val name = "x16" }
    case object X17 extends XRegister { val name = "x17" }
    case object X18 extends XRegister { val name = "x18" }
    case object X19 extends XRegister { val name = "x19" }
    case object X20 extends XRegister { val name = "x20" }
    case object X21 extends XRegister { val name = "x21" }
    case object X22 extends XRegister { val name = "x22" }
    case object X23 extends XRegister { val name = "x23" }
    case object X24 extends XRegister { val name = "x24" }
    case object X25 extends XRegister { val name = "x25" }
    case object X26 extends XRegister { val name = "x26" }
    case object X27 extends XRegister { val name = "x27" }
    case object X28 extends XRegister { val name = "x28" }

    case object XZR extends XRegister { val name = "xzr" }
    case object FP  extends XRegister { val name = "fp"  }
    case object LR  extends XRegister { val name = "lr"  }
    case object SP  extends XRegister { val name = "sp"  }

    case object W0  extends WRegister { val name = "w0"  }
    case object W1  extends WRegister { val name = "w1"  }
    case object W2  extends WRegister { val name = "w2"  }
    case object W3  extends WRegister { val name = "w3"  }
    case object W4  extends WRegister { val name = "w4"  }
    case object W5  extends WRegister { val name = "w5"  }
    case object W6  extends WRegister { val name = "w6"  }
    case object W7  extends WRegister { val name = "w7"  }
    case object W8  extends WRegister { val name = "w8"  }
    case object W9  extends WRegister { val name = "w9"  }
    case object W10 extends WRegister { val name = "w10" }
    case object W11 extends WRegister { val name = "w11" }
    case object W12 extends WRegister { val name = "w12" }
    case object W13 extends WRegister { val name = "w13" }
    case object W14 extends WRegister { val name = "w14" }
    case object W15 extends WRegister { val name = "w15" }
    case object W16 extends WRegister { val name = "w16" }
    case object W17 extends WRegister { val name = "w17" }
    case object W18 extends WRegister { val name = "w18" }
    case object W19 extends WRegister { val name = "w19" }
    case object W20 extends WRegister { val name = "w20" }
    case object W21 extends WRegister { val name = "w21" }
    case object W22 extends WRegister { val name = "w22" }
    case object W23 extends WRegister { val name = "w23" }
    case object W24 extends WRegister { val name = "w24" }
    case object W25 extends WRegister { val name = "w25" }
    case object W26 extends WRegister { val name = "w26" }
    case object W27 extends WRegister { val name = "w27" }
    case object W28 extends WRegister { val name = "w28" }

    case object WZR extends WRegister { val name = "wzr" }
}

object immediate {
    sealed trait Immediate

    // TODO: Add more types of immediates and safety-check the parameters
    case class Imm(value: Int) extends Immediate
}

object instructions {
    type RegImm = Register | Immediate

    sealed trait Instruction

    // TODO: Complete the set of instructions

    // labels and directives
    case class Label(text: String) extends Instruction
    case class Dir(dir: Directive) extends Instruction

    // stack
    case class PushStack(reg1: Register, reg2: Register = XZR) extends Instruction
    case class PopStack(reg1: Register, reg2: Register = XZR) extends Instruction
    
    // operations
    case class Add(dest: Register, src1: Register, src2: RegImm) extends Instruction
    case class Sub(dest: Register, src1: Register, src2: RegImm) extends Instruction
    case class Mul(dest: Register, src1: Register, src2: RegImm) extends Instruction
    case class Mod(dest: Register, src1: Register, src2: RegImm) extends Instruction
    case class Div(dest: Register, src1: Register, src2: RegImm) extends Instruction
    case class And(dest: Register, src1: Register, src2: RegImm) extends Instruction
    case class Or(dest: Register, src1: Register, src2: RegImm) extends Instruction

    case class Neg(dest: Register, src1: RegImm) extends Instruction
    case class Not(dest: Register, src1: RegImm) extends Instruction

    // memory
    case class Mov(dest: Register, src: RegImm) extends Instruction
    case class Load(dest: Register, addr: RegImm) extends Instruction
    case class Store(dest: Register, addr: RegImm) extends Instruction

    // control flow
    case class Branch(label: Label) extends Instruction
    case object BranchError extends Instruction
    case object Return extends Instruction
}
