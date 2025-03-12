package wacc.backend.analysis

import scala.collection.mutable
import wacc.backend.ir.*
//import wacc.backend.generator.*

import registers.*
import instructions.*

/**
 * Base class for control flow graph nodes.
 * Provides common functionality for both block and instruction nodes.
 */
sealed trait CFGNode {
    val id: Int
    val predecessors: mutable.Set[CFGNode] = mutable.Set.empty
    val successors: mutable.Set[CFGNode] = mutable.Set.empty
}



/**
 * An instruction-level CFG node representing a single instruction.
 */
class InstructionNode(val id: Int, val instruction: Instruction) extends CFGNode {
    // Liveness analysis information for this instruction
    val defs: Set[TempReg] = instruction.getDefs
    val uses: Set[TempReg] = instruction.getUses
    var liveIn: mutable.Set[TempReg] = mutable.Set.empty
    var liveOut: mutable.Set[TempReg] = mutable.Set.empty
  
}

/**
 * A basic block in the control flow graph.
 * Contains a sequence of instructions with no jumps in or out except at entry/exit.
 */
class BasicBlock(val id: Int) extends CFGNode {
    val instructions: mutable.ListBuffer[Instruction] = mutable.ListBuffer.empty
    
    // For dead code elimination and other block-level optimizations
    var isReachable: Boolean = false
    var isLive: Boolean = false
    
    // Maps instruction index to its corresponding instruction node in the instruction-level CFG
    val instructionNodes: mutable.Map[Int, InstructionNode] = mutable.Map.empty
}

/**
 * Control Flow Graph factory that can generate both block-level and instruction-level CFGs.
 */
class CFGBuilder {
    // Unique ID counter for nodes
    private var nextNodeId: Int = 0
    
    // Get next unique node ID
    private def getNextNodeId(): Int = {
        val id = nextNodeId
        nextNodeId += 1
        id
    }

    
    /**
     * Build both block-level and instruction-level CFGs from a list of instructions.
     * @param instructions The IR instruction list
     * @return A tuple (blockCFG, instructionCFG) containing both CFG representations
     */
    def buildDualCFGs(instructions: List[Instruction]): (BlockCFG, InstructionCFG) = {
        // Reset node ID counter
        nextNodeId = 0
        
        // 1. Identify basic block boundaries (leaders)
        val leaders = findLeaders(instructions)
        
        // 2. Create both CFGs simultaneously
        val blockCFG = new BlockCFG()
        val instrCFG = new InstructionCFG()
        
        // 3. Perform the actual construction
        constructCFGs(instructions, leaders, blockCFG, instrCFG)
        
        // 4. Return both CFGs
        (blockCFG, instrCFG)
    }

    /**
     * Find leader instructions that start basic blocks.
     */
    private def findLeaders(instructions: List[Instruction]): Set[Int] = {
        val leaders = mutable.Set[Int](0) // First instruction is always a leader
        
        for (i <- 0 until instructions.size - 1) {
        val instr = instructions(i)
        
        instr match {
            // Instructions that potentially change control flow
            case Jump(_,_) | JumpComp(_, _) | Ret =>
                // The instruction after a jump is a leader
                if (i + 1 < instructions.size) leaders += (i + 1)
                
            case _ => // Not a control flow instruction
        }
        
        // Find jump targets
        instr match {
            case Jump(label, _) =>
                // Find the target index of this jump
                val targetIdx = instructions.indexWhere {
                    case Label(name) if name == label.name => true
                    case _ => false
                }
                if (targetIdx >= 0) leaders += targetIdx
                
            case JumpComp(label, _) =>
                // Find the target index of this conditional jump
                val targetIdx = instructions.indexWhere {
                    case Label(name) if name == label.name => true
                    case _ => false
                }
                if (targetIdx >= 0) leaders += targetIdx
            case _ => // Not a jump instruction
        }
        }
        
        leaders.toSet
    }

  /**
   * Construct both block-level and instruction-level CFGs in a single pass.
   */
    private def constructCFGs(
        instructions: List[Instruction], 
        leaders: Set[Int],
        blockCFG: BlockCFG,
        instrCFG: InstructionCFG
        ): Unit = {
        // TO IMPLEMENT: Construct both CFGs in a single pass
        // This should:
        // 1. Create basic blocks and add them to blockCFG
        // 2. Create instruction nodes and add them to instrCFG
        // 3. Set up predecessor/successor relationships in both CFGs
        // 4. Connect instruction nodes to their containing basic blocks


        if (instructions.nonEmpty){
            // create all blocks and instruction nodes

            var currentBlockIdx = -1
            var currentBlock: Option[BasicBlock] = None

            for (i <- instructions.indices) {
                val instr = instructions(i)

                // create instruction node
                val instrNode = new InstructionNode(getNextNodeId(), instr)
                instrCFG.nodes += instrNode
                instrCFG.setNodeForInstruction(instr, instrNode)

                // If this is a leader, start a new block
                if (leaders.contains(i)) {
                    
                    currentBlockIdx += 1
                    currentBlock = Some(new BasicBlock(currentBlockIdx))
                    blockCFG.blocks += currentBlock.get
                    blockCFG.setBlockForIndex(i, currentBlock.get)

                    // if this is the first leader, set it as the entry block
                    if (blockCFG.entryBlock.isEmpty) {
                        blockCFG.entryBlock = Some(currentBlock.get)
                        instrCFG.entryNode = Some(instrNode)
                    }                    
                }

                // Add instruction to the current block
                if (currentBlock.isDefined) {
                    currentBlock.get.instructions += instr
                    currentBlock.get.instructionNodes(currentBlock.get.instructions.size - 1) = instrNode
                }

                // Check if this is an exit point
                instr match {
                    case Ret =>
                        blockCFG.exitBlocks += currentBlock.get
                        instrCFG.exitNodes += instrNode
                    case _ => // Not an exit point
                }
            }

            // Create helper function to identify successors instruction indices
            def getSuccessorIndices(idx: Int): List[Int] = {
                
                // Get the instruction at the current index
                val instr = instructions(idx)

                instr match {
                    case Jump(label, _) =>
                        // Find the target index of this unconditional jump
                        val targetIdx = instructions.indexWhere {
                            case Label(name) if name == label.name => true
                            case _ => false
                        }

                        // If the target index is valid, return it otherwise return empty list
                        if (targetIdx >= 0) List(targetIdx) else Nil 
                    
                    case JumpComp(label, _) =>
                        // Find the target index and fallthrough index of this conditional jump
                        val targetIdx = instructions.indexWhere {
                            case Label(name) if name == label.name => true
                            case _ => false
                        }
                        val fallThroughIdx = idx + 1 // Next instruction is the fallthrough

                        // Build list of all possible successors
                        if (targetIdx >= 0 && fallThroughIdx < instructions.size) {
                            List(targetIdx, fallThroughIdx) // Both jump target and fallthrough
                        } else if (targetIdx >= 0) { 
                            List(targetIdx) // Only jump target
                        } else if (fallThroughIdx < instructions.size) {
                            List(fallThroughIdx) // Only fall through
                        } else {
                            Nil // No successors
                        }

                    case Ret =>
                        // Return empty list for return instructions
                        Nil
                    case _ =>
                        // For all other instructions, return the next instruction index
                        if (idx + 1 < instructions.size) List(idx + 1) else Nil
                }
            }

            // Helper function fund which block contains a specific instruction index
            def findBlockForIndex(idx: Int): Option[BasicBlock] = {
                // If index is a leader, its block is directly mapped
                blockCFG.getBlockForIndex(idx).orElse {
                    //Otherwise, find the closest leader before the index
                    val leaderIdx = leaders.filter(_ < idx).maxOption
                    leaderIdx.flatMap(blockCFG.getBlockForIndex)
                }
            }


            // Process all leaders to connect blocks and instruction nodes
            for (leaderIdx <- leaders) {
                blockCFG.getBlockForIndex(leaderIdx).foreach { block => 
                    if (block.instructions.nonEmpty) {
                        // BLOCK-LEVEL CONNECTIONS
                        // Find the last instruction in the block
                    
                        val lastInstrIdx2 = block.instructions.size - 1 // Get last instruction index in the block
                        val lastInstrNode = block.instructionNodes(lastInstrIdx2)
                        val lastInstrIdx = leaderIdx + lastInstrIdx2 // Get the absolute index of the last instruction in the instructions list

                        // Get all successor indices for the last instruction
                        val successorIndices = getSuccessorIndices(lastInstrIdx)


                        // INSTRUCTION-LEVEL CONNECTIONS
                        // Connect instructions sequentially within the block
                        for (i <- 0 until block.instructions.size - 1) {
                            val currentInstrNode = block.instructionNodes(i)
                            val nextInstrNode = block.instructionNodes(i + 1)

                            currentInstrNode.successors += nextInstrNode
                            nextInstrNode.predecessors += currentInstrNode
                        }
                        
                    
                        // Connect to each successor instruction
                        for (succIdx <- successorIndices) {
                            
                            // Block-level connection
                            findBlockForIndex(succIdx).foreach { succBlock =>
                                if (succBlock != block) {  // Avoid self-loops at block level
                                    block.successors += succBlock
                                    succBlock.predecessors += block
                                }
                            }   


                            // Get the instruction node for this successor
                            instrCFG.getNodeForInstruction(instructions(succIdx)).foreach { succNode =>
                                lastInstrNode.successors += succNode
                                succNode.predecessors += lastInstrNode
                            }
                        }
                    }
                }
            }

        } else {
            // No instructions to process
        }
    }
}



/**
 * Block-level Control Flow Graph.
 * Used for optimizations like dead code elimination.
 */
class BlockCFG {
    val blocks: mutable.ListBuffer[BasicBlock] = mutable.ListBuffer.empty
    var entryBlock: Option[BasicBlock] = None
    var exitBlocks: mutable.Set[BasicBlock] = mutable.Set.empty


    // Map from leader instruction index to basic block
    private val indexToBlock: mutable.Map[Int, BasicBlock] = mutable.Map.empty
    
    // Set a block for a leader index
    def setBlockForIndex(idx: Int, block: BasicBlock): Unit = {
        indexToBlock(idx) = block
    }
    
    // Get the block for a leader index
    def getBlockForIndex(idx: Int): Option[BasicBlock] = {
        indexToBlock.get(idx)
    }
    
    /**
     * Find the block containing a specific instruction.
     */
    def findBlockForInstruction(instr: Instruction): Option[BasicBlock] = {
        // Efficient implementation using blocks and their instructions
        blocks.find(_.instructions.contains(instr))
    }

    /**
     * Find unreachable blocks in the CFG.
     * @return Set of unreachable block IDs
     */
    def findUnreachableBlocks(): Set[Int] = {
        // TO IMPLEMENT: Mark reachable blocks via DFS from entry block
        // Return IDs of blocks that aren't reachable
        Set.empty
    }
    
    /**
     * Perform dead code elimination.
     * @return List of eliminated instructions
     */
    def eliminateDeadCode(): List[Instruction] = {
        // TO IMPLEMENT: Remove unreachable blocks
        // Return the eliminated instructions
        List.empty
    }
    
    /**
     * Print the block-level CFG for debugging.
     */
    def printCFG(): Unit = {
        // TO IMPLEMENT: Print the block-level CFG structure
    }
}


/**
 * Instruction-level Control Flow Graph.
 * Used for precise liveness analysis and register allocation.
 */
class InstructionCFG {
    val nodes: mutable.ListBuffer[InstructionNode] = mutable.ListBuffer.empty
    var entryNode: Option[InstructionNode] = None
    var exitNodes: mutable.Set[InstructionNode] = mutable.Set.empty
    
    // Maps instructions to their nodes
    private val instructionToNode: mutable.Map[Instruction, InstructionNode] = mutable.Map.empty
    
    /**
     * Get the node for an instruction.
     */
    def getNodeForInstruction(instr: Instruction): Option[InstructionNode] = {
        instructionToNode.get(instr)
    }

    /**
     * Get the instruction for a node.
     */
    def getInstructionForNode(node: InstructionNode): Option[Instruction] = {
        instructionToNode.find(_._2 == node).map(_._1)
    }

    /**
     * Set the node for an instruction.
     */
    def setNodeForInstruction(instr: Instruction, node: InstructionNode): Unit = {
        instructionToNode(instr) = node
    }

    
    /**
     * Perform liveness analysis to compute liveIn and liveOut for each instruction.
     */
    def performLivenessAnalysis(): Unit = {
        // TO IMPLEMENT: Run iterative dataflow algorithm to compute liveness
        // Start with empty liveIn/liveOut sets
        // Iterate until fixpoint:
        //   For each node in reverse order:
        //     liveOut[n] = union of liveIn[s] for all successors s
        //     liveIn[n] = uses[n] ∪ (liveOut[n] - defs[n])  
        
        // clear previous liveness if we ran liveness analysis before 
        for (node <- nodes) {
            node.liveIn.clear()
            node.liveOut.clear()
        }

        // Worklist algorithm for liveness analysis
        val worklist = mutable.Queue.from(nodes)

        while (worklist.nonEmpty) {
            val node = worklist.dequeue()

            // Save the old liveIn and liveOut sets
            val oldLiveOut = node.liveOut.toSet
            val oldLiveIn = node.liveIn.toSet

            // liveOut = union of liveIn of successors
            val newLiveOut= mutable.Set[TempReg]()
            node.successors.foreach {
                case succNode: InstructionNode =>
                    newLiveOut ++= succNode.liveIn
                case _ => // Ignore non-instruction nodes
            }  

            val newLiveIn = node.uses ++ (newLiveOut.diff(node.defs))

            // Update liveIn and liveOut
            node.liveIn.clear()
            node.liveIn ++= newLiveIn
            node.liveOut.clear()
            node.liveOut ++= newLiveOut

            // If liveIn or liveOut changed, add predecessors to worklist
            if (oldLiveIn != node.liveIn || oldLiveOut != node.liveOut) {
                node.predecessors.foreach { 
                    case predNode: InstructionNode => worklist += predNode
                    case _ => // Ignore non-instruction nodes
                }
            }
        }
    }

    /**
     * Check if a temporary register is live at a specific instruction.
     */
    def isLiveAt(temp: TempReg, instr: Instruction, before: Boolean = false): Boolean = {
        getNodeForInstruction(instr) match {
            case Some(node) =>
                if (before) {
                    node.liveIn.contains(temp)
                } else {
                    node.liveOut.contains(temp)
                }
            case None => false // Instruction not found in CFG
        }
    }
    
    /**
     * Get all instructions where a temporary register is live.
     */
    def getLiveRangeFor(temp: TempReg): List[Instruction] = {
        nodes
            .filter { node => node.liveIn.contains(temp) || node.liveOut.contains(temp)}
            .map(_.instruction)
            .toList
    }
    /**
     * Print the instruction-level CFG with liveness information.
     */
    def printCFG(): Unit = {
      // TO IMPLEMENT: Print the instruction-level CFG with liveness info
    }
}
