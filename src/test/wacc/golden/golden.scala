package wacc.golden

import java.io.File
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.golden.*
import org.scalatest.matchers.should.Matchers.*
import os.{Generator, Path}

/** Tests the error messages of the WACC compiler on invalid WACC programs. */
class GoldenTests extends AnyFreeSpec with GoldenMatchers {
    
    private val path = os.pwd / "src" / "test" / "wacc"
    private val goldenPath = path / "golden" / "examples"

    // create subdirectories for error types if they do not exist
    os.makeDir.all(goldenPath / "syntaxErr")
    os.makeDir.all(goldenPath / "semanticErr")

    "Syntax errors:" - {
        runGolden("syntaxErr")
    }

    "Semantic errors:" - {
        runGolden("semanticErr")
    }

    /** Compiles a WACC program and returns its output as a string. */
    private def compileTest(p: os.Path): String = {
        val msg = wacc.compile(new File(p.toString))._1
        msg.trim
    }

    /** Runs the golden tests on the given files. */
    private def runGolden(errType: String): Unit = {
        val errPath = path / "examples" / "invalid" / errType
        val errFiles: Generator[Path] = os.walk.stream(errPath).filter(os.isFile)

        errFiles.foreach { file =>
            s"${file.last}" in {
                //pending // comment out to run the test
                val output = compileTest(file)
                val goldenFile = goldenPath / errType / s"${file.last.stripSuffix(".wacc")}.golden"
                
                if (!os.exists(goldenFile)) {
                    os.write(goldenFile, output)
                }

                output should matchGolden(goldenFile.toString)
            }
        }
    }
}
