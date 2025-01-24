package wacc
import parsley.{Parsley, Result}
import parsley.Parsley.{atomic, many, lookAhead, pure}
import parsley.combinator.countSome
import parsley.expr.{precedence, SOps, InfixL, InfixR, InfixN, Prefix, Atoms}

import lexer.*
import implicits.implicitSymbol

import syntax.*
import exprs.*
import prog.*
import stmts.*
import types.*

object parser {
    // ========== Top Level Parser ==========
    def parse(input: String): Result[String, Program] = parser.parse(input)
    private val parser = fully(program)
    
    // ========== Atomic Expressions ==========
    // <atom> ::= 'null' | 'true' | 'false' | <int-lit> | <char-lit> | <str-lit>
    //          | <ident> '[' <expr> ']' | '(' <expr> ')'
    private lazy val atom: Parsley[Atom] = {
        "null".as(PairLit)
        <|> BoolLit("true".as(true) | "false".as(false)) 
        <|> IntLit(integer | "-" ~> integer)
        <|> CharLit(character)
        <|> StrLit(string)
        <|> IdOrArrayElem(Id(identifier), many(brackets(expr)))
        <|> ParensExpr(parens(expr))
    }

    private lazy val literals = Atoms(atom)

    // ========== Expression Parser ==========
    // Implements operator precedence using Parsley's precedence combinator
    // Higher precedence operators appear lower in the list
    private lazy val expr: Parsley[Expr] = precedence {
        SOps(InfixR)(Or from "||") +:
        SOps(InfixR)(And from "&&") +:
        SOps(InfixN)(Equal from "==" , NotEqual from "!=") +:
        SOps(InfixN)(Greater from ">", GreaterEqual from ">=",
                        Less from "<", LessEqual from "<=") +:
        SOps(InfixL)(Add from "+", Sub from "-") +:
        SOps(InfixL)(Mul from "*", Div from "/", Mod from "%") +:
        SOps(Prefix)(Not from "!", (Neg from atomic("-" <~ lookAhead("("))),
                     Len from "len", Ord from "ord",
                     Chr from "chr") +:
        literals
    }

    // ========== Statements ==========
    // <stmt> ::= 'return' <expr>

    private lazy val simpleStmt: Parsley[Stmt] = Declaration(idType, "=" ~> rvalue) <|> Assignment(lvalue, "=" ~> rvalue)
    private lazy val stmt: Parsley[Stmt] = Return("return" ~> expr) <|> simpleStmt
    private lazy val stmts: Parsley[List[Stmt]] = semiSep1(stmt)

    // ========== Program ==========
    // <program> ::= <begin> <func>* <stmt> <end>
    private lazy val program: Parsley[Program] = 
        Program("begin" ~>  pure(List[Function]()), stmts <~ "end")

    // ========== L-Values ==========
    // <lvalue> ::= <ident> | <array-elem> | <pair-elem>
    private lazy val lvalue: Parsley[LValue] = {
        Fst("fst" ~> lvalue) <|> Snd("snd" ~> lvalue)
        <|> IdOrArrayElem(Id(identifier), many(brackets(expr)))
    }

    // ========== R-Values ==========
    // <rvalue> ::= expr | <array-lit> | <newpair> | <pair-elem> | <call>
    private lazy val rvalue: Parsley[RValue] = {
        ArrayLit(brackets(commaSep(expr)))
        // TODO: Use between and pair brackets
        <|> NewPair("newpair" ~> "(" ~> expr <~ ",", expr <~ ")")
        <|> (Fst("fst" ~> lvalue) <|> Snd("snd" ~> lvalue))
        <|> Call("call" ~> Id(identifier), parens(commaSep(expr)))
        <|> expr
    }

    // ========== Type System ==========
    // All types including pairs

    // Array and base types
    private val arrayAndBasetypes: Parsley[IdType & PairElemType] = {
        // Base types: int, bool, char, string
        lazy val baseType: Parsley[IdType & PairElemType] = {
            "int".as(IntType)
            <|> "bool".as(BoolType)
            <|> "char".as(CharType)
            <|> "string".as(StringType)
        }

        // Array types: <type>[]
        lazy val arrayType: Parsley[IdType & PairElemType] = {
            ArrayType(baseType, countSome("[" <~> "]"))
        }

         atomic(arrayType) <|> baseType
    }

    // Pair element types
    private lazy val pairElemType: Parsley[PairElemType] =
        "pair".as(Pair) <|> arrayAndBasetypes

    // All types besides erased pair type
    private lazy val types: Parsley[IdType] =
        arrayAndBasetypes <|> PairType("pair" ~>"(" ~> pairElemType, "," ~> pairElemType <~ ")")

    // Type-identifier pairs for declarations
    private lazy val idType: Parsley[TypeId] = {
        (types <~> Id(identifier))
    }
}
