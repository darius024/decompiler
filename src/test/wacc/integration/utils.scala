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

    /** Decompiles program at path `p`, returning error message and exit code. */
    def decompileTest(path: Path): (String, ExitCode) =
        wacc.decompile(new File(path.toString))

    /** Lists the categories of tests in a directory. */
    def listCategories(path: Path): Generator[String] =
        os.list.stream(path).filter(os.isDir).map(_.baseName)

    /** Lists the tests in a directory and its subdirectories. */
    def listTests(path: Path): Generator[Path] = 
        os.walk.stream(path).filter(os.isFile)

    /** Parse the header of the test file. */
    def parseHeader(test: Path): (Array[String], Seq[String], Int) = {
        // read the file to be able to fetch information from the header
        val lines = os.read.lines(test)

        // find the possible input parameter
        // Constants for header markers
        val INPUT_MARKER = "# Input:"
        val OUTPUT_MARKER = "# Output:"
        val EXIT_MARKER = "# Exit:"
        val COMMENT_PREFIX = "# "
        val DEFAULT_EXIT_CODE = "0"
        val HEADER_LINE_COUNT = 1

        val input = lines
            .find(_.startsWith(INPUT_MARKER))
            .map(_.stripPrefix(s"$INPUT_MARKER ").trim)
            .getOrElse("")
            .split(" ")

        // find the output results
        val output = lines
            .dropWhile(!_.startsWith(OUTPUT_MARKER))
            .drop(HEADER_LINE_COUNT)
            .takeWhile(_.startsWith(COMMENT_PREFIX))
            .map(_.stripPrefix(COMMENT_PREFIX).trim)
        
        // find the possible exit code
        val exit = lines
            .dropWhile(!_.startsWith(EXIT_MARKER))
            .drop(HEADER_LINE_COUNT)
            .headOption
            .map(_.stripPrefix(COMMENT_PREFIX).trim)
            .getOrElse(DEFAULT_EXIT_CODE)

        (input, output, exit.toInt)
    }
}
