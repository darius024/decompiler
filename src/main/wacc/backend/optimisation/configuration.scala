package wacc.backend.optimisation

import scala.collection.mutable

import wacc.backend.*
import generator.*
import ir.instructions.*
import optimisations.*

/** Optimises the intermediate representation of the backend of the compiler. */
def optimise(codeGen: CodeGenerator, flags: Seq[String]): CodeGenerator = {
    val config = parseFlags(flags)

    codeGen.instrs = config.enabledOptimisations.foldLeft(codeGen.instrs) { (ir, optimisation) =>
        optimisation(ir)
    }

    codeGen
}

/** Transforms the flags into their corresponding types. */
def parseFlags(flags: Seq[String]): OptimisationConfig = {
    val config = new OptimisationConfig

    // add information to the configuration
    flags.foreach {
        case "--register" => config.addFlag(RegisterAllocation)
        case "--peephole" => config.addFlag(Peephole)
        case "--all"      => config.allFlags
    }

    config
}

/** Types of optimisations that the compiler supports. */
object optimisations {
    sealed trait OptimisationType {
        def apply(instructions: List[Instruction]): List[Instruction]
    }

    // provide efficient register allocation by using graph colouring
    case object RegisterAllocation extends OptimisationType {
        // this optimisation is performed during allocation
        def apply(instructions: List[Instruction]): List[Instruction] = instructions
    }

    // remove redundant instructions and coalesce simple assembly patterns
    case object Peephole extends OptimisationType {
        def apply(instructions: List[Instruction]): List[Instruction] = peephole(instructions)
    }
}

/** Configures the optimisations that need to be performed on the intermediate representation. */
class OptimisationConfig {
    // order in which the optimisations should be applied
    private val flags: mutable.LinkedHashMap[OptimisationType, Boolean] = mutable.LinkedHashMap(List(
        RegisterAllocation,
        Peephole,
    ).map(_ -> false)*)

    // return the optimisations that need to be applied
    def enabledOptimisations: Set[OptimisationType] = flags.filter(_._2).map(_._1).toSet
    
    // activate all the flags
    def allFlags: Unit = {
        flags.keys.foreach(addFlag)
    }

    // activate the provided flag
    def addFlag(flag: OptimisationType): Unit = {
        flags(flag) = true
    }
}

/** Determines if the allocator should be optimised. */
def optimiseRegs(flags: Seq[String]): Boolean = flags.contains("--register")
