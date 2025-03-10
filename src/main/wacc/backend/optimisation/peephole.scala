package wacc.backend.optimisation

import wacc.backend.generator.utils.*
import wacc.backend.ir.*
import immediate.*
import instructions.*
import registers.*

/** Perform peephole optimisation by removing redundant instructions and combining compatible instructions. */
def peephole(instructions: List[Instruction], before: List[Instruction] = Nil): List[Instruction] = instructions match {
    // remove unnecessary stack usage
    case Push(reg1) :: Pop(reg2) :: rest
        if (matchRegister(reg1, reg2)) => peepholeBacktrack(rest, before)
    // case Pop(reg1) :: Push(reg2) :: rest
    //     if (matchRegister(reg1, reg2)) => peephole(rest, before)

    // remove redundant moves
    case Mov(reg1, reg2)         :: rest
        if (matchRegister(reg1, reg2, true)) => peephole(rest, before)
    // reduce indirect moves between registers and memory accesses
    case Mov(reg1, reg2) :: Mov(reg3: Register, reg4) :: rest
        if (matchRegister(reg1, reg4) && reg2.size == reg3.size && !matchRegister(reg3, reg4)) =>
            peephole(rest, before ::: List(Mov(reg3, reg2)))
    
    // reduce instruction based on the use of immediate values
    case Mov(reg1: Register, imm: Immediate) :: (binInstr: BinaryInstr) :: rest
        if (matchRegister(reg1, binInstr.src) && (matchRegister(reg1, RAX()) || matchRegister(reg1, RBX()))) =>
            peephole(transformBinaryInstr(binInstr, binInstr.dest, imm) :: rest, before)

    // reduce expressions' use of the stack
    case Push(reg1) :: Push(reg2) :: Pop(reg3) :: Pop(reg4) :: rest =>
        peephole(Mov(reg4, reg1) :: Mov(reg3, reg2) :: rest, before)
    case Mov(reg3, reg1: Register) :: Mov(reg4, reg2: RegImm) :: (binInstr: BinaryInstr) :: rest
        if (matchRegister(binInstr.dest, reg3, true) && matchRegister(binInstr.src, reg4, true)) =>
            peephole(transformBinaryInstr(binInstr, reg1, reg2) :: rest, before)
    case Mov(reg4, reg2: RegImm) :: Mov(reg3, reg1: Register) :: (binInstr: BinaryInstr) :: rest
        if (matchRegister(binInstr.dest, reg3, true) && matchRegister(binInstr.src, reg4, true)) =>
            peephole(transformBinaryInstr(binInstr, reg1, reg2) :: rest, before)

    // remove unnecessary jumps
    case Jump(label1, _) :: (label2 @ Label(name)) :: rest
        if (label1 == label2) => peephole(rest, before)

    // remove redundant instructions
    case Add(reg, Imm(memoryOffsets.NO_OFFSET)) :: rest => peephole(rest, before)
    case Sub(reg, Imm(memoryOffsets.NO_OFFSET)) :: rest => peephole(rest, before)
    
    case instr                   :: rest                   => peephole(rest, before :+ instr)
    case Nil                                               => before
}

/** Backtracks one step in the IR to be able to apply optimisations cumulatively. */
def peepholeBacktrack(instructions: List[Instruction], before: List[Instruction]): List[Instruction] = before match {
    case last :: Nil  => peephole(last :: instructions)
    case rest :+ last => peephole(last :: instructions, rest)
    case last         => peephole(instructions, last)
}

/** Checks if the two registers refer to the same one. */
def matchRegister(reg1: RegImmMem, reg2: RegImmMem, equal: Boolean = false): Boolean = (reg1, reg2) match {
    case (reg1: Register, reg2: Register) => changeRegisterSize(reg1, reg2.size) == reg2 && (!equal || reg1.size == reg2.size)
    case _                                => false
}

/** Transform a binary instruction to its more specific format. */
def transformBinaryInstr(binInstr: BinaryInstr, reg1: Register, reg2: RegImm): BinaryInstr = binInstr match {
    case _: Add  => Add (reg1, reg2)
    case _: Sub  => Sub (reg1, reg2)
    case _: And  => And (reg1, reg2)
    case _: Or   => Or  (reg1, reg2)
    case _: Cmp  => Cmp (reg1, reg2)
    case _: Test => Test(reg1, reg2)
}
