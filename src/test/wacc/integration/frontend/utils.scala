package wacc.integration

import os.{Generator, Path}

/** Utility functions for integration tests. */
object utils {
    private val disablingFilePath =
        os.pwd / "src" / "test" / "wacc" / "integration" / "frontend" / "disable.json"
    private val disables = ujson.read(os.read(disablingFilePath))

    /** Checks disable file if @tparam testSet contains @tparam category. */
    def isDisabled(testSet: String, category: String): Boolean =
        disables(testSet).arr.exists(_.str == category)

    /** Compiles program at @tparam p, returning error message and exit code. */
    def compileTest(p: Path): (String, Int) = wacc.compile(os.read(p))

    /** Lists the categories of tests in a directory. */
    def listCategories(p: Path): Generator[String] =
        os.list.stream(p).filter(os.isDir).map(_.baseName)

    /** Lists the tests in a directory and its subdirectories. */
    def listTests(p: Path): Generator[Path] = 
        os.walk.stream(p).filter(os.isFile)
}
