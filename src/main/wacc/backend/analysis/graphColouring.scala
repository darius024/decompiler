package wacc.backend.analysis

import scala.collection.mutable
import wacc.backend.ir.*
import wacc.backend.generator.*

import registers.*
import instructions.*

/**
 * Register allocator using graph coloring algorithm.
 */
class GraphColoringAllocator(availableRegisters: List[Register], codeGen: CodeGenerator) {
  // Mapping from temp registers to physical registers
  val colors: mutable.Map[TempReg, Register] = mutable.Map.empty
  
  // Variables that couldn't be allocated to registers
  val spilledNodes: mutable.Set[TempReg] = mutable.Set.empty

  // Stack frame offset for spilled variables\
  var stackFrameOffset: Int = 0
  
  /**
   * Color the interference graph using Chaitin's algorithm.
   */
  def colorGraph(graph: InterferenceGraph): Unit = {
    ???
    // TO IMPLEMENT:
    // 1. Simplification: Remove nodes with degree < K until none left
    // 2. Spill: If graph not empty, choose a node to spill
    // 3. Select: Pop nodes from stack and assign colors
  }
  
  /**
   * Choose which node to spill based on heuristics.
   */
  def selectSpillCandidate(graph: InterferenceGraph): TempReg = {
    ???
    // TO IMPLEMENT: Use heuristic to select a good spill candidate
    // (e.g., frequency of use, loop nesting level, etc.)
  }
  
  /**
   * Rewrite the IR with physical registers.
   */
  def rewriteIR(instructions: List[Instruction]): List[Instruction] = {
    ???
    // TO IMPLEMENT:
    // 1. Replace temp registers with allocated physical registers
    // 2. Add spill code for spilled variables
    // 3. Update function prologue/epilogue as needed
  }
}
