package wacc.backend.analysis

import scala.collection.mutable
import wacc.backend.ir.*
//import wacc.backend.generator.*

import registers.*
import instructions.*

/**
 * Basic block in the control flow graph.
 * Represents a sequence of instructions with no jumps in or out except at entry/exit.
 */
class BasicBlock(val id: Int) {
  val instructions: mutable.ListBuffer[Instruction] = mutable.ListBuffer.empty
  val predecessors: mutable.Set[BasicBlock] = mutable.Set.empty
  val successors: mutable.Set[BasicBlock] = mutable.Set.empty
  
  // Liveness analysis information
  val uses: mutable.Set[TempReg] = mutable.Set.empty
  val defs: mutable.Set[TempReg] = mutable.Set.empty
  var liveIn: mutable.Set[TempReg] = mutable.Set.empty
  var liveOut: mutable.Set[TempReg] = mutable.Set.empty
  // Instruction liveness at each instruction
  val liveAtInstruction: mutable.Map[Int, mutable.Set[TempReg]] = mutable.Map.empty
  
  /**
   * Analyze instructions to find which temporary registers are defined or used.
   */
  def computeUsesAndDefs(): Unit = {
    // TO IMPLEMENT: For each instruction in the block, 
    // identify which temp registers are defined and which are used
    // ...

    // Compute liveness
    computeInstructionLiveness()
  }

  private def computeInstructionLiveness(): Unit = {
    // TO IMPLEMENT: Compute liveIn and liveOut sets for the block
    // based on the successors and predecessors
  }


}

/**
 * Control Flow Graph for a function.
 */
class ControlFlowGraph {
  val blocks: mutable.ListBuffer[BasicBlock] = mutable.ListBuffer.empty

  
  /**
   * Build the control flow graph from a list of instructions.
   */
  def build(instructions: List[Instruction]): Unit = ???
    // TO IMPLEMENT:
    // 1. Identify basic block boundaries
    // 2. Create basic blocks
    // 3. Connect blocks with edges based on control flow
  
}

/**
 * Control Flow Graph Builder.
 */
object CFGBuilder {
  /**
   * Identify function boundaries in the instruction stream.
   * Returns a list of (function name, function body) pairs.
   */
  def identifyFunctions(instructions: List[Instruction]): List[(String, List[Instruction])] = ???
    // TO IMPLEMENT: Split IR into separate functions
  
  
  /**
   * Build a CFG for each function in the program.
   */
  def buildProgramCFG(instructions: List[Instruction]): Map[String, ControlFlowGraph] = ???
    // TO IMPLEMENT: Build separate CFGs for each function
  
}

