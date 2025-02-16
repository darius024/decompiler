package wacc.frontend

import java.io.File
import parsley.{Parsley, Result, Failure}
import parsley.Parsley.{atomic, many}
import parsley.combinator.countMany
import parsley.expr.{precedence, SOps, InfixL, InfixR, InfixN, Prefix, Atoms}
import parsley.cats.combinator.*
import scala.util.{Success => TrySuccess, Failure => TryFailure}

import lexer.*
import implicits.implicitSymbol

import wacc.error.advancedErrors.*
import wacc.error.syntaxErrors.*
import wacc.error.errors.*
import wacc.syntax.*
import exprs.*
import prog.*
import stmts.*
import types.*

/** Formulates the grammar rules the parser should follow. */
object parser {
    
    // expressions
    
    private lazy val atom: Parsley[Atom] = 
        ( IntLit(integer)
        | BoolLit(boolean) 
        | CharLit(character)
        | StrLit(string)
        | PairLit.from("null")
        | idOrArrayElem <~ _func_expr
        | ParensExpr(parens(expr))
        )

    private lazy val expr: Parsley[Expr] = labelExpr(precedence {
        SOps(InfixR)(Or        from "||")  +:
        SOps(InfixR)(And       from "&&")  +:
        SOps(InfixN)(Equal     from "==", 
                     NotEqual  from "!=")  +:
        SOps(InfixN)(Greater   from ">",
                     GreaterEq from ">=",
                     Less      from "<",
                     LessEq    from "<=")  +:
        SOps(InfixL)(Add       from "+",
                     Sub       from "-")   +:
        SOps(InfixL)(Mul       from "*",
                     Div       from "/",
                     Mod       from "%")   +:
        SOps(Prefix)(Neg       from atomic(minusExpr),
                     Not       from "!",
                     Len       from "len",
                     Ord       from "ord",
                     Chr       from "chr") +:
        Atoms(atom)
    })
    
    private lazy val pairElem: Parsley[PairElem] = 
        ( Fst("fst" ~> lvalue) 
        | Snd("snd" ~> lvalue)
        )

    private lazy val idOrArrayElem: Parsley[Atom & LValue] = 
        IdOrArrayElem(Id(identifier), many(brackets(expr)))
    
    private lazy val lvalue: Parsley[LValue] = 
        ( idOrArrayElem
        | pairElem
        )

    private lazy val rvalue: Parsley[RValue] =
        ( expr
        | ArrayLit(brackets(commaSep(expr)))
        | NewPair ("newpair" ~> "(" ~> expr, "," ~> expr <~ ")")
        | pairElem
        | Call    ("call"    ~> Id(identifier), parens(commaSep(expr)))
        )

    // types

    private lazy val baseType: Parsley[IdType & PairElemType] =
        ( IntType.from   ("int")
        | BoolType.from  ("bool")
        | CharType.from  ("char")
        | StringType.from("string")
        )

    private lazy val pairElemType: Parsley[PairElemType] =
        ( BaseArrayType(baseType, countMany("[" <~> "]"))
        | Pair.from("pair") <~ _nested
        )

    private lazy val pairType: Parsley[IdType] =
        PairType("pair" ~> "(" ~> pairElemType, "," ~> pairElemType <~ ")")

    private lazy val ty: Parsley[IdType] =
        BaseArraPairType(baseType | pairType, countMany("[" <~> "]"))
    
    private lazy val idType: Parsley[TypeId] =
        (ty <~> (_pointers ~> Id(identifier)))

    // statements

    private lazy val simpleStmt: Parsley[Stmt] =
        ( Skip.from  ("skip")
        | Declaration(idType, ("=" ~> rvalue) | _func)
        | Assignment (lvalue,  "=" ~> rvalue)
        | Read       ("read"       ~> lvalue)
        | Print      ("print"      ~> expr)
        | Println    ("println"    ~> expr)
        | Free       ("free"       ~> expr)
        | Return     ("return"     ~> expr)
        | Exit       ("exit"       ~> expr)
        )
        
    private lazy val compoundStmt: Parsley[Stmt] = 
        ( If   ("if"    ~> expr, "then" ~> stmts, "else" ~> stmts <~ "fi"  )
        | While("while" ~> expr, "do"   ~> stmts                  <~ "done")
        | Block("begin"                 ~> stmts                  <~ ("end" | _unclosed))
        )

    // functions and programs

    private lazy val stmts: Parsley[StmtList] =
        labelScope(sepBy1(simpleStmt | compoundStmt, ";"))

    private lazy val function: Parsley[Function] =
        Function(atomic(idType <~ "("), commaSep(idType) <~ ")", "is" ~> stmts <~ "end")

    private lazy val program: Parsley[Program] =
        Program(("begin" | _beforeMain) ~> many(function), (stmts | _emptyMain) <~ ("end" <~ _afterMain))

    // top-level parsers
    
    private val parser = fully(program)

    def parse(f: File): Result[WaccError, Program] = parser.parseFile[WaccError](f) match {
        case TrySuccess(result) => result
        case TryFailure(_)      => Failure(IOError)
    }

    def parse(input: String): Result[WaccError, Program] = parser.parse[WaccError](input)
}
