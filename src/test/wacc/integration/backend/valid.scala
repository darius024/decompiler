package wacc.integration.backend

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers.*
import os.*
import scala.concurrent.duration.*

import wacc.integration.utils.*

/** Tests compiler backend on valid programs.
  * 
  * The assembly files should already be generated from the
  * frontend tests at the root level of the directory.
  */
class ValidProgramTest extends AnyWordSpec {
    val root = os.pwd / "src" / "test" / "wacc" / "examples" / "valid"
    val categories = listCategories(root)

    categories foreach { category =>
        // each category
        s"$category tests:" should {
            listTests(root / category) foreach { test =>
                // each test case
                s"generate assembly for ${test.relativeTo(root / category)}" in {
                    if (isDisabled("backend", root.baseName, category)) {
                        // clean up the executable and assembly file
                        val fileName = test.last.stripSuffix(".wacc")
                        os.remove(os.pwd / s"$fileName.s")
                        os.remove(os.pwd / s"$fileName")
                        pending
                    }
                    
                    // testing the frontend should have created the assembly
                    // files at the root level of the directory

                    // parse the header to get the input and output parameters
                    val (inputParameters, outputParameter, exitCode) = parseHeader(test)
                    val fileName = test.last.stripSuffix(".wacc")

                    try {
                        // transform the compiled program into an executable object
                        val compileResult = os.proc(
                            "gcc",
                            "-o", os.pwd / s"$fileName",
                            "-z", "noexecstack",
                            os.pwd / s"$fileName.s"
                        ).call(
                            timeout = 2000,
                        )
                        
                        // there should be no errors in the assembly program
                        compileResult.out.text() mustBe empty

                        // run/emulate the executable and compare results
                        val process = os.proc(
                            s"./$fileName"
                        ).spawn(
                            stdin = os.Pipe,
                            shutdownGracePeriod = 5000,
                        )

                        // provide the input parameters
                        val delayBetweenInputs = 100.milliseconds
                        for (input <- inputParameters) {
                            Thread.sleep(delayBetweenInputs.toMillis)
                            process.stdin.write(s"$input")
                            process.stdin.flush()
                        }
                        process.stdin.close()
                        process.waitFor()
                        
                        // the output should match the expected output
                        if (outputParameter.nonEmpty) {
                            val lines = process.stdout.trim().split("\n")
                            for ((line, param) <- lines.zip(outputParameter)) {
                                if (param.contains("#addrs#")) {
                                    assert(!line.contains("fatal error"))
                                } else if (param.contains("#runtime_error#")) {
                                    assert(line.contains("fatal error"))
                                } else {
                                    line mustBe param
                                }
                            }
                        }

                        // the exit code should match
                        if (exitCode != 0) {
                            process.exitCode() mustBe exitCode
                        }
                    } finally {
                        // clean up the executable and assembly file
                        os.remove(os.pwd / s"$fileName.s")
                        os.remove(os.pwd / s"$fileName")
                    }
                }
            }
        }
    }
}
