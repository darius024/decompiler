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
                List(Function((IntType, Id("f")), Nil,
                    (List(
                        Declaration((IntType, Id("x")), IntLit(1)),
                        Declaration((IntType, Id("y")), IntLit(2)),
                        Return(Add(Id("x"), Id("y")))
                    ))
                )),
                List(Skip)
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
                    Function((IntType, Id("f")), Nil,
                        (List(
                            Declaration((IntType, Id("x")), IntLit(1)),
                            Return(Id("x"))
                        ))
                    ),
                    Function((BoolType, Id("g")), Nil,
                        (List(
                            Declaration((BoolType, Id("y")), BoolLit(true)),
                            Return(Id("y"))
                        ))
                    )
                ),
                List(Skip)
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
                    Function((IntType, Id("f")), List((IntType, Id("x"))),
                        (List(
                            Return(Id("x"))
                        ))
                    ),
                    Function((BoolType, Id("g")), List((BoolType, Id("y"))),
                        (List(
                            Return(Id("y"))
                        ))
                    )
                ),
                List(Skip)
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
                    Function((IntType, Id("f")), List((IntType, Id("x"))),
                        (List(
                            Return(Add(Id("x"), IntLit(1)))
                        ))
                    ),
                    Function((BoolType, Id("g")), List((BoolType, Id("y"))),
                        (List(
                            Return(Not(Id("y")))
                        ))
                    )
                ),
                List(Skip)
            )) => succeed
        }
    }
}