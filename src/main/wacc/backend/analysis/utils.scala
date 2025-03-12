// package wacc.backend.analysis

// import wacc.backend.ir.*
// import registers.*
// import instructions.*

//TODO put utility functions in this file once implemented


/**
 * Utility for analyzing instruction register usage.
 */


// /**
//  * Live Variable Analysis.
//  */
// object LivenessAnalyser {
//   /**
//    * Perform live variable analysis on a control flow graph.
//    * Updates liveIn and liveOut sets for each basic block.
//    */
//   def analyse(cfg: ControlFlowGraph): Unit = {
//     // TO IMPLEMENT:
//     // 1. First compute uses and defs for each block
//     // 2. Iteratively update liveIn and liveOut until fixed point
//     //    - liveOut[B] = Union of liveIn[S] for all successors S of B
//     //    - liveIn[B] = uses[B] U (liveOut[B] - defs[B])
//   }
// }

