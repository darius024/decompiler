package wacc.semantics.scoping

import scala.collection.mutable

import semanticTypes.*
import wacc.syntax.*
import bridges.*
import exprs.*
import prog.*
import stmts.*
import types.*

type IdInfo = (KType, Position)
type RenamedInfo = (String, IdInfo)
type FuncInfo = (KType, List[IdInfo], Position)

enum SemanticError {
    case VariableNotInScope(id: String)(val pos: Position)
    case VariableAlreadyDeclared(id: String)(val pos: Position)
    case FunctionNotDefined(id: String)(val pos: Position)
    case ReturnMainBody(val pos: Position)
}

class ScopeCheckerContext[C](val funcTypes: mutable.Map[String, FuncInfo],
                             val varTypes: mutable.Map[String, IdInfo],
                             errs: mutable.Builder[SemanticError, C]) {
    def funcs: Map[String, FuncInfo] = funcTypes.toMap
    def vars: Map[String, IdInfo] = varTypes.toMap
    def errors: C = errs.result()

    def addFunc(id: Id, retType: KType, params: List[IdInfo], pos: Position) =
        funcTypes += id.value -> (retType, params, pos)
    def addVar(id: Id, kType: KType, pos: Position)
              (using funcScope: String) =
        id.value = convertName(id, funcScope)
        varTypes += id.value -> (kType, pos)

    def error(err: SemanticError) = {
        errs += err
        None
    }
}

def scopeCheck(prog: Program): (List[SemanticError], Map[String, FuncInfo], Map[String, IdInfo]) = {
    given ctx: ScopeCheckerContext[List[SemanticError]] =
        ScopeCheckerContext(mutable.Map.empty[String, FuncInfo],
                            mutable.Map.empty[String, IdInfo],
                            List.newBuilder)

    val Program(funcs, stmts) = prog
    funcs.foreach(rename)
    given funcScope: String = "main"
    rename(stmts, Map.empty[String, RenamedInfo])

    (ctx.errors, ctx.funcs, ctx.vars)
}

def rename(func: Function)(using ctx: ScopeCheckerContext[?]): Unit = {
    val Function((retTy, funcName), params, stmts) = func
    given funcScope: String = funcName.value

    val ps: Map[String, RenamedInfo] = params.map { (idType, id) =>
        val kType = convertType(idType)

        val actualId = id.value
        ctx.addVar(id, kType, id.pos)

        actualId -> (id.value, (kType, id.pos))
    }.toMap

    ctx.addFunc(funcName, convertType(retTy), ps.values.map(_._2).toList, func.pos)
    rename(stmts, ps)
}

def rename(stmts: List[Stmt], parentScope: Map[String, RenamedInfo])
          (using funcScope: String)
          (using ScopeCheckerContext[?]) = ???

def rename(stmt: Stmt, parentScope: Map[String, RenamedInfo], currentScope: mutable.Map[String, RenamedInfo])
          (using funcScope: String)
          (using ctx: ScopeCheckerContext[?]): Unit = stmt match {
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

def rename(rvalue: LValue | RValue, parentScope: Map[String, RenamedInfo], currentScope: mutable.Map[String, RenamedInfo])
          (using funcScope: String)
          (using ctx: ScopeCheckerContext[?]): Unit = rvalue match {
    case e: Expr => renameExpr(e, parentScope, currentScope)
    case pairElem: PairElem => pairElem match {
        case Fst(lval) => rename(lval, parentScope, currentScope)
        case Snd(lval) => rename(lval, parentScope, currentScope)
    }
    case ArrayLit(exprs) => exprs.map(expr => rename(expr, parentScope, currentScope))
    case NewPair(fst, snd) => renameBinExpr(fst, snd, parentScope, currentScope)
    case Call(func, args) =>
        if (ctx.funcs.contains(func.value)) {
            args.map(arg => rename(arg, parentScope, currentScope))
        } else {
            ctx.error(SemanticError.FunctionNotDefined(func.value)(func.pos))
        }
}

def renameExpr(expr: Expr, parentScope: Map[String, RenamedInfo], currentScope: mutable.Map[String, RenamedInfo])
              (using funcScope: String)
              (using ctx: ScopeCheckerContext[?]): Unit = expr match {
    case id@Id(value) =>
        if (currentScope.contains(value)) {
            id.value = currentScope(value)._1
        } else {
            if (parentScope.contains(value)) {
                id.value = parentScope(value)._1
            } else {
                ctx.error(SemanticError.VariableNotInScope(id.value)(id.pos))
            }
        }
    case ArrayElem(id, indices) =>
        renameExpr(id, parentScope, currentScope)
        indices.foreach(index => renameExpr(index, parentScope, currentScope))
    
    case Or(lhs, rhs)           => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case And(lhs, rhs)          => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Equal(lhs, rhs)        => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case NotEqual(lhs, rhs)     => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Greater(lhs, rhs)      => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case GreaterEqual(lhs, rhs) => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Less(lhs, rhs)         => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case LessEqual(lhs, rhs)    => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Add(lhs, rhs)          => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Sub(lhs, rhs)          => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Mul(lhs, rhs)          => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Div(lhs, rhs)          => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Mod(lhs, rhs)          => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Not(exp)               => renameExpr(exp, parentScope, currentScope)
    case Neg(exp)               => renameExpr(exp, parentScope, currentScope)
    case Len(exp)               => renameExpr(exp, parentScope, currentScope)
    case Ord(exp)               => renameExpr(exp, parentScope, currentScope)
    case Chr(exp)               => renameExpr(exp, parentScope, currentScope)
    case ParensExpr(exp)        => renameExpr(exp, parentScope, currentScope)
    
    case _ => 
}

def renameBinExpr(lhs: Expr, rhs: Expr, parentScope: Map[String, RenamedInfo], currentScope: mutable.Map[String, RenamedInfo])
                 (using funcScope: String)
                 (using ScopeCheckerContext[?]): Unit = {
    renameExpr(lhs, parentScope, currentScope)
    renameExpr(rhs, parentScope, currentScope)
}

def convertName(id: Id, funcScope: String): String =
    s"${id.value}_${funcScope}_${id.pos._1}_${id.pos._2}"
