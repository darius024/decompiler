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

    // Getter for all nodes in the graph
    def nodes: Set[TempReg] = graph.keySet.toSet
    
    /**
     * Build the interference graph from an instruction-level CFG with liveness information.
     */
    def build(instrCFG: InstructionCFG): Unit = {

        // Then perform liveness analysis
        instrCFG.performLivenessAnalysis()
        
        // For each instruction node in the CFG
        for (instr <- instrCFG.nodes) {
            // Get temps defined at this instruction
            val defs =  instr.defs
            
            // Get temps live at this point (after the instruction)
            val liveOut = instr.liveOut

            // Add interference edges
            for (defVar <- defs) {
                for (liveVar <- liveOut) {
                    if (defVar != liveVar) {
                        addEdge(defVar, liveVar)
                    }
                }
            }
            
            // Ensure all nodes aer in the graph
            for (temp <- instr.uses ++ instr.defs) {
                if (!graph.contains(temp)) {
                    graph(temp) = mutable.Set.empty
                }
            }
        }
    }

    /**
     * Create a copy of the interference graph.
     */
    def copy(): InterferenceGraph = {
        val newGraph = new InterferenceGraph()
        for ((temp, neighbors) <- graph) {
            newGraph.graph(temp) = mutable.Set.from(neighbors)
        }
        newGraph
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
     * Get the neighbors of a node.
     */
    def getNeighbors(node: TempReg): Set[TempReg] = {
        graph.getOrElse(node, mutable.Set.empty).toSet
    }

    /**
     * Remove a node from the graph and return its neighbors.
     */
    def removeNode(node: TempReg): Set[TempReg] = {
        val neighbours = getNeighbors(node)
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
