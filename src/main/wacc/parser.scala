package wacc
import parsley.Parsley.{some}
import parsley.{Parsley, Result}
import parsley.expr.{precedence, SOps, InfixL, InfixR, InfixN, Prefix, Atoms}
import syntax.exprs.*

import lexer.implicits.implicitSymbol
import wacc.lexer.*


object parser {
    // TODO: Change Expr to Program when we implement it
    def parse(input: String): Result[String, Expr] = parser.parse(input)
    private val parser = fully(expr)
    

    private lazy val atom: Parsley[Atom] = {
        "null".as(PairLit)
        <|> BoolLit("true".as(true) | "false".as(false)) 
        <|> IntLit(integer.map(_.toInt))
        <|> CharLit(character) 
        <|> StrLit(string) 
        <|> IdOrArrayElem(Id(identifier), some(brackets(expr)))
        <|> ParensExpr(parens(expr))
    }

    private lazy val literals = Atoms(atom)

    private lazy val expr: Parsley[Expr] = precedence {
        SOps(InfixR)(Or from "||") +:
        SOps(InfixR)(And from "&&") +:
        SOps(InfixN)(Equal from "==" , NotEqual from "!=") +:
        SOps(InfixN)(Greater from ">", GreaterEqual from ">=",  
                     Less from "<", LessThan from "<=") +:
        SOps(InfixL)(Add from "+", Sub from "-") +:
        SOps(InfixL)(Mul from "*", Div from "/", Mod from "%") +:
        SOps(Prefix)(Not from "!", Neg from "-", 
                     Len from "len", Ord from "ord",
                     Chr from "chr") +:
        literals
    }

}

