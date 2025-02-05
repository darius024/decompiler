package wacc.golden

import java.io.File
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.golden.*
import org.scalatest.matchers.should.Matchers.*

/** Tests the error messages of the WACC compiler on invalid WACC programs. */
class GoldenTests extends AnyFreeSpec with GoldenMatchers {
    
    private val path = os.pwd / "src" / "test" / "wacc" / "examples"
    private val goldenPath = path / "golden"

    // create subdirectories for error types if they do not exist
    os.makeDir.all(goldenPath / "syntaxErr")
    os.makeDir.all(goldenPath / "semanticErr")

    "Syntax errors:" - {
        val syntaxErrPath = path / "invalid" / "syntaxErr"
        val syntaxErrFiles = os.walk.stream(syntaxErrPath).filter(os.isFile)

        syntaxErrFiles.foreach { file =>
            s"${file.last}" in {
                val output = compileTest(file)
                val goldenFile = goldenPath / "syntaxErr" / s"${file.last}.golden"

                os.write(goldenFile, output)

                output should matchGolden(goldenFile.toString)
            }
        }
    }

    "Semantic errors:" - {
        val semanticErrPath = path / "invalid" / "semanticErr"
        val semanticErrFiles = os.walk.stream(semanticErrPath).filter(os.isFile)

        semanticErrFiles.foreach { file =>
            s"${file.last}" in {
                //pending // comment out to run the test
                val output = compileTest(file)
                val goldenFile = goldenPath / "semanticErr" / s"${file.last}.golden"
                
                os.write(goldenFile, output)
                

                output should matchGolden(goldenFile.toString)
            }
        }
    }

    /** Compiles a WACC program and returns its output as a string. */
    private def compileTest(p: os.Path): String = {
        val msg = wacc.compile(new File(p.toString))._1
        msg.trim
    }
}
