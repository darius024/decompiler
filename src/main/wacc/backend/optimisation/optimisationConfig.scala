package wacc.backend.optimisation

// Define the optimization types
enum OptimisationType {
  case Peephole
  // Add more optimisations here as needed
  
  def flagName: String = this match {
    case Peephole => "peephole"
    // Add more cases for other optimisations
  }
}


// Configuration that holds enabled optimisation types
case class OptimisationConfig(
  enabledOpts: Set[OptimisationType] = Set.empty
) {
  def isEnabled: Boolean = enabledOpts.nonEmpty
  def enabledOptimisations: List[String] = enabledOpts.map(_.flagName).toList
  
  // Helper methods for type-safe access
  def peephole: Boolean = enabledOpts.contains(OptimisationType.Peephole)
  // Add more methods for other optimisations as needed
  
  def all: OptimisationConfig = copy(enabledOpts = OptimisationType.values.toSet)
}

// Companion object for factory methods
object OptimisationConfig {
  // Create config with specific optimisations enabled
  def apply(types: OptimisationType*): OptimisationConfig = 
    new OptimisationConfig(types.toSet)
  
}
