package wacc.semantics.typing

import cats.data.NonEmptyList
import scala.collection.mutable

import wacc.semantics.errors.*
import wacc.semantics.scoping.*
import semanticTypes.*
import wacc.syntax.*
import bridges.*
import exprs.*
import stmts.*
import prog.*

import Constraint.*

/** Syntactic errors that can occur during type checking. */
enum TypeError extends SemanticError {
    case TypeMismatch(unexpected: SemType, expected: SemType)(val pos: Position)
    case TypeCannotBeInfered(val pos: Position)
    case NumberArgumentsMismatch(expected: Int, got: Int)(val pos: Position)
}

/** Type constraints that can be imposed on types. */
enum Constraint {
    case Is(ty: SemType)
    case IsEither(ty1: SemType, ty2: SemType)
}
object Constraint {
    val Unconstrained = Is(?)
    def IsArray(kType: KType) = Is(KType.Array(kType))
    val IsPair = Is(KType.Pair(?, ?))
}

/** Type information received from the scope-checking phase. */
class TypeInfo(val funcs: Map[String, FuncInfo], val vars: Map[String, IdInfo])

/** Context for the type checker.
  * 
  * It keeps track of the type information and errors.
  */
class TypeCheckerContext[C](tyInfo: TypeInfo, errs: mutable.Builder[TypeError, C]) {
    def errors: C = errs.result()

    // get the type of the identifier
    def typeOf(id: String): KType = tyInfo.vars(id)._1
    // get the return type of the function
    def returnTypeOf(funcName: String): KType = tyInfo.funcs(funcName)._1
    // get the signature of the function
    def signatureOf(funcName: String): List[KType] = tyInfo.funcs(funcName)._2.map(_._1)

    // add an error to the context
    def error(err: TypeError) = {
        errs += err
        None
    }
}

/** Checks the type soundness of a WACC program.
  * 
  * It verifies that all types match within statements.
*/
def typeCheck(prog: Program, tyInfo: TypeInfo): Either[NonEmptyList[TypeError], TyProg] = {
    // initialise the context
    given ctx: TypeCheckerContext[List[TypeError]] = TypeCheckerContext(tyInfo, List.newBuilder)
    
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

/** Checks the type soundness within a function body. */
def check(func: Function)
         (using ctx: TypeCheckerContext[?]): TyFunc = {
    val Function((_, funcName), params, stmts) = func

    val retTy = ctx.returnTypeOf(funcName.value)
    val paramsTyped = params.map { (_, id) => TyExpr.LVal.Id(id.value, ctx.typeOf(id.value)) }
    val stmtsTyped = stmts.collect { stmt => check(stmt, Some(retTy)) }

    TyFunc(TyExpr.LVal.Id(funcName.value, retTy), paramsTyped, stmtsTyped)
}

/** Checks the type soundness of a statement.
  * 
  * Uses the type parameter to verify the return values of statements.
*/
def check(stmt: Stmt, retTy: Option[SemType])
         (using TypeCheckerContext[?]): TyStmt = stmt match {
    case Declaration((_, id), rvalue) =>
        val (idTy, idTyped) = checkLValue(id, Unconstrained)
        val (_, rvTyped) = checkRValue(rvalue, Is(idTy.getOrElse(?)))
        TyStmt.Assignment(idTyped, rvTyped)

    case Assignment(lvalue, rvalue) =>
        val (lvTy, lvTyped) = checkLValue(lvalue, Unconstrained)
        val (_, rvTyped) = checkRValue(rvalue, Is(lvTy.getOrElse(?)))
        TyStmt.Assignment(lvTyped, rvTyped)

    case Read(lvalue) =>
        val (_, lvTyped) = checkLValue(lvalue, IsEither(KType.Int, KType.Char))
        TyStmt.Read(lvTyped)

    case Print(expr) =>
        val (_, exprTyped) = checkExpr(expr, Unconstrained)
        TyStmt.Print(exprTyped)

    case Println(expr) =>
        val (_, exprTyped) = checkExpr(expr, Unconstrained)
        TyStmt.Println(exprTyped)

    case Free(expr) =>
        val (_, exprTyped) = checkExpr(expr, IsEither(KType.Array(?), KType.Pair(?, ?)))
        TyStmt.Print(exprTyped)

    case Return(expr) =>
        val (_, exprTyped) = checkExpr(expr, Is(retTy.getOrElse(?)))
        TyStmt.Return(exprTyped)

    case Exit(expr) =>
        val (_, exprTyped) = checkExpr(expr, Is(KType.Int))
        TyStmt.Exit(exprTyped)
    
    case If(cond, thenStmts, elseStmts) =>
        val (_, condTyped) = checkExpr(cond, Is(KType.Bool))
        val thenTyped = thenStmts.map { stmt => check(stmt, retTy) }
        val elseTyped = elseStmts.map { stmt => check(stmt, retTy) }
        TyStmt.If(condTyped, thenTyped, elseTyped)

    case While(cond, doStmts) =>
        val (_, condTyped) = checkExpr(cond, Is(KType.Bool))
        val doTyped = doStmts.map { stmt => check(stmt, retTy) }
        TyStmt.While(condTyped, doTyped)

    case Block(stmts) =>
        val blockTyped = stmts.map { stmt => check(stmt, retTy) }
        TyStmt.Block(blockTyped)

    case _ => ???
}

/** Checks the type soundness of an expression.
  * 
  * Imposes specific constraints on the types of the operations.
*/
def checkExpr(expr: Expr, cons: Constraint)
             (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = expr match {
    case Or(x, y)           => checkBinExpr(x, y, KType.Bool, cons)(TyExpr.Or.apply)
    case And(x, y)          => checkBinExpr(x, y, KType.Bool, cons)(TyExpr.And.apply)
    
    case Equal(x, y)        => checkBinExpr(x, y, ?, cons)(TyExpr.Equal.apply)
    case NotEqual(x, y)     => checkBinExpr(x, y, ?, cons)(TyExpr.NotEqual.apply)
    
    case Greater(x, y)      => checkIntChar(x, y, cons)(TyExpr.Greater.apply)
    case GreaterEqual(x, y) => checkIntChar(x, y, cons)(TyExpr.GreaterEqual.apply)
    case Less(x, y)         => checkIntChar(x, y, cons)(TyExpr.Less.apply)
    case LessEqual(x, y)    => checkIntChar(x, y, cons)(TyExpr.LessEqual.apply)
    
    case Add(x, y)          => checkBinExpr(x, y, KType.Int, cons)(TyExpr.Add.apply)
    case Sub(x, y)          => checkBinExpr(x, y, KType.Int, cons)(TyExpr.Sub.apply)
    case Mul(x, y)          => checkBinExpr(x, y, KType.Int, cons)(TyExpr.Mul.apply)
    case Div(x, y)          => checkBinExpr(x, y, KType.Int, cons)(TyExpr.Div.apply)
    case Mod(x, y)          => checkBinExpr(x, y, KType.Int, cons)(TyExpr.Mod.apply)
    
    case Not(x)             => checkUnary(x, KType.Bool, KType.Bool, cons)(TyExpr.Not.apply)
    case Neg(x)             => checkUnary(x, KType.Int, KType.Int, cons)(TyExpr.Neg.apply)
    case Len(x)             => checkUnary(x, KType.Array(?), KType.Int, cons)(TyExpr.Len.apply)
    case Ord(x)             => checkUnary(x, KType.Char, KType.Int, cons)(TyExpr.Ord.apply)
    case Chr(x)             => checkUnary(x, KType.Int, KType.Char, cons)(TyExpr.Chr.apply)

    case IntLit(v)          => (KType.Int.satisfies(cons), TyExpr.IntLit(v))
    case BoolLit(v)         => (KType.Bool.satisfies(cons), TyExpr.BoolLit(v))
    case CharLit(v)         => (KType.Char.satisfies(cons), TyExpr.CharLit(v))
    case StrLit(v)          => (KType.Str.satisfies(cons), TyExpr.StrLit(v))
    case PairLit            => (KType.Pair(?, ?).satisfies(cons), TyExpr.PairLit)
    case id: Id             => checkLValue(id, cons)
    case ae: ArrayElem      => checkLValue(ae, cons)
    case ParensExpr(e)      => checkExpr(e, cons)
}

/** Checks the type soundness of an rvalue. */
def checkRValue(rvalue: RValue, cons: Constraint)
               (using ctx: TypeCheckerContext[?]): (Option[SemType], TyExpr) = rvalue match {
    case e: Expr => checkExpr(e, cons)
        
    case ArrayLit(exprs) =>
        // the constraint should be an array of some type
        cons match {
            case Is(KType.Array(kTy)) =>
                val exprsTyped = exprs.map { expr => checkExpr(expr, Is(kTy))._2 }
                val arrayType = exprs match {
                    case Nil => KType.Array(?)
                    case ty :: tys => KType.Array(kTy)
                }
                (arrayType.satisfies(cons), TyExpr.ArrayLit(exprsTyped, arrayType))
            case _ =>
                val (exprsTy, exprsTyped) = exprs.map { expr => checkExpr(expr, Unconstrained) }.unzip
                (KType.Array(?).satisfies(cons), TyExpr.ArrayLit(exprsTyped, KType.Array(?)))
        }
    
    case NewPair(fst, snd) =>
        val (fstTy, fstTyped) = checkExpr(fst, cons)
        val (sndTy, sndTyped) = checkExpr(snd, cons)
        val firstTy = fstTy.getOrElse(?)
        val secondTy = sndTy.getOrElse(?)
        (KType.Pair(firstTy, secondTy).satisfies(cons), TyExpr.NewPair(fstTyped, sndTyped, firstTy, secondTy))

    case pairElem: PairElem => checkPairElem(pairElem, cons)
    
    case Call(id, args) =>
        val retTy = ctx.returnTypeOf(id.value)
        val argsTy = ctx.signatureOf(id.value)
        
        // check if the number of arguments match
        if (argsTy.length != args.length) {
            ctx.error(TypeError.NumberArgumentsMismatch(argsTy.length, args.length)(id.pos))
        }

        val (argTys, argsTyped) = args.zip(argsTy).map { (arg, argTy) =>
            val (exprTy, exprTyped) = checkExpr(arg, Is(argTy))
            (exprTy.getOrElse(?), exprTyped)
        }.unzip

        (retTy.satisfies(cons), TyExpr.Call(TyExpr.LVal.Id(id.value, retTy), argsTyped, argTys))
}

/** Checks the type soundness of an lvalue. */
def checkLValue(lvalue: LValue, cons: Constraint)
               (using ctx: TypeCheckerContext[?]): (Option[SemType], TyExpr.LVal) = lvalue match {
    case Id(value) => 
        val kTy = ctx.typeOf(value)
        (kTy.satisfies(cons), TyExpr.LVal.Id(value, kTy))
    
    case ArrayElem(id, idx) =>
        val (kTy, idTyped) = checkLValue(id, Is(KType.Array(?)))
        val (idxTy, idxTyped) = idx.map { idx =>
            val (exprTy, exprTyped) = checkExpr(idx, Is(KType.Int))
            (exprTy.getOrElse(?), exprTyped)
        }.unzip
        (kTy, TyExpr.LVal.ArrayElem(idTyped, idxTyped, kTy.getOrElse(?)))
    
    case pairElem: PairElem => checkPairElem(pairElem, cons)
}

/** Checks the type soundness of a pair element. */
def checkPairElem(pairElem: PairElem, cons: Constraint)
                 (using TypeCheckerContext[?]): (Option[SemType], TyExpr.LVal) = pairElem match {
    case Fst(lval) =>
        val (kTy, lvalTyped) = checkLValue(lval, IsPair)
        (KType.Pair(?, ?).satisfies(cons), TyExpr.LVal.PairFst(lvalTyped, kTy.getOrElse(?)))
    
    case Snd(lval) =>
        val (kTy, lvalTyped) = checkLValue(lval, IsPair)
        (KType.Pair(?, ?).satisfies(cons), TyExpr.LVal.PairSnd(lvalTyped, kTy.getOrElse(?)))
}

/** Checks the type soundness of a binary expression. */
def checkBinExpr(x: Expr, y: Expr, semType: SemType, cons: Constraint)
                (build: (TyExpr, TyExpr) => TyExpr)
                (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (lhsTy, xTyped) = checkExpr(x, Is(semType))
    val (rhsTy, yTyped) = checkExpr(y, lhsTy.fold(Is(semType))(Is(_)))
    val ty = mostSpecific(lhsTy, rhsTy)

    (ty.satisfies(cons), build(xTyped, yTyped))
}

/** Checks the type soundness of an integer or character binary expression. */
def checkIntChar(x: Expr, y: Expr, cons: Constraint)
                (build: (TyExpr, TyExpr) => TyExpr)
                (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (lhsTy, xTyped) = checkExpr(x, IsEither(KType.Int, KType.Char))
    val (rhsTy, yTyped) = checkExpr(y, lhsTy.fold(IsEither(KType.Int, KType.Char))(Is(_)))
    val ty = mostSpecific(lhsTy, rhsTy)

    (ty.satisfies(cons), build(xTyped, yTyped))
}

/** Checks the type soundness of a unary expression. */
def checkUnary(x: Expr, argTy: SemType, RetTy: SemType, cons: Constraint)
              (build: (TyExpr) => TyExpr)
              (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (exprTy, xTyped) = checkExpr(x, Is(argTy))

    (exprTy.getOrElse(?).satisfies(cons), build(xTyped))
}

/** Returns the most specific type between two types. */
def mostSpecific(ty1: Option[SemType], ty2: Option[SemType]): SemType = (ty1, ty2) match {
    case (Some(?), Some(t)) => t
    case (Some(t), _)       => t
    case (None, t)          => t.getOrElse(?)
}

/** Checks if a type satisfies a constraint. */
extension (ty: SemType) def satisfies(cons: Constraint)
                                     (using ctx: TypeCheckerContext[?]): Option[SemType] = (ty, cons) match {
    case (?, Is(refTy)) => Some(refTy)
    case (?, _) => Some(?)
    case (KType.Array(KType.Char), Is(KType.Str)) => Some(KType.Array(KType.Char))
    case (KType.Str, Is(KType.Array(KType.Char))) => Some(KType.Array(KType.Char))
    case (kTy, Is(refTy)) =>
        if kTy == refTy then Some(kTy)
        else ctx.error(TypeError.TypeMismatch(kTy, refTy)((0, 0)))   
    case (semTy, IsEither(ty1, ty2)) =>
        if semTy == ty1 || semTy == ty2 then Some(semTy)
        else ctx.error(TypeError.TypeMismatch(semTy, ty1)((0, 0)))
    case _ => None
}
