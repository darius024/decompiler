package wacc.backend.optimisation

import wacc.backend.ir.*
import instructions.*
import registers.*

def peephole(instructions: List[Instruction], before: List[Instruction] = Nil): List[Instruction] = instructions match {
    case Push(reg1) :: Pop(reg2) :: rest if (reg1 == reg2) => peepholeBacktrack(rest, before)
    // case Pop(reg1) :: Push(reg2) :: rest if (reg1 == reg2) => peepholeBacktrack(rest, before)
    case Mov(reg1, reg2)         :: rest if (reg1 == reg2) => peephole(rest, before)
    case instr                   :: rest                   => peephole(rest, before :+ instr)
    case Nil                                               => before
}

def peepholeBacktrack(instructions: List[Instruction], before: List[Instruction]): List[Instruction] = before match {
    case last :: Nil  => peephole(last :: instructions)
    case rest :+ last => peephole(last :: instructions, rest)
    case last         => peephole(instructions, last)
}
