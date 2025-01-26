package wacc.semantics.scoping

import scala.collection.mutable

import semanticTypes.*
import wacc.syntax.*
import bridges.*
import exprs.*
import prog.*
import stmts.*

type IdInfo = (KType, Position)
type FuncInfo = (KType, List[IdInfo], Position)

enum SemanticError {
    case VariableNotInScope(id: Id)(val pos: Position)
    case VariableAlreadyDeclared(id: Id)(val pos: Position)
    case VariableNotAssignedType(id: Id)(val pos: Position)
}

class ScopeCheckerContext[C](val funcTypes: mutable.Map[String, FuncInfo],
                             val varTypes: mutable.Map[String, IdInfo],
                             errs: mutable.Builder[Error, C]) {
    def funcs: Map[String, FuncInfo] = funcTypes.toMap
    def vars: Map[String, IdInfo] = varTypes.toMap
    def errors: C = errs.result()

    def addFunc(id: String, retType: KType, params: List[IdInfo], pos: Position) =
        funcTypes += id -> (retType, params, pos)
    def addVar(id: String, kType: KType, pos: Position) =
        varTypes += id -> (kType, pos)

    def error(err: Error) = {
        errs += err
        None
    }
}

def scopeCheck(prog: Program): (List[Error], Map[String, FuncInfo], Map[String, IdInfo]) = {
    given ctx: ScopeCheckerContext[List[Error]] =
        ScopeCheckerContext(mutable.Map.empty[String, FuncInfo],
                            mutable.Map.empty[String, IdInfo],
                            List.newBuilder)

    val Program(funcs, stmts) = prog
    funcs.map(rename)
    rename(stmts, Map.empty[String, IdInfo])

    (ctx.errors, ctx.funcs, ctx.vars)
}

def rename(func: Function)(using ctx: ScopeCheckerContext[?]) = {
    val Function((retTy, id), params, stmts) = func

    val ps: Map[String, IdInfo] = params.map { (idType, id) =>
        id.value -> (convertType(idType), (0, 0))
    }.toMap

    ctx.addFunc(id.value, convertType(retTy), ps.values.toList, func.pos)
}

def rename(stmts: List[Stmt], parentScope: Map[String, IdInfo])
          (using ScopeCheckerContext[?]) = ???

def rename(stmt: Stmt, parentScope: Map[String, SemType], currentScope: mutable.Map[String, SemType])
          (using ScopeCheckerContext[?]) = stmt match {
    case Skip => ???
    case Declaration((id, ty), rv) => ??? 
    case Assignment(lv, rv) => ???
    case Read(lv) => ???
    case Free(expr) => ???
    case Return(expr) => ???
    case Exit(expr) => ???
    case Print(expr) => ???
    case Println(expr) => ???
    case If(cond, thenStmts, elseStmts) => ???
    case While(cond, doStmts) => ???
    case Block(stmts) => ???
}

def rename(expr: Expr, parentScope: Map[String, SemType], currentScope: mutable.Map[String, SemType])
          (using ScopeCheckerContext[?]) = expr match {
    case Id(value) => ???
    case ArrayElem(id, indices) => ???
    case ParensExpr(exp) => ???
    case Or(lhs, rhs) => ???
    case And(lhs, rhs) => ???
    case Equal(lhs, rhs) => ???
    case NotEqual(lhs, rhs) => ???
    case Greater(lhs, rhs) => ???
    case GreaterEqual(lhs, rhs) => ???
    case Less(lhs, rhs) => ???
    case LessEqual(lhs, rhs) => ???
    case Add(lhs, rhs) => ???
    case Sub(lhs, rhs) => ???
    case Mul(lhs, rhs) => ???
    case Div(lhs, rhs) => ???
    case Mod(lhs, rhs) => ???
    case Not(expr) => ???
    case Neg(expr) => ???
    case Len(expr) => ???
    case Ord(expr) => ???
    case Chr(expr) => ???
    case _ => ???
}

def rename(lvalue: LValue, parentScope: Map[String, SemType], currentScope: mutable.Map[String, SemType])
          (using ScopeCheckerContext[?]) = lvalue match {
    case Id(value) => ???
    case ArrayElem(id, indices) => ???
    case _: PairElem => ???
}

def rename(rvalue: RValue, parentScope: Map[String, SemType], currentScope: mutable.Map[String, SemType])
          (using ScopeCheckerContext[?]) = rvalue match {
    case _: Expr => ???
    case ArrayLit(exprs) => ???
    case NewPair(fst, snd) => ???
    case _: PairElem => ???
    case Call(func, args) => ???
}
