package wacc.integration

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers.*

import wacc.exitCodes
import utils.*

/** Tests compiler frontend on valid programs. */
class ValidProgramTest extends AnyWordSpec {
    val root = os.pwd / "src" / "test" / "wacc" / "examples" / "valid"
    val categories = listCategories(root)

    categories foreach { category =>
        // each category
        s"$category tests:" should {
            listTests(root / category) foreach { test =>
                // each test case
                s"parse ${test.relativeTo(root / category)}" in {
                    if (isDisabled(root.baseName, category)) pending
                    
                    // run
                    val (msg, code) = compileTest(test)
                    withClue(s"$msg\n") {
                        code mustBe exitCodes.SuccessfulCompilation
                    }
                }
            }
        }
    }
}
