package wacc.unit

import cats.data.NonEmptyList
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inside.*

import wacc.error.errors.*
import wacc.frontend.parser
import wacc.syntax.*
import exprs.*
import prog.*
import stmts.*
import types.*

/** Helper function to parse a statement. */
private def parseProg(s: String): Either[WaccError, Program] = parser.parse(s"begin $s end").toEither

/** Tests the parsing of statements. */
class StmtParserTests extends AnyFlatSpec {
    "Skip statement" should "parse successfully" in {
        inside(parseProg("skip; skip")) {
            case Right(Program(Nil, NonEmptyList(Skip, Skip :: _))) => succeed
        }
    }

    "Variable declarations" should "parse basic types" in {
        inside(parseProg("int x = 42")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((IntType, Id("x")), IntLit(42)), _
            ))) => succeed
        }
    }
    they should "parse array types" in {
        inside(parseProg("int[] arr = [1, 2, 3]")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((ArrayType(IntType, 1), Id("arr")), 
                           ArrayLit(List(IntLit(1), IntLit(2), IntLit(3)))), _
            ))) => succeed
        }
    }
    they should "parse multi-dimensional arrays" in {
        inside(parseProg("int[][] arr = [a, b]")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((ArrayType(IntType, 2), Id("arr")), 
                           ArrayLit(List(Id("a"), Id("b")))), _
            ))) => succeed
        }
    }    
    they should "parse pair types" in {
        inside(parseStmt("pair(int, bool) p = newpair(1, true)")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((PairType(IntType, BoolType), Id("p")), 
                           NewPair(IntLit(1), BoolLit(true))), _
            ))) => succeed
        }
    }
    they should "parse pairs with expressions" in {
        inside(parseStmt("pair(int, bool) p = newpair(1 + 2, true)")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((PairType(IntType, BoolType), Id("p")), 
                           NewPair(Add(IntLit(1), IntLit(2)), BoolLit(true))), _
            ))) => succeed
        }
    }
    they should "parse pairs with variables" in {
        inside(parseStmt("pair(int, bool) p = newpair(x, true)")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((PairType(IntType, BoolType), Id("p")), 
                           NewPair(Id("x"), BoolLit(true))), _
            ))) => succeed
        }
    }
    they should "parse pairs with expressions and variables" in {
        inside(parseStmt("pair(int, bool) p = newpair(x + 1, true)")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((PairType(IntType, BoolType), Id("p")), 
                           NewPair(Add(Id("x"), IntLit(1)), BoolLit(true))), _
            ))) => succeed
        }
    }
    they should "parse erased pairs with expressions" in {
        inside(parseStmt("pair(int, pair) p = newpair(1, 0)")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((PairType(IntType, Pair), Id("p")),
                           NewPair(IntLit(1), IntLit(0))), _
                ))) => succeed
        }
    }

    "Assignments" should "parse simple assignments" in {
        inside(parseStmt("x = 42")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), IntLit(42)), _
            ))) => succeed
        }
    }
    they should "parse array assignments" in {
        inside(parseStmt("arr[1] = 42")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(ArrayElem(Id("arr"), List(IntLit(1))), IntLit(42)), _
            ))) => succeed
        }
    }
    they should "parse pair assignments" in {
        inside(parseStmt("fst p = 42")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Fst(Id("p")), IntLit(42)), _
            ))) => succeed
        }
    }

    "IO statements" should "parse read" in {
        inside(parseStmt("read x")) {
            case Right(Program(Nil, NonEmptyList(
                Read(Id("x")), _
            ))) => succeed
        }
    }
    they should "parse print" in {
        inside(parseStmt("print 42")) {
            case Right(Program(Nil, NonEmptyList(
                Print(IntLit(42)), _
            ))) => succeed
        }
    }
    they should "parse println" in {
        inside(parseStmt("println \"hello\"")) {
            case Right(Program(Nil, NonEmptyList(
                Println(StrLit("hello")), _
            ))) => succeed
        }
    }

    "Control flow" should "parse if statements" in {
        inside(parseStmt("if true then x = 1 else x = 2 fi")) {
            case Right(Program(Nil, NonEmptyList(
                If(BoolLit(true),
                   NonEmptyList(Assignment(Id("x"), IntLit(1)), _),
                   NonEmptyList(Assignment(Id("x"), IntLit(2)), _)), _
            ))) => succeed
        }
    }
    they should "parse while loops" in {
        inside(parseStmt("while true do x = x + 1 done")) {
            case Right(Program(Nil, NonEmptyList(
                While(BoolLit(true),
                      NonEmptyList(Assignment(Id("x"), 
                           Add(Id("x"), IntLit(1))), _)), _
            ))) => succeed
        }
    }

    "Blocks" should "parse begin end blocks" in {
        inside(parseStmt("begin skip end")) {
            case Right(Program(Nil, NonEmptyList(
                Block(NonEmptyList(Skip, _)), _
            ))) => succeed
        }
    }
    they should "parse multiple statements" in {
        inside(parseStmt("x = 1; y = 2")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), IntLit(1)),
                Assignment(Id("y"), IntLit(2)) :: _
            ))) => succeed
        }
    }
}
