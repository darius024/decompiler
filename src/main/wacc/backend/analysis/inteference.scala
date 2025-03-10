package wacc.backend.analysis

import scala.collection.mutable
import wacc.backend.ir.*

import registers.*

/**
 * Interference graph for register allocation.
 * Two nodes (temporary registers) interfere if they are live at the same time.
 */
class InterferenceGraph {
  // Map from temp register to the set of temp registers it interferes with
  val graph: mutable.Map[TempReg, mutable.Set[TempReg]] = mutable.Map.empty
  
  /**
   * Build the interference graph from a CFG with liveness information.
   */
  def build(cfg: ControlFlowGraph): Unit = {
    // First ensure all blocks have computed uses and defs
    cfg.blocks.foreach(_.computeUsesAndDefs())
    
    // For each block
    for (block <- cfg.blocks) {
        // Process each instruction to find interferences
        for (i <- 0 until block.instructions.size) {
            val instr = block.instructions(i)
            
            // Get temps defined at this instruction
            val defs = InstructionAnalysis.getDefines(instr)
            
            // Get temps live at this point (after the instruction)
            val liveAfter = block.liveAtInstruction.getOrElse(i, Set.empty)
            
            // Add interference edges
            for (defVar <- defs) {
                for (liveVar <- liveAfter) {
                    if (defVar != liveVar) {
                        addEdge(defVar, liveVar)
                        }
                    }
                }
        }

        // Ensure all nodes aer in the graph
        for (temp <- block.uses ++ block.defs) {
            if (!graph.contains(temp)) {
                graph(temp) = mutable.Set.empty
            }
        }
    }
}
  
  /**
   * Add an edge between two nodes in the graph.
   */
  def addEdge(a: TempReg, b: TempReg): Unit = {
    if (a != b) {
      graph.getOrElseUpdate(a, mutable.Set.empty) += b
      graph.getOrElseUpdate(b, mutable.Set.empty) += a
    }
  }
  
  /**
   * Get the degree (number of neighbors) of a node.
   */
  def getDegree(node: TempReg): Int = {
    graph.getOrElse(node, mutable.Set.empty).size
  }
  
  /**
   * Remove a node from the graph and return its neighbors.
   */
  def removeNode(node: TempReg): Set[TempReg] = {
    val neighbours = graph.getOrElse(node, mutable.Set.empty).toSet
    graph.remove(node)
    for (neighbor <- neighbours) {
      graph.get(neighbor).foreach(_.remove(node))
    }
    neighbours
  }


    /**
     * Check if two nodes interfere.
     */
    def interferes(a: TempReg, b: TempReg): Boolean = {
        graph.get(a).exists(_.contains(b)) || graph.get(b).exists(_.contains(a))
    }
}