package wacc.integration

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers.*

import wacc.exitCodes
import utils.*

/** Tests the frontend of the compiler on invalid semantic programs. */
class InvalidSemanticTest extends AnyWordSpec {
    val root = os.pwd / "src" / "test" / "wacc" / "examples" / "invalid" / "semanticErr"
    val categories = listCategories(root)

    categories foreach { category =>
        // Test each category of tests
        s"$category tests:" should {
            listTests(root / category) foreach { test =>
                // Test each file in the category
                s"not parse ${test.relativeTo(root / category)}" in {
                    if (isDisabled(root.baseName, category)) pending
            
                    // Run test
                    val (msg, code) = compileTest(test)
                    withClue(s"$msg\n") {
                        code mustBe exitCodes.SemanticError
                    }
                }
            }
        }
    }
}
