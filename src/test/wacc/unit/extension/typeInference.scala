package wacc.unit.extension

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import wacc.extension.decompiler.*
import representation.*

/** Tests the fifth pass of the decompiler backend. */
class TypeInferenceTests extends AnyFlatSpec {
    "Type inference" should "correctly identify integers" in {
        val program = Program(Nil,
            List(Assignment(Id("x"), IntLit(0)))
        )

        appendTypes(program) shouldBe Program(Nil, List(Declaration((IntType, Id("x")), IntLit(0))))
    }

    it should "correctly identify characters" in {
        val program = Program(Nil,
            List(Assignment(Id("x"), CharLit('a')))
        )

        appendTypes(program) shouldBe Program(Nil, List(Declaration((CharType, Id("x")), CharLit('a'))))
    }

    it should "correctly identify strings" in {
        val program = Program(Nil,
            List(Assignment(Id("x"), StrLit("wacc")))
        )

        appendTypes(program) shouldBe Program(Nil, List(Declaration((StrType, Id("x")), StrLit("wacc"))))
    }
}
