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

/** Helper function to parse a program. */
private def parseProg(s: String): Either[WaccError, Program] = parser.parse(s"begin $s end").toEither
/** Helper function to parse an expression. */
private def parseExpr(s: String): Either[WaccError, Program] = parseProg(s"return $s")
/** Helper function to parse a statement. */
private def parseStmt(s: String): Either[WaccError, Program] = parseProg(s)

/** Tests the parsing of expressions. */
class ExprParserTests extends AnyFlatSpec {
    "Integer Literals" should "be able to parse numbers" in {
        inside(parseExpr("123")) {
            case Right(Program(Nil, NonEmptyList(
                Return(IntLit(123)), _
            ))) => succeed
        }
    }
    they should "be able to parse signed numbers" in {
        inside(parseExpr("-123")) {
            case Right(Program(Nil, NonEmptyList(
                Return(IntLit(-123)), _
            ))) => succeed
        }
        inside(parseExpr("+123")) {
            case Right(Program(Nil, NonEmptyList(
                Return(IntLit(123)), _
            ))) => succeed
        }
    }
    they should "be able to parse the integer bounds" in {
        inside(parseExpr(s"${Int.MinValue}")) {
            case Right(Program(Nil, NonEmptyList(
                Return(IntLit(Int.MinValue)), _
            ))) => succeed
        }
        inside(parseExpr(s"${Int.MaxValue}")) {
            case Right(Program(Nil, NonEmptyList(
                Return(IntLit(Int.MaxValue)), _
            ))) => succeed
        }
    }

    "Boolean Literals" should "be able to parse true" in {
        inside(parseExpr("true")) {
            case Right(Program(Nil, NonEmptyList(
                Return(BoolLit(true)), _
            ))) => succeed
        }
    }
    they should "be able to parse false" in {
        inside(parseExpr("false")) {
            case Right(Program(Nil, NonEmptyList(
                Return(BoolLit(false)), _
            ))) => succeed
        }
    }

    "Character Literals" should "be able to parse chatacters" in {
        inside(parseExpr("'a'")) {
            case Right(Program(Nil, NonEmptyList(
                Return(CharLit('a')), _
            ))) => succeed
        }
    }
    they should "be able to parse escape characters" in {
        inside(parseExpr("'\\0'")) {
            case Right(Program(Nil, NonEmptyList(
                Return(CharLit('\u0000')), _
            ))) => succeed
        }
        inside(parseExpr("'\\b'")) {
            case Right(Program(Nil, NonEmptyList(
                Return(CharLit('\b')), _
            ))) => succeed
        }
        inside(parseExpr("'\\t'")) {
            case Right(Program(Nil, NonEmptyList(
                Return(CharLit('\t')), _
            ))) => succeed
        }
        inside(parseExpr("'\\n'")) {
            case Right(Program(Nil, NonEmptyList(
                Return(CharLit('\n')), _
            ))) => succeed
        }
        inside(parseExpr("'\\f'")) {
            case Right(Program(Nil, NonEmptyList(
                Return(CharLit('\f')), _
            ))) => succeed
        }
        inside(parseExpr("'\\r'")) {
            case Right(Program(Nil, NonEmptyList(
                Return(CharLit('\r')), _
            ))) => succeed
        }
        inside(parseExpr("'\\\''")) {
            case Right(Program(Nil, NonEmptyList(
                Return(CharLit('\'')), _
            ))) => succeed
        }
        inside(parseExpr("'\\\"'")) {
            case Right(Program(Nil, NonEmptyList(
                Return(CharLit('\"')), _
            ))) => succeed
        }
        inside(parseExpr("'\\\\'")) {
            case Right(Program(Nil, NonEmptyList(
                Return(CharLit('\\')), _
            ))) => succeed
        }
    }

    "String Literals" should "be able to parse strings" in {
        inside(parseExpr("\"wacc quack\"")) {
            case Right(Program(Nil, NonEmptyList(
                Return(StrLit("wacc quack")), _
            ))) => succeed
        }
    }

    "Pair Literals" should "be able to parse null" in {
        inside(parseExpr("null")) {
            case Right(Program(Nil, NonEmptyList(
                Return(PairLit), _
            ))) => succeed
        }
    }

    "Identifiers" should "be composed of letters" in {
        inside(parseExpr("duck")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Id("duck")), _
            ))) => succeed
        }
    }
    they should "be composed of letters, numbers, and underscores" in {
        inside(parseExpr("duck_1")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Id("duck_1")), _
            ))) => succeed
        }
    }
    they should "allow underscores as a first character" in {
        inside(parseExpr("_duck")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Id("_duck")), _
            ))) => succeed
        }
    }

    "Comments" should "be ignored" in {
        inside(parseExpr("0\n# wacc quack\n")) {
            case Right(Program(Nil, NonEmptyList(
                Return(IntLit(0)), _
            ))) => succeed
        }
    }
    they should "be allowed to be on the same line as code" in {
        inside(parseExpr("0#\n")) {
            case Right(Program(Nil, NonEmptyList(
                Return(IntLit(0)), _
            ))) => succeed
        }
    }

    "Unary operators" should "be able to parse '!'" in {
        inside(parseExpr("!true")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Not(BoolLit(true))), _
            ))) => succeed
        }
    }
    they should "be able to parse '-'" in {
        inside(parseExpr("-(123)")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Neg(ParensExpr(IntLit(123)))), _
            ))) => succeed
        }
    }
    they should "be able to parse 'len'" in {
        inside(parseExpr("len \"hello\"")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Len(StrLit("hello"))), _
            ))) => succeed
        }
    }
    they should "be able to parse 'ord'" in {
        inside(parseExpr("ord 'a'")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Ord(CharLit('a'))), _
            ))) => succeed
        }
    }
    they should "be able to parse 'chr'" in {
        inside(parseExpr("chr 97")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Chr(IntLit(97))), _
            ))) => succeed
        }
    }


    "Binary operators" should "be able to parse 'or'" in {
        inside(parseExpr("true || false")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Or(BoolLit(true), BoolLit(false))), _
            ))) => succeed
        }
    }
    they should "be able to parse 'and'" in {
        inside(parseExpr("true && false")) {
            case Right(Program(Nil, NonEmptyList(
                Return(And(BoolLit(true), BoolLit(false))), _
            ))) => succeed
        }
    }
    they should "be able to parse '=='" in {
        inside(parseExpr("1 == 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Equal(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "be able to parse '!='" in {
        inside(parseExpr("1 != 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(NotEqual(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "be able to parse '>'" in {
        inside(parseExpr("1 > 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Greater(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "be able to parse '>='" in {
        inside(parseExpr("1 >= 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(GreaterEq(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "be able to parse '<'" in {
        inside(parseExpr("1 < 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Less(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "be able to parse '<='" in {
        inside(parseExpr("1 <= 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(LessEq(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "be able to parse '+'" in {
        inside(parseExpr("1 + 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Add(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "be able to parse '-'" in {
        inside(parseExpr("1 - 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Sub(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "be able to parse '*'" in {
        inside(parseExpr("1 * 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Mul(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "be able to parse '/'" in {
        inside(parseExpr("1 / 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Div(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "be able to parse '%'" in {
        inside(parseExpr("1 % 2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Mod(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }

    "Expressions" should "parse the correct precedence" in {
        inside(parseExpr("1 + 2 * 3")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Add(IntLit(1), Mul(IntLit(2), IntLit(3)))), _
            ))) => succeed
        }
        inside(parseExpr("1 * 2 + 3")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Add(Mul(IntLit(1), IntLit(2)), IntLit(3))), _
            ))) => succeed
        }
        inside(parseExpr("-2 * 3")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Mul(IntLit(-2), IntLit(3))), _
            ))) => succeed
        }
        inside(parseExpr("len \"hello\" + 1")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Add(Len(StrLit("hello")), IntLit(1))), _
            ))) => succeed
        }
        inside(parseExpr("true == 2 >= 1")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Equal(BoolLit(true), GreaterEq(IntLit(2), IntLit(1)))), _
            ))) => succeed
        }
        inside(parseExpr("true && false == true")) {
            case Right(Program(Nil, NonEmptyList(
                Return(And(BoolLit(true), Equal(BoolLit(false), BoolLit(true)))), _
            ))) => succeed
        }
        inside(parseExpr("true || false && true")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Or(BoolLit(true), And(BoolLit(false), BoolLit(true)))), _
            ))) => succeed
        }
    }
    they should "handle all operator precedence levels" in {
        inside(parseExpr("!true && 1 + 2 * 3 > 4")) {
            case Right(Program(Nil, NonEmptyList(Return(
                And(Not(BoolLit(true)),
                    Greater(
                        Add(IntLit(1), Mul(IntLit(2), IntLit(3))),
                        IntLit(4)
                    )
                )
            ), _
            ))) => succeed
        }
    }
    they should "parse with parentheses" in {
        inside(parseExpr("(1 + 2) * 3")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Mul(ParensExpr(Add(IntLit(1), IntLit(2))), IntLit(3))), _
            ))) => succeed
        }
        inside(parseExpr("1 + (2 * 3)")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Add(IntLit(1), ParensExpr(Mul(IntLit(2), IntLit(3))))), _
            ))) => succeed
        }
        inside(parseExpr("-(2 * 3)")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Neg(ParensExpr(Mul(IntLit(2), IntLit(3))))), _
            ))) => succeed
        }
    }
    they should "handle nested parentheses" in {
        inside(parseExpr("(((1)))")) {
            case Right(Program(Nil, NonEmptyList(
                Return(ParensExpr(ParensExpr(ParensExpr(IntLit(1))))), _
            ))) => succeed
        }
    }
    they should "handle left associative operators correctly" in {
        inside(parseExpr("1 - 2 - 3")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Sub(Sub(IntLit(1), IntLit(2)), IntLit(3))), _
            ))) => succeed
        }
        inside(parseExpr("1 * 2 * 3")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Mul(Mul(IntLit(1), IntLit(2)), IntLit(3))), _
            ))) => succeed
        }
    }
    they should "handle right associative operators correctly" in {
        inside(parseExpr("true || false || true")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Or(BoolLit(true), Or(BoolLit(false), BoolLit(true)))), _
            ))) => succeed
        }
        inside(parseExpr("true && false && true")) {
            case Right(Program(Nil, NonEmptyList(
                Return(And(BoolLit(true), And(BoolLit(false), BoolLit(true)))), _
            ))) => succeed
        }
    }
    they should "handle unary operators with binary operators" in {
        inside(parseExpr("-1 + -2")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Add(IntLit(-1), IntLit(-2))), _
            ))) => succeed
        }
    }
    they should "be insensitive to whitespace" in {
        inside(parseExpr("1+-2*3")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Add(IntLit(1), Mul(IntLit(-2), IntLit(3)))), _
            ))) => succeed
        }
    }

    "Array elements" should "be able to parse a single index" in {
        inside(parseExpr("arr[1]")) {
            case Right(Program(Nil, NonEmptyList(
                Return(ArrayElem(Id("arr"), List(IntLit(1)))), _
            ))) => succeed
        }
    }
    they should "be able to parse multiple indices" in {
        inside(parseExpr("arr[1][2]")) {
            case Right(Program(Nil, NonEmptyList(
                Return(ArrayElem(Id("arr"), List(IntLit(1), IntLit(2)))), _
            ))) => succeed
        }
    }
    
    "LValues" should "parse simple identifiers" in {
        inside(parseExpr("x")) {
            case Right(Program(Nil, NonEmptyList(
                Return(Id("x")), _
            ))) => succeed
        }
    }
    they should "parse array elements with single index" in {
        inside(parseStmt("arr[1] = 2")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(ArrayElem(Id("arr"), List(IntLit(1))), IntLit(2)), _
            ))) => succeed
        }
    }
    they should "parse array elements with multiple indices" in {
        inside(parseStmt("arr[1][2] = 7")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(ArrayElem(Id("arr"), List(IntLit(1), IntLit(2))), IntLit(7)), _
            ))) => succeed
        }
    }
    they should "parse first pair elements" in {
        inside(parseStmt("fst p = 2")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Fst(Id("p")), IntLit(2)), _
            ))) => succeed
        }
    }
    they should "parse second pair elements" in {
        inside(parseStmt("snd p = 3")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Snd(Id("p")), IntLit(3)), _
            ))) => succeed
        }
    }
    they should "parse nested pair elements" in {
        inside(parseStmt("fst snd p = 2")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Fst(Snd(Id("p"))), IntLit(2)), _
            ))) => succeed
        }
    }
    they should "parse nested pair elements with array elements using fst" in {
        inside(parseStmt("fst arr[1] = 2")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Fst(ArrayElem(Id("arr"), List(IntLit(1)))), IntLit(2)), _
            ))) => succeed
        }
    }
    they should "parse nested pair elements with array elements using snd" in {
        inside(parseStmt("snd arr[1] = 2")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Snd(ArrayElem(Id("arr"), List(IntLit(1)))), IntLit(2)), _
            ))) => succeed
        }
    }

    "RValues" should "parse array literals" in {
        inside(parseStmt("x = [1, 2, 3]")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), ArrayLit(List(IntLit(1), IntLit(2), IntLit(3)))), _
            ))) => succeed
        }
    }
    they should "parse empty array literals" in {
        inside(parseStmt("x = []")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), ArrayLit(Nil)), _
            ))) => succeed
        }
    }
    they should "parse newpair expressions" in {
        inside(parseStmt("x = newpair(1, 2)")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), NewPair(IntLit(1), IntLit(2))), _
            ))) => succeed
        }
    }
    they should "parse function calls" in {
        inside(parseStmt("x = call f()")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), Call(Id("f"), Nil)), _
            ))) => succeed
        }
    }
    they should "parse function calls with arguments" in {
        inside(parseStmt("x = call f(1, true, 'c')")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), Call(Id("f"), List(
                    IntLit(1), BoolLit(true), CharLit('c')
                ))), _
            ))) => succeed
        }
    }
    they should "parse pair elements" in {
        inside(parseStmt("x = fst p")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), Fst(Id("p"))), _
            ))) => succeed
        }
    }
    they should "parse second pair elements" in {
        inside(parseStmt("x = snd p")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), Snd(Id("p"))), _
            ))) => succeed
        }
    }
    they should "parse nested pair elements" in {
        inside(parseStmt("x = fst snd p")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), Fst(Snd(Id("p")))), _
            ))) => succeed
        }
    }
    they should "parse nested pair elements with array elements using fst" in {
        inside(parseStmt("x = fst arr[1]")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), Fst(ArrayElem(Id("arr"), List(IntLit(1))))), _
            ))) => succeed
        }
    }
    they should "parse nested pair elements with array elements using snd" in {
        inside(parseStmt("x = snd arr[1]")) {
            case Right(Program(Nil, NonEmptyList(
                Assignment(Id("x"), Snd(ArrayElem(Id("arr"), List(IntLit(1))))), _
            ))) => succeed
        }
    }
}
