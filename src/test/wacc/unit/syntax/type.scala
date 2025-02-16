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

/** Helper function to parse a type. */
private def parseType(s: String): Either[WaccError, Program] = parser.parse(s"begin $s x = 0 end").toEither

/** Tests the parsing of types. */
class TypeParserTests extends AnyFlatSpec {
    "Base types" should "be able to parse int" in {
        inside(parseType("int")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((IntType, Id("x")), IntLit(0)), _
            ))) => succeed
        }
    }
    they should "be able to parse bool" in {
        inside(parseType("bool")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((BoolType, Id("x")), IntLit(0)), _
            ))) => succeed
        }
    }
    they should "be able to parse char" in {
        inside(parseType("char")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((CharType, Id("x")), IntLit(0)), _
            ))) => succeed
        }
    }
    they should "be able to parse string" in {
        inside(parseType("string")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((StringType, Id("x")), IntLit(0)), _
            ))) => succeed
        }
    }

    "Array types" should "be able to parse single dimensions" in {
        inside(parseProg("int[] x = [0]")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((ArrayType(IntType, 1), Id("x")), ArrayLit(List(IntLit(0)))), _
            ))) => succeed
        }
    }
    they should "be able to parse multiple dimensions" in {
        inside(parseProg("int[][] x = [p]")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((ArrayType(IntType, 2), Id("x")), ArrayLit(List(Id(p)))), _
            ))) => succeed
        }
        inside(parseProg("int[][][] x = [p]")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((ArrayType(IntType, 3), Id("x")), ArrayLit((List(Id(p))))), _
            ))) => succeed
        }
    }

    "Pair types" should "be able to parse pairs of base types" in {
        inside(parseType("pair(int, bool)")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((PairType(IntType, BoolType), Id("x")), IntLit(0)), _
            ))) => succeed
        }
    }
    they should "be able to parse pairs of array types" in {
        inside(parseType("pair(int[], bool[])")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((PairType(ArrayType(IntType, 1), ArrayType(BoolType, 1)), Id("x")), IntLit(0)), _
            ))) => succeed
        }
    }
        they should "be able to parse pairs of pair types" in {
        inside(parseType("pair(pair, pair)")) {
            case Right(Program(Nil, NonEmptyList(
                Declaration((PairType(Pair, Pair), Id("x")), IntLit(0)), _
            ))) => succeed
        }
    }
}
