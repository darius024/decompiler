package wacc.semantics.typing

import cats.data.NonEmptyList
import scala.collection.mutable

import wacc.semantics.scoping.*
import semanticTypes.*
import wacc.syntax.*
import bridges.*
import exprs.*
import stmts.*
import prog.*

import Constraint.*

enum SyntaxError {
    case TypeMismatch(unexpected: SemType, expected: SemType)(val pos: Position)
    case TypeCannotBeInfered(val pos: Position)
}
enum Constraint {
    case Is(ty: SemType)
    case IsEither(ty1: SemType, ty2: SemType)
}
object Constraint {
    val Unconstrained = Is(?)
    def IsArray(kType: KType) = Is(KType.Array(kType))
    val IsPair = Is(KType.Pair(?, ?))
}

class TypeInfo(val funcs: Map[String, FuncInfo], val vars: Map[String, IdInfo])

class TypeCheckerContext[C](tyInfo: TypeInfo, errs: mutable.Builder[SyntaxError, C]) {
    def errors: C = errs.result()

    def typeOf(v: String): KType = tyInfo.vars(v)._1

    def Syntaxerror(err: SyntaxError) = {
        errs += err
        None
    }
}

def typeCheck(prog: Program, tyInfo: TypeInfo): Either[NonEmptyList[SyntaxError], TyProg] = {
    given ctx: TypeCheckerContext[List[SyntaxError]] = TypeCheckerContext(tyInfo, List.newBuilder)
    
    val Program(funcs, stmts) = prog
    val typedFuncs = funcs.map(check)
    val typedStmts = stmts.map { stmt =>
        check(stmt, None)
    }

    val typedProg = TyProg(typedFuncs, typedStmts)

    ctx.errors match {
        case err :: errs => Left(NonEmptyList(err, errs))
        case Nil         => Right(typedProg)
    }
}

def check(func: Function)
         (using TypeCheckerContext[?]): TyFunc = ???

def check(stmt: Stmt, retTy: Option[SemType])
         (using TypeCheckerContext[?]): TyStmt = stmt match {
    case Skip => ???
    case Declaration((ty, id), rvalue) => ???
    case Assignment(lvalue, rvalue) => ???
    case Read(lvalue) => ???
    case Print(expr) => ???
    case Println(expr) => ???
    case Free(expr) => ???
    case Return(expr) => ???
    case Exit(expr) => ???
    case If(cond, thenStmts, elseStmts) => ???
    case While(cond, doStmts) => ???
    case Block(stmts) => ???
}

def checkExpr(expr: Expr, cons: Constraint)
             (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = expr match {
    case Or(x, y) => ???
    case And(x, y) => ???
    
    case Equal(x, y) => ???
    case NotEqual(x, y) => ???
    
    case Greater(x, y) => ???
    case GreaterEqual(x, y) => ???
    case Less(x, y) => ???
    case LessEqual(x, y) => ???
    
    case Add(x, y) => ???
    case Sub(x, y) => ???
    case Mul(x, y) => ???
    case Div(x, y) => ???
    case Mod(x, y) => ???
    
    case Not(x) => ???
    case Neg(x) => ???
    case Len(x) => ???
    case Ord(x) => ???
    case Chr(x) => ???

    case IntLit(v) => ???
    case BoolLit(v) => ???
    case CharLit(v) => ???
    case StrLit(v) => ???
    case PairLit => ???
    case Id(v) => ???
    case ArrayElem(v, idx) => ??? 
    case ParensExpr(e) => ???   
}

def check(value: LValue | RValue, cons: Constraint)
         (using ctx: TypeCheckerContext[?]): (Option[SemType], TyExpr.LVal) = value match {
    case e: Expr => ???
    case pairElem: PairElem => pairElem match {
        case Fst(lval) => ???
        case Snd(lval) => ???
    }
    case ArrayLit(exprs) => ???
    case NewPair(fst, snd) => ???
    case Call(func, args) => ???
}
