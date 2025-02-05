package wacc.unit

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inside.*

import wacc.error.*
import wacc.semantics.*
import scoping.semanticTypes.*
import wacc.syntax.*
import bridges.*
import exprs.*
import prog.*
import stmts.*
import types.*

/** Tests the type checker for programs not well-typed. */
class TypeCheckerTests extends AnyFlatSpec {

    private val pos: Position = (0, 0)

    "Type checker" should "detect type mismatches in declarations" in {
        val prog = Program(Nil, List(
            Declaration((IntType, Id("x")(pos)), BoolLit(false)(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Int))(pos))
        }
    }

    it should "detect type mismatches in assignments" in {
        val prog = Program(Nil, List(
            Declaration((IntType, Id("x")(pos)), IntLit(0)(pos))(pos),
            Assignment(Id("x")(pos), BoolLit(false)(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Int))(pos))
        }
    }

    it should "detect type mismatches in return statements" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, List(Return(BoolLit(false)(pos))(pos)))(pos),
        ), List(
            Skip,
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Int))(pos))
        }
    }

    it should "detect type mismatches in arithmetic expressions" in {
        val prog = Program(Nil, List(
            Print(Add(IntLit(0)(pos), BoolLit(false)(pos))(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Int))(pos))
        }
    }

    it should "detect type mismatches in relational expressions" in {
        val prog = Program(Nil, List(
            Print(Less(BoolLit(false)(pos), BoolLit(false)(pos))(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Int, KType.Char))(pos))
        }
    }

    it should "detect type mismatches in logical expressions" in {
        val prog = Program(Nil, List(
            Print(And(BoolLit(false)(pos), IntLit(0)(pos))(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Int, Set(KType.Bool))(pos))
        }
    }

    it should "detect type mismatches in unary expressions" in {
        val prog = Program(Nil, List(
            Print(Not(IntLit(0)(pos))(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Int, Set(KType.Bool))(pos))
        }
    }

    it should "correctly identify expression returns" in {
        val prog = Program(Nil, List(
            Declaration((BoolType, Id("x")(pos)), Equal(IntLit(0)(pos), IntLit(0)(pos))(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Right(prog) => succeed
        }
    }

    it should "detect type mismatches in array expressions" in {
        val prog = Program(Nil, List(
            Declaration((ArrayType(IntType, 1)(pos), Id("ar")(pos)), ArrayLit(List(BoolLit(false)(pos)))(pos))(pos),
            Declaration((ArrayType(IntType, 1)(pos), Id("x")(pos)), Id("ar")(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Int))(pos))
        }
    }

    it should "detect type mismatches in pair expressions" in {
        val prog = Program(Nil, List(
            Declaration((PairType(IntType, CharType)(pos), Id("p")(pos)), NewPair(IntLit(0)(pos), BoolLit(false)(pos))(pos))(pos),
            Declaration((PairType(IntType, CharType)(pos), Id("x")(pos)), Id("p")(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Char))(pos))
        }
    }

    it should "detect type mismatches in pair extraction" in {
        val prog = Program(Nil, List(
            Declaration((PairType(IntType, CharType)(pos), Id("p")(pos)), NewPair(IntLit(0)(pos), CharLit('a')(pos))(pos))(pos),
            Declaration((IntType, Id("x")(pos)), Fst(Id("p")(pos))(pos))(pos),
            Declaration((IntType, Id("y")(pos)), Snd(Id("p")(pos))(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Char, Set(KType.Int))(pos))
        }
    }

    it should "detect type mismatches in function returns" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, List(Return(BoolLit(true)(pos))(pos)))(pos),
        ), List(
            Skip,
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Int))(pos))
        }
    }

    it should "detect type mismatches in function calls" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), Nil, List(Return(IntLit(0)(pos))(pos)))(pos),
        ), List(
            Declaration((BoolType, Id("x")(pos)), Call(Id("f")(pos), Nil)(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Int, Set(KType.Bool))(pos))
        }
    }

    it should "detect type mismatches in parameter types" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), List((IntType, Id("x")(pos))), List(Return(Id("x")(pos))(pos)))(pos),
        ), List(
            Declaration((IntType, Id("x")(pos)), Call(Id("f")(pos), List(BoolLit(false)(pos)))(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Int))(pos))
        }
    }

    it should "detect type mismatches in if statements" in {
        val prog = Program(Nil, List(
            If(IntLit(0)(pos), List(Skip), List(Skip))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Int, Set(KType.Bool))(pos))
        }
    }

    it should "detect type mismatches in read statements" in {
        val prog = Program(Nil, List(
            Declaration((BoolType, Id("x")(pos)), BoolLit(false)(pos))(pos),
            Read(Id("x")(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Int, KType.Char))(pos))
        }
    }

    it should "detect type mismatches in free statements" in {
        val prog = Program(Nil, List(
            Free(BoolLit(false)(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Bool, Set(KType.Array(?), KType.Pair(?, ?)))(pos))
        }
    }

    it should "allow for string weakening" in {
        val prog = Program(Nil, List(
            Declaration((StringType, Id("x")(pos)), ArrayLit(List(CharLit('a')(pos)))(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Right(prog) => succeed
        }
    }

    it should "not allow for array variance" in {
        val prog = Program(Nil, List(
            Declaration((ArrayType(CharType, 1)(pos), Id("x")(pos)), ArrayLit(List(CharLit('a')(pos)))(pos))(pos),
            Declaration((ArrayType(CharType, 2)(pos), Id("y")(pos)), ArrayLit(List(Id("x")(pos)))(pos))(pos),
            Declaration((ArrayType(StringType, 1)(pos), Id("z")(pos)), Id("y")(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Array(KType.Char), Set(KType.Str))(pos))
        }
    }

    it should "not allow for pair variance" in {
        val prog = Program(Nil, List(
            Declaration((ArrayType(CharType, 1)(pos), Id("x")(pos)), ArrayLit(List(CharLit('a')(pos)))(pos))(pos),
            Declaration((PairType(StringType, IntType)(pos), Id("y")(pos)), NewPair(Id("x")(pos), IntLit(0)(pos))(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeMismatch(KType.Pair(KType.Array(KType.Char), KType.Int), Set(KType.Pair(KType.Str, KType.Int)))(pos))
        }
    }

    it should "return scope errors as well" in {
        val prog = Program(Nil, List(
            Declaration((IntType, Id("z")(pos)), Id("y")(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (VariableNotInScope("y")(pos))
        }
    }

    it should "not allow for unknown types in read statements" in {
        val prog = Program(Nil, List(
            Read(Id("x")(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeCannotBeInfered(pos))
        }
    }

    it should "not allow for unknown types assignments" in {
        val prog = Program(Nil, List(
            Declaration((PairType(IntType, IntType)(pos), Id("y")(pos)),
                         NewPair(IntLit(0)(pos), IntLit(0)(pos))(pos))(pos),
            Declaration((PairType(Pair, Pair)(pos), Id("x")(pos)),
                         NewPair(Id("y")(pos), Id("y")(pos))(pos))(pos),
            Declaration((PairType(Pair, Pair)(pos), Id("z")(pos)),
                         NewPair(Id("y")(pos), Id("y")(pos))(pos))(pos),
            Assignment(Fst(Fst(Id("x")(pos))(pos))(pos), Snd(Snd(Id("z")(pos))(pos))(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (TypeCannotBeInfered(pos))
        }
    }

    it should "detect mismatches in the number of arguments" in {
        val prog = Program(List(
            Function((IntType, Id("f")(pos)), List((IntType, Id("x")(pos))), List(Return(Id("x")(pos))(pos)))(pos),
        ), List(
            Declaration((IntType, Id("x")(pos)), Call(Id("f")(pos), Nil)(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (NumberArgumentsMismatch(0, 1)(pos))
        }
    }

    it should "detect return statements in the main body" in {
        val prog = Program(Nil, List(
            Return(IntLit(0)(pos))(pos),
        ))(pos)
        
        inside(checkSemantics(prog)) {
            case Left(errs) =>
                errs should contain (ReturnInMainBody(pos))
        }
    }
}
