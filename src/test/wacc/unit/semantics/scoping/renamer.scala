package wacc.unit

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

    private val pos: Position = NoPosition

    "Scope checker" should "add a variable to the scope correctly" in {
        val prog = Program(Nil, List(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        vars shouldBe Map("x_main_0_0" -> (KType.Int, pos))
        errs shouldBe empty
    }

    it should "detect variable redeclaration in the same scope" in {
        val prog = Program(Nil, List(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        errs should contain (VariableAlreadyDeclared("x")(pos))
    }

    it should "detect usage of an undeclared variable" in {
        val prog = Program(Nil, List(
            Declaration((IntType, Id("x")(pos)), Id("y")(pos))(pos),
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        errs should contain (VariableNotInScope("y")(pos))
    }

    it should "add a function to the scope correctly" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, List(Return(IntLit(0)(pos))(pos)))(pos),
        ), List(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        funcs shouldBe Map("f" -> (KType.Int, Nil, pos))
        errs shouldBe empty
    }

    it should "detect function redeclaration" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, List(Return(IntLit(0)(pos))(pos)))(pos),
            Function((IntType, Id("f")(pos)), Nil, List(Return(IntLit(0)(pos))(pos)))(pos),
        ), List(
            Skip,
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        errs should contain (FunctionAlreadyDeclared("f")(pos))
    }

    it should "detect parameter redeclaration in function definitions" in {
        val prog = Program(List(
            Function(
                (IntType, Id("f")(pos)),
                List((IntType, Id("x")(pos)), (IntType, Id("x")(pos))),
                List(Return(IntLit(0)(pos))(pos))
            )(pos),
        ), List(
            Skip,
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        errs should contain (VariableAlreadyDeclared("x")(pos))
    }

    it should "detect not defined functions" in {
        val prog = Program(Nil, List(
            Declaration((IntType, Id("x")(pos)), Call(Id("f")(pos), Nil)(pos))(pos),
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        errs should contain (FunctionNotDefined("f")(pos))
    }

    it should "allow for variables to be shadowed" in {
        val prog = Program(Nil, List(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
            Block(List(
                Declaration((IntType, Id("x")((0, 1))), IntLit(0)(pos))(pos),
            ))(pos),
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        vars should contain ("x_main_0_0" -> (KType.Int, pos))
        vars should contain ("x_main_0_1" -> (KType.Int, (0, 1)))
        errs shouldBe empty
    }

    it should "be able to call variables from the parent scope" in {
        val prog = Program(Nil, List(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
            Block(List(
                Declaration((IntType, Id("y")(pos)), Id("x")(pos))(pos),
            ))(pos),
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        errs shouldBe empty
    }

    it should "forget variables after the block ends" in {
        val prog = Program(Nil, List(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
            Block(List(
                Declaration((IntType, Id("y")(pos)), Id("x")(pos))(pos),
            ))(pos),
            Declaration((IntType, Id("z")(pos)), Id("y")(pos))(pos),
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        errs should contain (VariableNotInScope("y")(pos))
    }

    it should "allow for mutual recursion" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, List(
                Declaration((IntType, Id("x")(pos)), Call(Id("g")(pos), Nil)(pos))(pos),
                Return(Id("x")(pos))(pos),
            ))(pos),
            Function((IntType, Id("g")(pos)), Nil, List(
                Declaration((IntType, Id("x")(pos)), Call(Id("f")(pos), Nil)(pos))(pos),
                Return(Id("x")(pos))(pos),
            ))(pos),
        ), List(
            Skip,
        ))(pos)
        val (errs, funcs, vars) = scopeCheck(prog)

        errs shouldBe empty
    }
}
