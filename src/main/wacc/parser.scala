package wacc
import parsley.{Parsley, Result}
import parsley.Parsley.{atomic, many, notFollowedBy}
import parsley.character.digit
import parsley.combinator.countMany
import parsley.expr.{precedence, SOps, InfixL, InfixR, InfixN, Prefix, Atoms}

import lexer.*
import implicits.implicitSymbol

import syntax.*
import exprs.*
import prog.*
import stmts.*
import types.*

/** Formulates the grammar rules the parser should follow. */
object parser {
    /** Top-level parser. */
    def parse(input: String): Result[String, Program] = parser.parse(input)
    private val parser = fully(program)
    
    /** Expressios. */
    private lazy val idOrArrayElem: Parsley[Atom & LValue] = 
        IdOrArrayElem(Id(identifier), many(brackets(expr)))
    
    private lazy val atom: Parsley[Atom] = 
        ( IntLit(integer)
        | BoolLit("true".as(true) | "false".as(false)) 
        | CharLit(character)
        | StrLit(string)
        | PairLit.from("null")
        | idOrArrayElem
        | ParensExpr(parens(expr))
        )

    // operator precedence hierarchy
    private lazy val expr: Parsley[Expr] = precedence {
        SOps(InfixR)(Or       from "||") +:
        SOps(InfixR)(And      from "&&") +:
        SOps(InfixN)(Equal    from "==",  NotEqual     from "!=") +:
        SOps(InfixN)(Greater  from ">",   GreaterEqual from ">=",
                     Less     from "<",   LessEqual    from "<=") +:
        SOps(InfixL)(Add      from "+",   Sub          from "-") +:
        SOps(InfixL)(Mul      from "*",
                     Div      from "/",   Mod          from "%") +:
        SOps(Prefix)(Neg      from atomic("-" <~ notFollowedBy(digit)),
                     Not      from "!",   Len          from "len",
                     Ord      from "ord", Chr          from "chr") +:
        Atoms(atom)
    }

    private lazy val pairElem: Parsley[PairElem] = 
        ( Fst("fst" ~> lvalue) 
        | Snd("snd" ~> lvalue)
        )
    
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

    /** Types. */
    private lazy val baseType: Parsley[IdType & PairElemType] =
        ( IntType.from   ("int")
        | BoolType.from  ("bool")
        | CharType.from  ("char")
        | StringType.from("string")
        )
    private lazy val pairElemType: Parsley[PairElemType] =
        ( BaseArrayType(baseType, countMany("[" <~> "]"))
        | Pair.from("pair")
        )
    private lazy val pairType: Parsley[IdType] =
        PairType("pair" ~> "(" ~> pairElemType, "," ~> pairElemType <~ ")")

    private lazy val ty: Parsley[IdType] =
        BaseArraPairType(baseType | pairType, countMany("[" <~> "]"))
    
    private lazy val idType: Parsley[TypeId] =
        (ty <~> Id(identifier))

    /** Statements. */
    private lazy val simpleStmt: Parsley[Stmt] = 
        ( Skip.from  ("skip")
        | Declaration(idType, "=" ~> rvalue)
        | Assignment (lvalue, "=" ~> rvalue)
        | Read       ("read"      ~> lvalue)
        | Print      ("print"     ~> expr)
        | Println    ("println"   ~> expr)
        | Free       ("free"      ~> expr)
        | Return     ("return"    ~> expr)
        | Exit       ("exit"      ~> expr)
        )
        
    private lazy val compoundStmt: Parsley[Stmt] = 
        ( If   ("if"    ~> expr, "then" ~> stmts, "else" ~> stmts <~ "fi")
        | While("while" ~> expr, "do"   ~> stmts                  <~ "done")
        | Block("begin"                 ~> stmts                  <~ "end")
        )
        
    private lazy val stmts: Parsley[List[Stmt]] =
        semiSep1(simpleStmt | compoundStmt)

    /** Functions and programs. */
    private lazy val function: Parsley[Function] =
        Function(atomic(idType <~ "("), commaSep(idType) <~ ")", "is" ~> stmts <~ "end")

    private lazy val program: Parsley[Program] =
        Program("begin" ~> many(function), stmts <~ "end")
}
