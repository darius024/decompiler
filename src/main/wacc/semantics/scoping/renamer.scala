package wacc.semantics.scoping

import scala.collection.mutable

import wacc.error.*
import semanticTypes.*
import wacc.semantics.typing.*
import wacc.syntax.*
import bridges.*
import exprs.*
import prog.*
import stmts.*
import types.*

/** Renamed types to store identifier information. */
type IdInfo      = (KType, Position)
type RenamedInfo = (String, IdInfo)
type FuncInfo    = (KType, Array[IdInfo], Position)

/** Context for the scope checker.
  * 
  * It contains information about the signature of all functions
  * that are in scope and a global map of all renamed variables
  * with their type and position information.
  */
class ScopeCheckerContext[C](val funcTypes: mutable.Map[String, FuncInfo],
                             val varTypes: mutable.Map[String, IdInfo],
                             errs: mutable.Builder[PartialSemanticError, C]) {
    def funcs: Map[String, FuncInfo] = funcTypes.toMap
    def vars: Map[String, IdInfo] = varTypes.toMap
    def errors: C = errs.result()

    // insert a function to the function map
    def addFunc(id: Id, retType: KType, params: Array[IdInfo], pos: Position) =
        funcTypes += id.value -> (retType, params, pos)
    
    // insert a renamed variable to the global variable map
    def addVar(id: Id, kType: KType, pos: Position)
              (using funcScope: String) =
        id.value = convertName(id, funcScope)
        varTypes += id.value -> (kType, pos)

    // add an error to the list of errors
    def error(err: PartialSemanticError) = {
        errs += err
        None
    }
}

/** Checks the scope soundness of a WACC program.
  * 
  * It verifies that all variables are declared before use and are
  * not duplicated within the same scope. It also checks that the
  * main body does not contain a return statement.
  */
def scopeCheck(prog: Program): TypeInfo = {
    // initialise the context with empty maps
    given ctx: ScopeCheckerContext[List[PartialSemanticError]] =
        ScopeCheckerContext(mutable.Map.empty[String, FuncInfo],
                            mutable.Map.empty[String, IdInfo],
                            List.newBuilder)

    val Program(funcs, stmts) = prog

    // add all functions to the context and check their scopes
    // adding the functions beforehand allows for mutual recursion
    (funcs zip funcs.map(addFunction)).foreach { (func, scope) =>
        given funcScope: String = func.typeId._2.value
        // perform renaming on the function body
        rename(func.stmts, scope)
    }

    // check the scope of the main body
    given funcScope: String = "main"
    rename(stmts, Map.empty)

    TypeInfo(ctx.funcs, ctx.vars, ctx.errors)
}

/** Adds a function to the context and returns a map of its parameters. */
def addFunction(func: Function)
               (using ctx: ScopeCheckerContext[?]): Map[String, RenamedInfo] = {
    val Function((retTy, funcName), params, stmts) = func
    given funcScope: String = funcName.value

    // rename all parameters and add them to the context
    // need to keep track of the parameters in order
    val paramScope = mutable.LinkedHashMap.empty[String, RenamedInfo]
    params.foreach(checkIdInScope(_, _, paramScope))

    // add the function defintion to the context
    if (ctx.funcs.contains(funcName.value)) {
        val funcLocation = ctx.funcs(funcName.value)._3
        ctx.error(FunctionAlreadyDeclared(funcName.value, funcLocation)("main", funcName.pos))
    } else {
        ctx.addFunc(funcName, convertType(retTy), paramScope.toArray.map(_._2._2), func.pos)
    }
    paramScope.toMap
}

/** Renames and checks all variables in a list of statements. */
def rename(stmts: StmtList, parentScope: Map[String, RenamedInfo])
          (using funcScope: String)
          (using ScopeCheckerContext[?]): Unit = {
    val currentScope = mutable.Map.empty[String, RenamedInfo]

    stmts.toList.foreach(rename(_, parentScope, currentScope))
}

/** Renames and checks all variables in a statement. */
def rename(stmt: Stmt, parentScope: Map[String, RenamedInfo], currentScope: mutable.Map[String, RenamedInfo])
          (using funcScope: String)
          (using ctx: ScopeCheckerContext[?]): Unit = stmt match {
    case Skip =>

    case Declaration((ty, id), rv) =>
        rename(rv, parentScope, currentScope)
        checkIdInScope(ty, id, currentScope)
    
    case Assignment(lv, rv) =>
        rename(lv, parentScope, currentScope)
        rename(rv, parentScope, currentScope)
    
    case Read(lv)           => rename(lv, parentScope, currentScope)
    case Free(expr)         => rename(expr, parentScope, currentScope)
    case Return(expr)       => rename(expr, parentScope, currentScope)
    case Exit(expr)         => rename(expr, parentScope, currentScope)
    case Print(expr)        => rename(expr, parentScope, currentScope)
    case Println(expr)      => rename(expr, parentScope, currentScope)

    case If(cond, thenStmts, elseStmts) =>
        rename(cond, parentScope, currentScope)
        rename(thenStmts, parentScope ++ currentScope)
        rename(elseStmts, parentScope ++ currentScope)
    
    case While(cond, doStmts) =>
        rename(cond, parentScope, currentScope)
        rename(doStmts, parentScope ++ currentScope)

    case Block(stmts) =>
        rename(stmts, parentScope ++ currentScope)
}

/** Renames and checks all variables in a value. */
def rename(rvalue: LValue | RValue, parentScope: Map[String, RenamedInfo], currentScope: mutable.Map[String, RenamedInfo])
          (using funcScope: String)
          (using ctx: ScopeCheckerContext[?]): Unit = rvalue match {
    case e: Expr => renameExpr(e, parentScope, currentScope)

    case ArrayLit(exprs) => exprs.map(rename(_, parentScope, currentScope))

    case NewPair(fst, snd) => renameBinExpr(fst, snd, parentScope, currentScope)

    case pairElem: PairElem => pairElem match {
        case Fst(lval) => rename(lval, parentScope, currentScope)
        case Snd(lval) => rename(lval, parentScope, currentScope)
    }

    case Call(func, args) =>
        // check if the function is defined in the main scope
        if (ctx.funcs.contains(func.value)) {
            args.foreach(rename(_, parentScope, currentScope))
        } else {
            ctx.error(FunctionNotDefined(func.value)(funcScope, func.pos))
        }
}

/** Renames and checks all variables in an expression. */
def renameExpr(expr: Expr, parentScope: Map[String, RenamedInfo], currentScope: mutable.Map[String, RenamedInfo])
              (using funcScope: String)
              (using ctx: ScopeCheckerContext[?]): Unit = expr match {
    case id @ Id(value) =>
        // check if the variable is in the current or parent scope
        if (currentScope.contains(value)) {
            id.value = currentScope(value)._1
        } else {
            if (parentScope.contains(value)) {
                id.value = parentScope(value)._1
            } else {
                ctx.error(VariableNotInScope(id.value, (parentScope ++ currentScope).keys.toList)(funcScope, id.pos))
            }
        }
    
    case ArrayElem(id, indices) =>
        renameExpr(id, parentScope, currentScope)
        indices.foreach(renameExpr(_, parentScope, currentScope))
    
    case Or(lhs, rhs)           => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case And(lhs, rhs)          => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Equal(lhs, rhs)        => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case NotEqual(lhs, rhs)     => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Greater(lhs, rhs)      => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case GreaterEq(lhs, rhs)    => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case Less(lhs, rhs)         => renameBinExpr(lhs, rhs, parentScope, currentScope)
    case LessEq(lhs, rhs)       => renameBinExpr(lhs, rhs, parentScope, currentScope)
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

/** Renames and checks all variables in a binary expression. */
def renameBinExpr(lhs: Expr, rhs: Expr, parentScope: Map[String, RenamedInfo], currentScope: mutable.Map[String, RenamedInfo])
                 (using funcScope: String)
                 (using ScopeCheckerContext[?]): Unit = {
    renameExpr(lhs, parentScope, currentScope)
    renameExpr(rhs, parentScope, currentScope)
}

/** Checks if an identifier is in the current scope. */
def checkIdInScope(ty: IdType, id: Id, scope: mutable.Map[String, RenamedInfo])
                  (using funcScope: String)
                  (using ctx: ScopeCheckerContext[?]): Unit = {
    if (scope.contains(id.value)) {
        val varLocation = scope(id.value)._2._2
        ctx.error(VariableAlreadyDeclared(id.value, varLocation)(funcScope, id.pos))
    } else {
        // add the new variable to the current scope and the global map
        val actualId = id.value
        val kType = convertType(ty)
        ctx.addVar(id, kType, id.pos)
        scope += actualId -> (id.value, (kType, id.pos))
    }
}

/** Converts a syntactic name to a uniquely identified name. */
def convertName(id: Id, funcScope: String): String =
    s"${id.value}/${funcScope}/${id.pos._1}/${id.pos._2}"
