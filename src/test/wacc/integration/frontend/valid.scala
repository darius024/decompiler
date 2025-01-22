package wacc.integration

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers
import wacc.exitCodes

class ValidProgramTest extends AnyWordSpec with Matchers {
    val root = os.pwd / "src" / "test" / "wacc" / "examples" / "valid"
    val categories = listCategories(root)

    categories foreach { category =>
        s"$category tests" should {
            listTests(root / category) foreach { test =>
                s"parse ${test.relativeTo(root / category)}" in {
                    if (isDisabled(root.baseName, category)) pending
                    
                    // run test
                    val (msg, code) = wacc.integration.compile(test)
                    withClue(s"$msg\n") {
                        code mustEqual exitCodes.SuccessfulCompilation
                    }
                }
            }
        }
    }
}
