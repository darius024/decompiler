package wacc.integration

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers.*

import wacc.exitCodes
import utils.*

/** Tests compiler frontend on invalid semantic programs. */
class InvalidSyntaxTest extends AnyWordSpec {
    val root = os.pwd / "src" / "test" / "wacc" / "examples" / "invalid" / "syntaxErr"
    val categories = listCategories(root)

    categories foreach { category =>
        // each category
        s"$category tests:" should {
            listTests(root / category) foreach { test =>
                // each test case
                s"not parse ${test.relativeTo(root / category)}" in {
                    if (isDisabled(root.baseName, category)) pending

                    // run
                    val (msg, code) = compileTest(test)
                    println(msg)
                    withClue(s"$msg\n") {
                        code mustBe exitCodes.SyntaxError
                    }
                }
            }
        }
    }
}
