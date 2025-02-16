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

/** Helper function to parse a program. */
private def parseFunc(tid: String, params: String, body: String, stmts: String): Either[WaccError, Program] =
    parser.parse(s"begin $tid ($params) is $body end $stmts end").toEither

/** Tests the parsing of functions and complete programs. */
class ProgramParserTests extends AnyFlatSpec {
    "Program" should "parse a function" in {
        inside(parseFunc("int f","", "return 0", "skip")) {
            case Right(Program(
                List(Function((IntType, Id("f")), Array(), NonEmptyList(Return(IntLit(0)), _))),
                NonEmptyList(Skip, _)
            )) => succeed
        }
    }
    it should "parse functions with parameters" in {
        inside(parseFunc("int add", "int x, int y", "return x + y", "skip")) {
            case Right(Program(
                List(Function(
                    (IntType, Id("add")),
                    Array((IntType, Id("x")), (IntType, Id("y"))),
                    NonEmptyList(Return(Add(Id("x"), Id("y"))), _)
                )),
                NonEmptyList(Skip, _)
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
                    Function((IntType, Id("f")), Array(), NonEmptyList(Return(IntLit(1)), _)),
                    Function((BoolType, Id("g")), Array(), NonEmptyList(Return(BoolLit(true)), _))
                ),
                NonEmptyList(Skip, _)
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
                List(Function((IntType, Id("f")), Array(),
                    (NonEmptyList(
                        Declaration((IntType, Id("x")), IntLit(1)),
                        Return(Id("x")) :: _
                    ))
                )),
                NonEmptyList(Skip, _)
            )) => succeed
        }
    }
    it should "parse multiple nested scopes in functions" in {
        inside(parseProg(
            """
            int f() is
                int x = 1;
                int y = 2;
                return x + y
            end
            skip
            """   
        )
        ) {
            case Right(Program(
                List(Function((IntType, Id("f")), Array(),
                    (NonEmptyList(
                        Declaration((IntType, Id("x")), IntLit(1)),
                        Declaration((IntType, Id("y")), IntLit(2)) ::
                        Return(Add(Id("x"), Id("y"))) :: _
                    ))
                )),
                NonEmptyList(Skip, _)
            )) => succeed
        }
    }
    it should "parse multiple functions with nested scopes" in {
        inside(parseProg(
            """
            int f() is
                int x = 1;
                return x
            end
            bool g() is
                bool y = true;
                return y
            end
            skip
            """   
        )
        ) {
            case Right(Program(
                List(
                    Function((IntType, Id("f")), Array(),
                        (NonEmptyList(
                            Declaration((IntType, Id("x")), IntLit(1)),
                            Return(Id("x")) :: _
                        ))
                    ),
                    Function((BoolType, Id("g")), Array(),
                        (NonEmptyList(
                            Declaration((BoolType, Id("y")), BoolLit(true)),
                            Return(Id("y")) :: _
                        ))
                    )
                ),
                NonEmptyList(Skip, _)
            )) => succeed
        }
    }
    it should "parse multiple functions with nested scopes and parameters" in {
        inside(parseProg(
            """
            int f(int x) is
                return x
            end
            bool g(bool y) is
                return y
            end
            skip
            """   
        )
        ) {
            case Right(Program(
                List(
                    Function((IntType, Id("f")), Array((IntType, Id("x"))),
                        (NonEmptyList(
                            Return(Id("x")), _
                        ))
                    ),
                    Function((BoolType, Id("g")), Array((BoolType, Id("y"))),
                        (NonEmptyList(
                            Return(Id("y")), _
                        ))
                    )
                ),
                NonEmptyList(Skip, _)
            )) => succeed
        }
    }
    it should "parse multiple functions with nested scopes and parameters and expressions" in {
        inside(parseProg(
            """
            int f(int x) is
                return x + 1
            end
            bool g(bool y) is
                return !y
            end
            skip
            """   
        )
        ) {
            case Right(Program(
                List(
                    Function((IntType, Id("f")), Array((IntType, Id("x"))),
                        (NonEmptyList(
                            Return(Add(Id("x"), IntLit(1))), _
                        ))
                    ),
                    Function((BoolType, Id("g")), Array((BoolType, Id("y"))),
                        (NonEmptyList(
                            Return(Not(Id("y"))), _
                        ))
                    )
                ),
                NonEmptyList(Skip, _)
            )) => succeed
        }
    }
}
