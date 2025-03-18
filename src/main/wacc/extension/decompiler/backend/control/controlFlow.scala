package wacc.extension.decompiler

import scala.collection.mutable

import wacc.backend.ir.*
import flags.*
import instructions.*

import controlGraph.*

/** Keeps track of all the blocks of assembly code.
  * 
  * Constructs the control flow of the program by storing information
  * of the scope of instructions and analysing the types of jumps.
  */
class ControlFlow(blocks: mutable.Builder[(Label, Block), List[(Label, Block)]],
                  directives: mutable.Builder[StrLabel, Set[StrLabel]]) {
    // information about label blocks
    private var currentLabel = Label("main")
    private var currentFuncLabel = Label("main")
    private var currentBlock = mutable.ListBuffer.empty[Instruction]
    private var currentNext = mutable.ListBuffer.empty[Label]
    private var labelCount = 0

    // information about instructions that change the control flow
    var previousWasJump = false
    var unconditional = false

    // information of the utility of blocks
    var valid = true
    var funcValid = true

    def programBlocks: List[(Label, Block)] = blocks.result()
    def programDirectives: Set[StrLabel] = directives.result()

    // insert an instruction to the current block
    def addInstr(instruction: Instruction): Unit = {
        currentBlock += instruction
    }

    // record the directive in the set
    def addStrLabel(directive: StrLabel): Unit = {
        directives += directive
    }

    // link the current block to the next one
    def addNextBlockLabel(label: Label): Unit = {
        currentNext += label
    }

    // begin a new function block scope
    def startNewFunction(label: Label): Unit = {
        currentFuncLabel = label
        funcValid = true
    }

    // begin a local block of assembly instructions
    def startNewBlock(label: Label): Unit = {
        currentLabel = label
        currentBlock = mutable.ListBuffer.empty[Instruction]
        currentNext = mutable.ListBuffer.empty[Label]
        valid = true
    }

    // end the current block and add it to the map of blocks
    def endBlock(funcEnd: Boolean = false): Unit = {
        if (valid && funcValid) {
            val block = Block(currentLabel, currentBlock.toList, currentNext.toList)
            blocks += currentFuncLabel -> removeCallingConventions(block, currentFuncLabel)
        }
        // do not repeat blocks if returns happend in the middle of blocks
        valid = false
        // perform dead code elimination by not continuing to analyse assembly code after a return
        funcValid = !(funcEnd && (currentLabel == currentFuncLabel))
    }

    // provide a next artificial label, used in delimiting jump points
    def nextLabel: Label = {
        labelCount += 1
        Label(s"./$labelCount")
    }
}

/** First Pass of the decompiler.
  * 
  * Perform control flow analysis on the AST of the decompiler.
  */
def controlFlow(instructions: List[Instruction]): ControlFlow = {
    given controller: ControlFlow = ControlFlow(List.newBuilder, Set.newBuilder)

    instructions.foreach(controlFlow)
    controller.endBlock(true)

    controller
}

/** Analyses the instructions that change the control flow of the execution. */
def controlFlow(instruction: Instruction)
               (using controller: ControlFlow): Unit = instruction match {
    case strDirective: StrLabel =>
        controller.addStrLabel(strDirective)

    case label: Label =>
        // if the previous instruction was a jump, add it to the control flow graph
        if (controller.previousWasJump || label.name.startsWith(".")) {
            // if the jump is unconditional, reaching this label is not possible
            if (!controller.unconditional) {
                controller.addNextBlockLabel(label)
            }
            controller.previousWasJump = false
            controller.unconditional = false
        }

        // end current block, as a new one begins
        controller.endBlock()

        // start a new block in the control flow graph
        if (label.name == "main" || !label.name.startsWith(".")) {
            controller.startNewFunction(label)
        }
        controller.startNewBlock(label)

    case instr @ Jump(label, JumpFlag.Unconditional) =>
        controller.addInstr(instr)
        controller.addNextBlockLabel(label)
        // if the jump is unconditional, it is impossible to reach the next block
        controller.previousWasJump = true
        controller.unconditional = true
    
    case instr @ JumpComp(label, compFlag) if (!label.name.startsWith("_err"))=>
        controller.addInstr(instr)
        controller.addNextBlockLabel(label)
        controller.previousWasJump = true

    case instr @ Ret =>
        controller.addInstr(instr)
        controller.endBlock(true)
    
    // clean the assembly of directives
    case _: Directive =>
    
    case instr =>
        if (controller.previousWasJump) {
            // add an artificial label to better match the graph edges
            val newLabel = controller.nextLabel
            controller.unconditional = false
            // end the current block and start a new one
            controlFlow(newLabel)
        }
        
        controller.addInstr(instr)
}
