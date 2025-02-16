package wacc.integration.backend

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers.*
import os.*

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
                    if (isDisabled("backend", root.baseName, category)) pending
                    
                    // testing the frontend should have created the assembly
                    // files at the root level of the directory

                    // parse the header to get the input and output parameters
                    val (inputParameter, outputParameter) = parseHeader(test)
                    val fileName = test.last.stripSuffix(".wacc")

                    try {
                        // transform the compiled program into an executable object
                        val compileResult = os.proc(
                            "aarch64-linux-gnu-gcc",
                            "-o", os.pwd / s"$fileName",
                            "-z", "noexecstack",
                            "-march=armv8-a",
                            os.pwd / s"$fileName"
                        ).call(
                            timeout = 2000,
                        )
                        
                        // there should be no errors in the assembly program
                        compileResult.out.text() mustBe empty

                        // run/emulate the executable and compare results
                        val emulateResult = os.proc(
                            "qemu-aarch64",
                            "-L", "/usr/aarch64-linux-gnu/",
                            os.pwd / s"$fileName"
                        ).call(
                            stdin = inputParameter,
                            timeout = 3000,
                        )
                        
                        // the output should match the expected output
                        emulateResult.out.text().trim() mustBe outputParameter.mkString("\n")
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
