package wacc.unit

import cats.data.NonEmptyList
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inside.*

import wacc.error.*
import wacc.semantics
import semantics.scoping.semanticTypes.*
import wacc.syntax.*
import bridges.*
import exprs.*
import prog.*
import stmts.*
import types.*

/** Tests the type checker for programs not well-typed. */
class TypeCheckerTests extends AnyFlatSpec {
    private val funcScope = "main"
    private val pos: Position = NoPosition

    "Type checker" should "detect type mismatches in declarations" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), BoolLit(false)(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Bool, Set(KType.Int))(funcScope, pos))
        }
    }

    it should "detect type mismatches in assignments" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
            Assignment(Id("x")(pos), BoolLit(false)(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Bool, Set(KType.Int))(funcScope, pos))
        }
    }

    it should "detect type mismatches in return statements" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, NonEmptyList.of(Return(BoolLit(false)(pos))(pos)))(pos),
        ), NonEmptyList.of(
            Skip,
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Bool, Set(KType.Int))(funcScope, pos))
        }
    }

    it should "detect type mismatches in arithmetic expressions" in {
        val prog = Program(Nil, NonEmptyList.of(
            Print(Add(IntLit(0)(pos), BoolLit(false)(pos))(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Bool, Set(KType.Int))(funcScope, pos))
        }
    }

    it should "detect type mismatches in relational expressions" in {
        val prog = Program(Nil, NonEmptyList.of(
            Print(Less(BoolLit(false)(pos), BoolLit(false)(pos))(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Bool, Set(KType.Int, KType.Char))(funcScope, pos))
        }
    }

    it should "detect type mismatches in logical expressions" in {
        val prog = Program(Nil, NonEmptyList.of(
            Print(And(BoolLit(false)(pos), IntLit(0)(pos))(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Int, Set(KType.Bool))(funcScope, pos))
        }
    }

    it should "detect type mismatches in unary expressions" in {
        val prog = Program(Nil, NonEmptyList.of(
            Print(Not(IntLit(0)(pos))(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Int, Set(KType.Bool))(funcScope, pos))
        }
    }

    it should "correctly identify expression returns" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((BoolType, Id("x")(pos)), Equal(IntLit(0)(pos), IntLit(0)(pos))(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Right(prog) => succeed
        }
    }

    it should "detect type mismatches in array expressions" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((ArrayType(IntType, 1)(pos), Id("ar")(pos)), ArrayLit(List(BoolLit(false)(pos)))(pos))(pos),
            Declaration((ArrayType(IntType, 1)(pos), Id("x")(pos)), Id("ar")(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Bool, Set(KType.Int))(funcScope, pos))
        }
    }

    it should "detect type mismatches in pair expressions" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((PairType(IntType, CharType)(pos), Id("p")(pos)), NewPair(IntLit(0)(pos), BoolLit(false)(pos))(pos))(pos),
            Declaration((PairType(IntType, CharType)(pos), Id("x")(pos)), Id("p")(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Pair(KType.Int, KType.Bool), Set(KType.Pair(KType.Int, KType.Char)))(funcScope, pos))
        }
    }

    it should "detect type mismatches in pair extraction" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((PairType(IntType, CharType)(pos), Id("p")(pos)), NewPair(IntLit(0)(pos), CharLit('a')(pos))(pos))(pos),
            Declaration((IntType, Id("x")(pos)), Fst(Id("p")(pos))(pos))(pos),
            Declaration((IntType, Id("y")(pos)), Snd(Id("p")(pos))(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Char, Set(KType.Int))(funcScope, pos))
        }
    }

    it should "detect type mismatches in function returns" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, NonEmptyList.of(Return(BoolLit(true)(pos))(pos)))(pos),
        ), NonEmptyList.of(
            Skip,
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Bool, Set(KType.Int))(funcScope, pos))
        }
    }

    it should "detect type mismatches in function calls" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, NonEmptyList.of(Return(IntLit(0)(pos))(pos)))(pos),
        ), NonEmptyList.of(
            Declaration((BoolType, Id("x")(pos)), Call(Id("f")(pos), Nil)(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Int, Set(KType.Bool))(funcScope, pos))
        }
    }

    it should "detect type mismatches in parameter types" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), List((IntType, Id("x")(pos))), NonEmptyList.of(Return(Id("x")(pos))(pos)))(pos),
        ), NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), Call(Id("f")(pos), List(BoolLit(false)(pos)))(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Bool, Set(KType.Int))(funcScope, pos))
        }
    }

    it should "detect type mismatches in if statements" in {
        val prog = Program(Nil, NonEmptyList.of(
            If(IntLit(0)(pos), NonEmptyList.of(Skip), NonEmptyList.of(Skip))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Int, Set(KType.Bool))(funcScope, pos))
        }
    }

    it should "detect type mismatches in read statements" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((BoolType, Id("x")(pos)), BoolLit(false)(pos))(pos),
            Read(Id("x")(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Bool, Set(KType.Int, KType.Char))(funcScope, pos))
        }
    }

    it should "detect type mismatches in free statements" in {
        val prog = Program(Nil, NonEmptyList.of(
            Free(BoolLit(false)(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Bool, Set(KType.Array(?, AnyDimension), KType.Pair(?, ?)))(funcScope, pos))
        }
    }

    it should "allow for string weakening" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((StringType, Id("x")(pos)), ArrayLit(List(CharLit('a')(pos)))(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Right(prog) => succeed
        }
    }

    it should "not allow for array covariance" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((ArrayType(CharType, 1)(pos), Id("x")(pos)), ArrayLit(List(CharLit('a')(pos)))(pos))(pos),
            Declaration((ArrayType(CharType, 2)(pos), Id("y")(pos)), ArrayLit(List(Id("x")(pos)))(pos))(pos),
            Declaration((ArrayType(StringType, 1)(pos), Id("z")(pos)), Id("y")(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Array(KType.Char, 2), Set(KType.Array(KType.Str, 1)))(funcScope, pos))
        }
    }

    it should "not allow for pair covariance" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((PairType(ArrayType(CharType, 1)(pos), ArrayType(CharType, 1)(pos))(pos), Id("x")(pos)), PairLit)(pos),
            Declaration((PairType(StringType, StringType)(pos), Id("y")(pos)), Id("x")(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeMismatch(KType.Pair(KType.Array(KType.Char, 1), KType.Array(KType.Char, 1)), Set(KType.Pair(KType.Str, KType.Str)))(funcScope, pos))
        }
    }

    it should "return scope errors as well" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((IntType, Id("z")(pos)), Id("y")(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (VariableNotInScope("y", Nil)(funcScope, pos))
        }
    }

    it should "not allow for unknown types in read statements" in {
        val prog = Program(Nil, NonEmptyList.of(
            Read(Id("x")(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeCannotBeInfered(funcScope, pos))
        }
    }

    it should "not allow for unknown types assignments" in {
        val prog = Program(Nil, NonEmptyList.of(
            Declaration((PairType(IntType, IntType)(pos), Id("y")(pos)),
                         NewPair(IntLit(0)(pos), IntLit(0)(pos))(pos))(pos),
            Declaration((PairType(Pair, Pair)(pos), Id("x")(pos)),
                         NewPair(Id("y")(pos), Id("y")(pos))(pos))(pos),
            Declaration((PairType(Pair, Pair)(pos), Id("z")(pos)),
                         NewPair(Id("y")(pos), Id("y")(pos))(pos))(pos),
            Assignment(Fst(Fst(Id("x")(pos))(pos))(pos), Snd(Snd(Id("z")(pos))(pos))(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (TypeCannotBeInfered(funcScope, pos))
        }
    }

    it should "detect mismatches in the number of arguments" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), List((IntType, Id("x")(pos))), NonEmptyList.of(Return(Id("x")(pos))(pos)))(pos),
        ), NonEmptyList.of(
            Declaration((IntType, Id("x")(pos)), Call(Id("f")(pos), Nil)(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (NumberArgumentsMismatch(0, 1)(funcScope, pos))
        }
    }

    it should "detect return statements in the main body" in {
        val prog = Program(Nil, NonEmptyList.of(
            Return(IntLit(0)(pos))(pos),
        ))(pos)
        
        inside(semantics.check(prog)) {
            case Left(errs) =>
                errs.toList should contain (ReturnInMainBody(funcScope, pos))
        }
    }
}
