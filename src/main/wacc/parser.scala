package wacc

import java.io.File
import parsley.{Parsley, Result, Failure}
import parsley.Parsley.{atomic, many, notFollowedBy}
import parsley.combinator.countMany
import parsley.expr.{precedence, SOps, InfixL, InfixR, InfixN, Prefix, Atoms}
import parsley.errors.combinator.*
import parsley.errors.patterns.{PreventativeErrors, VerifiedErrors}
import parsley.cats.combinator.*
import scala.util.{Success => TrySuccess, Failure => TryFailure}

import lexer.*
import implicits.implicitSymbol

import wacc.error.syntaxErrors.*
import wacc.error.errors.*
import syntax.*
import exprs.*
import prog.*
import stmts.*
import types.*

/** Formulates the grammar rules the parser should follow. */
object parser {
    import advancedErrors.*
    
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

    // operator precedence hierarchy TODO try dynamically form like testsuite    
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
        SOps(Prefix)(Neg       from atomic("-" <~ notFollowedBy(digit)),
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
        | Read       ("read"      ~> lvalue)
        | Print      ("print"     ~> expr)
        | Println    ("println"   ~> expr)
        | Free       ("free"      ~> expr)
        | Return     ("return"    ~> expr)
        | Exit       ("exit"      ~> expr)
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

    /** Top-level parser */
    private val parser = fully(program)

    def parse(f: File): Result[WaccError, Program] = parser.parseFile[WaccError](f) match {
        case TrySuccess(result) => result
        case TryFailure(_)      => Failure(IOError)
    }

    def parse(input: String): Result[WaccError, Program] = parser.parse[WaccError](input)
}

object advancedErrors {
    import parsley.Parsley.eof
    import parsley.errors.VanillaGen
    import parsley.quick.unit

    lazy val _emptyMain = unit.verifiedExplain("missing main program body")
    lazy val _beforeMain = unit.verifiedExplain("all program body and function declarations must be within `begin` and `end`")
    lazy val _afterMain = eof
        | ";".verifiedExplain("semi-colons cannot follow the `end` of the program")
        | unit.verifiedExplain("all program body and function declarations must be within `begin` and `end`")

    lazy val _pointers = "*".preventWith(
        err = new VanillaGen[Unit] {
            override def reason(x: Unit) = Some("pointers are not supported by WACC")
            override def unexpected(x: Unit) = VanillaGen.NamedItem("pointer type")
        },
        labels = "identifier"
    )

    lazy val _func = "(".verifiedExplain("all functions must be declared at the top of the main block")
    lazy val _func_type = unit.verifiedExplain("function declaration has missing type")
    lazy val _func_expr = "(".preventativeExplain("function calls may not appear in expressions and must use `call`")
    lazy val _nested = "(".preventativeExplain("pair types may not be nested in WACC")
    lazy val _unclosed = unit.preventativeExplain("unclosed scope, function or main body")

    /** Helpers to improve errors. */
    def labelScope[A](p: Parsley[A]): Parsley[A] =
        p.explain("empty scopes are not allowed")

    def labelExpr[A](p: Parsley[A]): Parsley[A] =
        p.label("expression")
         .explain("expressions may start with: literals, identifier, unary operators, null, parantheses")
}
