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

  // Stack frame offset for spilled variables
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
   * Assign a physical register to a temporary register.
   */
  def assignRegister(temp: TempReg, reg: Register): Unit = {
    colors(temp) = reg
  }

  /**
    * Update variable locations for temporary registers to concrete registers or stack.
    */
  def updateVariableLocations(): Unit = {
    ???
    // TO IMPLEMENT: Update the variable locations in the code generator
    // based on the colors map and spilled nodes.
    // This may involve updating the stack frame offset for spilled nodes.
  }

}
