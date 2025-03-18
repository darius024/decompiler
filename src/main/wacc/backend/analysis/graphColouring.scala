package wacc.backend.analysis

import scala.collection.mutable
import wacc.backend.ir.*
import wacc.backend.generator.*

import registers.*


/**
 * Register allocator using graph coloring algorithm.
 */
class GraphColoring(availableRegisters: List[Register]) {
  
    // mutable set of available registers
    val available: mutable.Set[Register] = mutable.Set(availableRegisters*)
    
    // Mapping from temp registers to physical registers
    val colors: mutable.Map[TempReg, Register] = mutable.Map.empty
    
    /**
     * Color the interference graph using Chaitin's algorithm.
     */
    def colorGraph(graph: InterferenceGraph): Unit = {

        // 1. Simplification: Remove nodes with degree < K until none left
        // 2. Spill: If graph not empty, choose a node to spill
        // 3. Select: Pop nodes from stack and assign colors

        val k = available.size // number of available registers
        val stack = mutable.Stack[TempReg]()
        val workGraph = graph.copy()
        
        // remove nodes with degree < K
        var progress = true
        while (progress) {
          progress = false
          val simplifiableNodes = workGraph.nodes.filter(n => workGraph.getDegree(n) < k)
          if (simplifiableNodes.nonEmpty) {
            for (node <- simplifiableNodes) {
              stack.push(node)
              workGraph.removeNode(node)
              progress = true
            }
          } 
        }

        // assign colors to nodes in reverse order
        while (stack.nonEmpty) {
            val node = stack.pop()
            val unavailableColors = workGraph.getNeighbors(node).flatMap(colors.get).toSet

            val availableColor = available.find(!unavailableColors.contains(_))

            if (availableColor.isDefined) {
                // Assign a color to the node
                assignRegister(node, availableColor.get)
            } 
        }
    }
  
    
    /**
     * Assign a physical register to a temporary register.
     */
    def assignRegister(temp: TempReg, reg: Register): Unit = {
        colors(temp) = reg
    }

    /**
     * Allocates registers using graph coloring algorithm before falling back to
     * the standard allocator for register spilling.
     */
    def allocateWithGraphColoring(codeGen: CodeGenerator): CodeGenerator = {
        // Build instruction-level CFG for the whole program
        // (this respects function boundaries since Call instructions don't have edges to function entries)
        val cfgBuilder = new CFGBuilder()
        val instrCFG = new InstructionCFG()

        cfgBuilder.constructInstructionCFG(codeGen.ir, instrCFG)
        
        // Perform liveness analysis
        instrCFG.performLivenessAnalysis()
        
        // Build interference graph
        val interferenceGraph = new InterferenceGraph()
        interferenceGraph.build(instrCFG)
        
        // Perform graph coloring using callee-saved registers
        // (we'll let the standard allocator handle parameter registers in main)
        this.colorGraph(interferenceGraph)
        
        // Pre-populate the varRegs map with colored temporaries
        for ((temp, reg) <- this.colors) {
            codeGen.varRegs(temp.toString) = reg
        }
        
        // Execute standard allocator with pre-colored registers
        allocate(codeGen, true)
    }

}
