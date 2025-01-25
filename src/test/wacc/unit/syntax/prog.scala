package wacc.unit

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inside.*

import wacc.parser
import wacc.syntax.*
import exprs.*
import prog.*
import stmts.*
import types.*

private def parseProg(s: String): Either[String, Program] = parser.parse(s"begin $s end").toEither
private def parseFunc(tid: String, params: String, body: String, stmts: String): Either[String, Program] 
    = parseProg(s"$tid ($params) is $body end $stmts")


class ProgTest extends AnyFlatSpec {
    "a program" should "parse a function" in {
        inside(parseFunc("int f","", "return 0", "skip")) {
            case Right(Program(
                List(Function((IntType, Id("f")), Nil, List(Return(IntLit(0))))),
                List(Skip)
                )) => succeed
        }
    }

    it should "parse functions with parameters" in {
        inside(parseFunc("int add", "int x, int y", "return x + y", "skip")) {
            case Right(Program(
                List(Function(
                    (IntType, Id("add")),
                    List((IntType, Id("x")), (IntType, Id("y"))),
                    List(Return(Add(Id("x"), Id("y"))))
                )),
                List(Skip)
            )) => succeed
        }
    }

    it should "parse multiple functions" in {
        inside(parseProg("""
            int f() is return 1 end
            bool g() is return true end
            skip 
            """)
        ) {
            case Right(Program(
                List(
                    Function((IntType, Id("f")), Nil, List(Return(IntLit(1)))),
                    Function((BoolType, Id("g")), Nil, List(Return(BoolLit(true))))
                ),
                List(Skip)
            )) => succeed
        }
    }

    it should "parse nested scopes in functions" in {
        inside(parseProg(
            """
            int f() is
                int x = 1;
                return x
            end
            skip
            """   
        )
        ) {
            case Right(Program(
                List(Function((IntType, Id("f")), Nil,
                    (List(
                        Declaration((IntType, Id("x")), IntLit(1)),
                        Return(Id("x"))
                    ))
                )),
                List(Skip)
            )) => succeed
        }
    }


}