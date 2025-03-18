package wacc.extension.decompiler

def prettyPrint(controller: ControlFlow): Unit = {
    val directives = controller.programDirectives
    val blocks = controller.programBlocks

    println("Directives:")
    println(directives.map(println))
    println()

    println("Blocks:")
    blocks.foreach { (label, block) =>
        println(s"${label}:")
        block.instrs.foreach(println)
        println()
    }
}
