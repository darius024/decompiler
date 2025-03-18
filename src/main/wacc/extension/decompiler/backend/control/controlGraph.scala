package wacc.extension.decompiler

import wacc.backend.ir.*
import instructions.*
import memory.*
import registers.*

/** Main control flow data structures. */
object controlGraph {
    /** Local block within a function block that contains the instructions used.
      * 
      * The `next` field holds the valid next labels reachable from this block.
      */
    case class Block(label: Label,
                     instrs: List[Instruction] = List.empty,
                     next: List[Label] = Nil)

    /** Function block that holds a map of all local blocks. */
    case class FuncBlock(funcLabel: Label,
                         blocks: Map[Label, Block]) {
        // keep track of the number of parameters of the function
        var numParameter = 0
    }

    /** Cleans blocks by removing calling conventions related instructions.
      *
      * These represent saving registers on stack, setting the frame pointer,
      * setting the return code in the main function, pushing and popping registers.
      */
    def removeCallingConventions(block: Block, funcLabel: Label): Block = {
        val Block(label, instructions, next) = block

        var newInstructions = instructions
        if (block.instrs.length > 0 && block.instrs(0) == Push(RBP())) {
            newInstructions = newInstructions.dropWhile(isSavingRegisters(_)).dropWhile(isPrologue(_))
        }
        newInstructions = newInstructions.reverse.dropWhile(isEpilogue(_, funcLabel.name == "main")).reverse
        
        Block(label, newInstructions, next)
    }

    /** Removes certain patterns at the beginning of a block. */
    def isPrologue(instruction: Instruction): Boolean = instruction match {
        case Sub(RSP(_), _)                  => true
        case Mov(MemAccess(RSP(_), _, _), _) => true
        case Mov(RBP(_), RSP(_))             => true
        case And(RSP(_), _)                  => true
        case _                               => false
    }

    /** Removes certain patterns at the beginning of a block. */
    def isSavingRegisters(instruction: Instruction): Boolean = instruction match {
        case Push(_)                         => true
        case _                               => false
    }

    /** Removes certain patterns at the end of a block. */
    def isEpilogue(instruction: Instruction, isMain: Boolean): Boolean = instruction match {
        case Ret                             => true
        case Pop(_)                          => true
        case Add(RSP(_), _)                  => true
        case Mov(RSP(_), RBP(_))             => true
        case Mov(_, MemAccess(RSP(_), _, _)) => true
        case Mov(RAX(_), _)                  => isMain
        case _                               => false
    }
}
