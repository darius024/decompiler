package wacc.integration

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.BeforeAndAfterAll
import os.*

import wacc.compile
import wacc.exitCodes

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
                        testProgram(file, exitCodes.SuccessfulCompilation, validCount)
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
                        testProgram(file, exitCodes.SyntaxError, syntaxCount)
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
                        testProgram(file, exitCodes.SemanticError, semanticCount)
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

    private def testProgram(file: Path, expectedExitCode: Int, testCount: TestCount) = {
        val waccFile: String = os.read(file)
        val (msg, actualExitCode) = compile(waccFile)

        withClue(s"${printError(actualExitCode)}:\n$msg\nthe actual exit code ") {
            actualExitCode shouldEqual expectedExitCode
        }
        testCount.correct += 1
    }

    private def printError(exitCode: Int): String = exitCode match {
        case exitCodes.SuccessfulCompilation => "program should not have compiled"
        case exitCodes.SyntaxError => "program should not have syntax errors"
        case exitCodes.SemanticError => "program should not have semantic errors"
    }

    private def printValidFile(testDir: String, file: Path): String =
        file.relativeTo(examples / "valid" / testDir).toString
    private def printInvalidFile(testType: String, testDir: String, file: Path): String =
        file.relativeTo(examples / "invalid" / testType / testDir).toString
    
    private def getValidFiles(testDir: String): Generator[Path] =
        os.walk.stream(examples / "valid" / testDir).filter(os.isFile)
    private def getInvalidFiles(testType: String, testDir: String): Generator[Path] =
        os.walk.stream(examples / "invalid" / testType / testDir).filter(os.isFile)

    private def printSummary(testCount: TestCount, validType: String) = {
        if (testCount.correct == testCount.total) {
            println(s"All $validType tests passed.")
        } else if (testCount.correct == 0) {
            println(s"No $validType tests passed.")
        } else {
            println(s"Some $validType tests passed: ${testCount.correct} out of ${testCount.total}.")
        }
    }
}
