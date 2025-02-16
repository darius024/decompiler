package wacc.integration

import java.io.File
import os.{Generator, Path}
import wacc.ExitCode

/** Utility functions for integration tests. */
object utils {
    /** Checks if a test is disabled. */
    def isDisabled(part: String, testSet: String, category: String): Boolean = {
        val disablingFilePath =
            os.pwd / "src" / "test" / "wacc" / "integration" / part / "disable.json"
        val disabled = ujson.read(os.read(disablingFilePath))
        disabled(testSet).arr.exists(_.str == category)
    }

    /** Compiles program at path `p`, returning error message and exit code. */
    def compileTest(path: Path): (String, ExitCode) =
        wacc.compile(new File(path.toString))

    /** Lists the categories of tests in a directory. */
    def listCategories(path: Path): Generator[String] =
        os.list.stream(path).filter(os.isDir).map(_.baseName)

    /** Lists the tests in a directory and its subdirectories. */
    def listTests(path: Path): Generator[Path] = 
        os.walk.stream(path).filter(os.isFile)

    /** Parse the header of the test file. */
    def parseHeader(test: Path): (String, Seq[String]) = {
        // read the file to be able to fetch information from the header
        val lines = os.read.lines(test)

        // find the possible input parameter
        val input = lines
            .find(_.startsWith("# Input:"))
            .map(_.stripPrefix("# Input: ").trim)
            .getOrElse("")

        // find the output results
        val output = lines
            .dropWhile(!_.startsWith("# Output:"))
            .drop(1)
            .takeWhile(_.startsWith("# "))
            .map(_.stripPrefix("# ").trim)

        (input, output)
    }
}
