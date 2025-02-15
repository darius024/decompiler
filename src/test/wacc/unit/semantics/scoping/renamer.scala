package wacc.unit

import cats.data.NonEmptyList
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import wacc.error.*
import wacc.semantics.*
import scoping.*
import semanticTypes.*
import wacc.syntax.*
import bridges.*
import exprs.*
import prog.*
import stmts.*
import types.*

/** Tests the scope checker and renamer of variables. */
class ScopeCheckerTest extends AnyFlatSpec {
    private val funcScope = "main"
    private val pos: Position = NoPosition

    "Scope checker" should "add a variable to the scope correctly" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.vars shouldBe Map("x/main/0/0" -> (KType.Int, pos))
        typeInfo.errs shouldBe empty
    }

    it should "detect variable redeclaration in the same scope" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.errs should contain (VariableAlreadyDeclared("x", pos)(funcScope, pos))
    }

    it should "detect usage of an undeclared variable" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), Id("y")(pos))(pos),
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.errs should contain (VariableNotInScope("y", Nil)(funcScope, pos))

        // NonEmptyList(head, tail)
    }

    it should "add a function to the scope correctly" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, NonEmptyList.of(Return(IntLit(0)(pos))(pos)))(pos),
        ), NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.funcs.keys.toList shouldBe List("f")
        typeInfo.errs shouldBe empty
    }

    it should "detect function redeclaration" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, NonEmptyList.of(Return(IntLit(0)(pos))(pos)))(pos),
            Function((IntType, Id("f")(pos)), Nil, NonEmptyList.of(Return(IntLit(0)(pos))(pos)))(pos),
        ), NonEmptyList.of(
            Skip,
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.errs should contain (FunctionAlreadyDeclared("f", pos)(funcScope, pos))
    }

    it should "detect parameter redeclaration in function definitions" in {
        val prog = Program(List(
            Function(
                (IntType, Id("f")(pos)),
                List((IntType, Id("x")(pos)), (IntType, Id("x")(pos))),
                NonEmptyList.of(Return(IntLit(0)(pos))(pos))
            )(pos),
        ), NonEmptyList.of(
            Skip,
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.errs should contain (VariableAlreadyDeclared("x", pos)(funcScope, pos))
    }

    it should "detect not defined functions" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), Call(Id("f")(pos), Nil)(pos))(pos),
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.errs should contain (FunctionNotDefined("f")(funcScope, pos))
    }

    it should "allow for variables to be shadowed" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
            Block(NonEmptyList.of(
                Declaration((IntType, Id("x")((0, 1))), IntLit(0)(pos))(pos),
            ))(pos),
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.vars should contain ("x/main/0/0" -> (KType.Int, pos))
        typeInfo.vars should contain ("x/main/0/1" -> (KType.Int, (0, 1)))
        typeInfo.errs shouldBe empty
    }

    it should "be able to call variables from the parent scope" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
            Block(NonEmptyList.of(
                Declaration((IntType, Id("y")(pos)), Id("x")(pos))(pos),
            ))(pos),
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.errs shouldBe empty
    }

    it should "forget variables after the block ends" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
            Block(NonEmptyList.of(
                Declaration((IntType, Id("y")(pos)), Id("x")(pos))(pos),
            ))(pos),
            Declaration((IntType, Id("z")(pos)), Id("y")(pos))(pos),
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.errs should contain (VariableNotInScope("y", List("x"))(funcScope, pos))
    }

    it should "allow for mutual recursion" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, NonEmptyList.of(
                Declaration((IntType, Id("x")(pos)), Call(Id("g")(pos), Nil)(pos))(pos),
                Return(Id("x")(pos))(pos),
            ))(pos),
            Function((IntType, Id("g")(pos)), Nil, NonEmptyList.of(
                Declaration((IntType, Id("x")(pos)), Call(Id("f")(pos), Nil)(pos))(pos),
                Return(Id("x")(pos))(pos),
            ))(pos),
        ), NonEmptyList.of(
            Skip,
        ))(pos)
        val typeInfo = scopeCheck(prog)

        typeInfo.errs shouldBe empty
    }
}
