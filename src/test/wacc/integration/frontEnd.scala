package wacc.integration

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.BeforeAndAfterAll
import os.*

import parsley.{Success, Failure}
import wacc.parser
import TestStatus.*

private enum TestStatus {
    case Active
    case Pending
    case Ignored
}

private case class TestCount(var correct: Int, var total: Int)

class FrontEndTests extends AnyFreeSpec with BeforeAndAfterAll {
    val examples = os.pwd / "src" / "test" / "wacc" / "examples"

    val validCount = TestCount(0, 0)
    val syntaxCount = TestCount(0, 0)
    val semanticCount = TestCount(0, 0)

    val validTestCases = Map(
        "advanced"    -> Pending,
        "array"       -> Pending,
        "basic"       -> Pending,
        "expressions" -> Pending,
        "function"    -> Pending,
        "if"          -> Pending,
        "IO"          -> Pending,
        "pairs"       -> Pending,
        "runtimeErr"  -> Pending,
        "scope"       -> Pending,
        "sequence"    -> Pending,
        "variables"   -> Pending,
        "while"       -> Pending
    )

    val syntaxErrTestCases = Seq(
        "array", "basic", "expressions", "function", "if", "literals",
        "pairs", "print", "sequence", "variables", "while"
    )

    val semanticErrTestCases = Map(
        "array"       -> Pending,
        "exit"        -> Pending,
        "expressions" -> Pending,
        "function"    -> Pending,
        "if"          -> Pending,
        "IO"          -> Pending,
        "multiple"    -> Pending,
        "pairs"       -> Pending,
        "print"       -> Pending,
        "read"        -> Pending,
        "scope"       -> Pending,
        "variables"   -> Pending,
        "while"       -> Pending
    )

    "Valid WACC examples:" - {
        for ((testDir, status) <- validTestCases) {
            s"in directory $testDir:" - {
                val files = getValidFiles(testDir)

                for (file <- files) {
                    validCount.total += 1

                    s"should parse ${printValidFile(testDir, file)}" in runTest(status) {
                        testValidFile(file)
                    }
                }
            }
        }
    }

    "Invalid syntax WACC examples:" - {
        for (testDir <- syntaxErrTestCases) {
            s"in directory $testDir:" - {
                val files = getInvalidFiles("syntaxErr", testDir)

                for (file <- files) {
                    syntaxCount.total += 1

                    s"should not parse ${printInvalidFile("syntaxErr", testDir, file)}" in {
                        testInvalidSyntaxFile(file)
                    }
                }
            }
        }
    }

    "Invalid semantic WACC examples:" - {
        for ((testDir, status) <- semanticErrTestCases) {
            s"in directory $testDir:" - {
                val files = getInvalidFiles("semanticErr", testDir)

                for (file <- files) {
                    semanticCount.total += 1

                    s"should not parse ${printInvalidFile("semanticErr", testDir, file)}" in runTest(status) {
                        testInvalidSemanticFile(file)
                    }
                }
            }
        }
    }

    def runTest(status: TestStatus)(testLogic: =>Unit): Unit = status match {
        case Active  => testLogic
        case Pending => pending
        case Ignored => 
    }

    override def afterAll(): Unit = {
        println("\nTESTS SUMMARY:")

        printSummary(validCount, "valid")
        printSummary(syntaxCount, "invalid syntactic")
        printSummary(semanticCount, "invalid semantic")
        println()

        val total = validCount.total + syntaxCount.total + semanticCount.total
        val correct = validCount.correct + syntaxCount.correct + semanticCount.correct

        println(s"Total tests passed: ${correct} out of ${total}")
        println(f"Progress: ${correct.toDouble / total * 100}%.2f\n")
    }


    private def printValidFile(testDir: String, file: Path): String =
        file.relativeTo(examples / "valid" / testDir).toString
    private def printInvalidFile(testType: String, testDir: String, file: Path): String =
        file.relativeTo(examples / "invalid" / testType / testDir).toString
    
    private def getValidFiles(testDir: String): Generator[Path] =
        os.walk.stream(examples / "valid" / testDir).filter(os.isFile)
    private def getInvalidFiles(testType: String, testDir: String): Generator[Path] =
        os.walk.stream(examples / "invalid" / testType / testDir).filter(os.isFile)

    private def testValidFile(file: Path) = {
        val waccFile: String = os.read(file)
        parser.parse(waccFile) match {
            case Success(_) => {
                // TODO: Perform semantic analysis
                None match {
                    case _ => fail(s"program must be semantically valid\n"
                                 + s"with error message:\nmsg\n") 
                    // case _ => validCount.correct += 1
                }
            }
            case Failure(msg) => fail(s"program must be semantically valid\n"
                                    + s"with error message:\n${msg}\n") 
        }
    }
    private def testInvalidSyntaxFile(file: Path) = {
        val waccFile: String = os.read(file)
        parser.parse(waccFile) match {
            case Success(_) => fail(s"program must not be syntactically valid\n") 
            case Failure(_) => syntaxCount.correct += 1
        }
    }
    private def testInvalidSemanticFile(file: Path) = {
        val waccFile: String = os.read(file)
        parser.parse(waccFile) match {
            case Success(_) => {
                // TODO: Perform semantic analysis
                None match {
                    case _ => fail(s"program must not be semantically valid\n")
                    // case _ => semanticCount.correct += 1
                }
            }
            case Failure(_) => fail(s"program must be syntactically valid\n")
        }
    }

    private def printSummary(testCount: TestCount, validType: String) = {
        if (testCount.total == 0) {
            println(s"No $validType tests run.")
        } else {
            if (testCount.correct == testCount.total) {
                println(s"All $validType tests passed.")
            } else {
                println(s"Some $validType tests passed: ${testCount.correct} out of ${testCount.total}.")
            }
        }
    }
}
