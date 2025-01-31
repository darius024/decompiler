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
    case TypeUnknown(val pos: Position)
}

/** Type constraints that can be imposed on types. */
enum Constraint {
    case Is(ty: SemType)
    case IsEither(ty1: SemType, ty2: SemType)
}
object Constraint {
    val Unconstrained = Is(?)
    def IsArray(semTy: SemType) = Is(KType.Array(semTy))
    def IsPair(semTy1: SemType, semTy2: SemType) = Is(KType.Pair(semTy1, semTy2))
}

/** Type information received from the scope-checking phase. */
class TypeInfo(val funcs: Map[String, FuncInfo], val vars: Map[String, IdInfo])

/** Context for the type checker.
  * 
  * It keeps track of the type information and errors.
  * For identifiers out of scope, it returns unknown types.
  */
class TypeCheckerContext[C](tyInfo: TypeInfo, errs: mutable.Builder[TypeError, C]) {
    def errors: C = errs.result()

    // get the type of the identifier
    def typeOf(id: String): SemType = tyInfo.vars.get(id).map(_._1).getOrElse(?)
    // get the return type of the function
    def returnTypeOf(funcName: String): SemType = tyInfo.funcs.get(funcName).map(_._1).getOrElse(?)
    // get the signature of the function
    def signatureOf(funcName: String): List[SemType] = tyInfo.funcs.get(funcName).map(_._2.map(_._1)).getOrElse(Nil)

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
    val typedStmts = stmts.flatMap { stmt => check(stmt, None) }

    ctx.errors match {
        case err :: errs => Left(NonEmptyList(err, errs))
        case Nil         => Right(TyProg(typedFuncs, typedStmts))
    }
}

/** Checks the type soundness within a function body. */
def check(func: Function)
         (using ctx: TypeCheckerContext[?]): TyFunc = {
    val Function((_, funcName), params, stmts) = func

    val retTy = ctx.returnTypeOf(funcName.value)
    val paramsTyped = params.map { (_, id) => checkLValue(id, Unconstrained)._2 }
    val stmtsTyped = stmts.flatMap { stmt => check(stmt, Some(retTy)) }

    TyFunc(TyExpr.LVal.Id(funcName.value, retTy), paramsTyped, stmtsTyped)
}

/** Checks the type soundness of a statement.
  * 
  * Uses the type parameter to verify the return values of statements.
  */
def check(stmt: Stmt, retTy: Option[SemType])
         (using TypeCheckerContext[?]): Option[TyStmt] = stmt match {
    case Skip => None

    case Declaration((_, id), rvalue) =>
        val (idTy, idTyped) = checkLValue(id, Unconstrained)
        val (_, rvTyped) = checkRValue(rvalue, Is(weakenType(idTy)))
        Some(TyStmt.Assignment(idTyped, rvTyped))

    case a @ Assignment(lvalue, rvalue) =>
        val (lvTy, lvTyped) = checkLValue(lvalue, Unconstrained)
        val (rvTy, rvTyped) = checkRValue(rvalue, Is(weakenType(lvTy)))
        // check that both sides have the same type
        val ty = mostSpecific(lvTy, rvTy, a.pos)
        // if one of the sides misses a type, impose it to the other side's type
        if (lvTyped.ty == ?) lvTyped.ty = ty
        if (rvTyped.ty == ?) rvTyped.ty = ty

        Some(TyStmt.Assignment(lvTyped, rvTyped))

    case r @ Read(lvalue) =>
        val (lvTy, lvTyped) = checkLValue(lvalue, IsEither(KType.Int, KType.Char))
        assertKnownType(lvTy, r.pos)
        Some(TyStmt.Read(lvTyped))

    case p @ Print(expr) =>
        val (exprTy, exprTyped) = checkExpr(expr, Unconstrained)
        assertKnownType(exprTy, p.pos)
        Some(TyStmt.Print(exprTyped))

    case p @ Println(expr) =>
        val (exprTy, exprTyped) = checkExpr(expr, Unconstrained)
        assertKnownType(exprTy, p.pos)
        Some(TyStmt.Println(exprTyped))

    case Free(expr) =>
        val (_, exprTyped) = checkExpr(expr, IsEither(KType.Array(?), KType.Pair(?, ?)))
        Some(TyStmt.Print(exprTyped))

    case r @ Return(expr) =>
        val (exprTy, exprTyped) = checkExpr(expr, Is(retTy.getOrElse(?)))
        assertKnownType(exprTy, r.pos)
        Some(TyStmt.Return(exprTyped))

    case Exit(expr) =>
        val (_, exprTyped) = checkExpr(expr, Is(KType.Int))
        Some(TyStmt.Exit(exprTyped))
    
    case If(cond, thenStmts, elseStmts) =>
        val (_, condTyped) = checkExpr(cond, Is(KType.Bool))
        val thenTyped = thenStmts.flatMap { stmt => check(stmt, retTy) }
        val elseTyped = elseStmts.flatMap { stmt => check(stmt, retTy) }
        Some(TyStmt.If(condTyped, thenTyped, elseTyped))

    case While(cond, doStmts) =>
        val (_, condTyped) = checkExpr(cond, Is(KType.Bool))
        val doTyped = doStmts.flatMap { stmt => check(stmt, retTy) }
        Some(TyStmt.While(condTyped, doTyped))

    case Block(stmts) =>
        val blockTyped = stmts.flatMap { stmt => check(stmt, retTy) }
        Some(TyStmt.Block(blockTyped))
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
    
    case Not(x)             => checkUnary(x, KType.Bool, cons)(TyExpr.Not.apply)
    case Neg(x)             => checkUnary(x, KType.Int, cons)(TyExpr.Neg.apply)
    case Len(x)             => checkUnary(x, KType.Array(?), cons)(TyExpr.Len.apply)
    case Ord(x)             => checkUnary(x, KType.Char, cons)(TyExpr.Ord.apply)
    case Chr(x)             => checkUnary(x, KType.Int, cons)(TyExpr.Chr.apply)

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
                (Some(KType.Array(?)), TyExpr.ArrayLit(exprsTyped, KType.Array(?)))
        }
    
    case NewPair(fst, snd) =>
        val (fstTy, fstTyped) = checkExpr(fst, Unconstrained)
        val (sndTy, sndTyped) = checkExpr(snd, Unconstrained)
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
        
        kTy match {
            case Some(KType.Array(ty)) => {
                val (idxTy, idxTyped) = idx.map { idx =>
                    val (exprTy, exprTyped) = checkExpr(idx, Is(KType.Int))
                    (exprTy.getOrElse(?), exprTyped)
                }.unzip
                val finalTy = (1 until idx.length).foldLeft(ty) { (acc, _) =>
                    acc match {
                        case KType.Array(innerTy) => innerTy
                        case _ => ctx.error(TypeError.TypeMismatch(acc, KType.Array(?))((0, 0)))
                                  ?
                    }
                }
                (finalTy.satisfies(cons), TyExpr.LVal.ArrayElem(idTyped, idxTyped, finalTy))
            }
            case _ =>
                (kTy.getOrElse(?).satisfies(IsArray(?)), TyExpr.LVal.ArrayElem(idTyped, idx.map(checkExpr(_, Unconstrained)._2), ?))
        }
    
    case pairElem: PairElem => checkPairElem(pairElem, cons)
}

/** Checks the type soundness of a pair element. */
def checkPairElem(pairElem: PairElem, cons: Constraint)
                 (using TypeCheckerContext[?]): (Option[SemType], TyExpr.LVal) = pairElem match {
    case Fst(lval) =>
        val (fstTy, fstTyped) = checkLValue(lval, IsPair(?, ?))
        fstTy match {
            case Some(KType.Pair(fst, _)) => (fst.satisfies(cons), TyExpr.LVal.PairFst(fstTyped, fst))
            case _ => (fstTy.getOrElse(?).satisfies(IsPair(?, ?)), TyExpr.LVal.PairFst(fstTyped, ?))
        }
    
    case Snd(lval) =>
        val (sndTy, sndTyped) = checkLValue(lval, IsPair(?, ?))
        sndTy match {
            case Some(KType.Pair(_, snd)) => (snd.satisfies(cons), TyExpr.LVal.PairSnd(sndTyped, snd))
            case _ => (sndTy.getOrElse(?).satisfies(IsPair(?, ?)), TyExpr.LVal.PairSnd(sndTyped, ?))
        }
}

/** Checks the type soundness of a binary expression. */
def checkBinExpr(x: Expr, y: Expr, semType: SemType, cons: Constraint)
                (build: (TyExpr, TyExpr) => TyExpr)
                (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (lhsTy, xTyped) = checkExpr(x, Is(semType))
    val (rhsTy, yTyped) = checkExpr(y, lhsTy.fold(Is(semType))(Is(_)))
    // TODO: add position information
    val ty = mostSpecific(lhsTy, rhsTy, (0, 0))
    // if one of the sides misses a type, impose it to the other side's type
    if (xTyped.ty == ?) xTyped.ty = ty
    if (yTyped.ty == ?) yTyped.ty = ty

    val exprTyped = build(xTyped, yTyped)

    (exprTyped.ty.satisfies(cons), exprTyped)
}

/** Checks the type soundness of an integer or character binary expression. */
def checkIntChar(x: Expr, y: Expr, cons: Constraint)
                (build: (TyExpr, TyExpr) => TyExpr)
                (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (lhsTy, xTyped) = checkExpr(x, IsEither(KType.Int, KType.Char))
    val (_, yTyped) = checkExpr(y, lhsTy.fold(IsEither(KType.Int, KType.Char))(Is(_)))
    val exprTyped = build(xTyped, yTyped)

    (exprTyped.ty.satisfies(cons), exprTyped)
}

/** Checks the type soundness of a unary expression. */
def checkUnary(x: Expr, argTy: SemType, cons: Constraint)
              (build: (TyExpr) => TyExpr)
              (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (_, xTyped) = checkExpr(x, Is(argTy))
    val retTyped = build(xTyped)

    (retTyped.ty.satisfies(cons), retTyped)
}

/** Checks if a type satisfies a constraint. */
extension (ty: SemType) def satisfies(cons: Constraint)
                                     (using ctx: TypeCheckerContext[?]): Option[SemType] = (ty, cons) match {
    case (?, Is(refTy)) => Some(refTy)
    case (?, _) => Some(?)
    case (ty, Is(?)) => Some(ty)

    case (KType.Array(KType.Char), Is(KType.Str)) => Some(KType.Array(KType.Char))
    case (KType.Str, Is(KType.Array(KType.Char))) => Some(KType.Array(KType.Char))
    // do not allow array covariance
    case (KType.Array(kTy), Is(KType.Array(refTy))) => kTy match {
        case KType.Array(KType.Char) => ctx.error(TypeError.TypeMismatch(kTy, refTy)((0, 0)))
        case _ => Some(KType.Array(kTy.satisfies(Is(refTy)).getOrElse(?)))
    }

    // do not allow pair covariance
    case (KType.Pair(KType.Array(KType.Char), kTy2), Is(KType.Pair(KType.Str, refTy2))) =>
        ctx.error(TypeError.TypeMismatch(KType.Pair(KType.Array(KType.Char), kTy2), KType.Pair(KType.Str, refTy2))((0, 0)))
    case (KType.Pair(kTy1, KType.Array(KType.Char)), Is(KType.Pair(refTy1, KType.Str))) =>
        ctx.error(TypeError.TypeMismatch(KType.Pair(kTy1, KType.Array(KType.Char)), KType.Pair(refTy1, KType.Str))((0, 0)))
    
    case (KType.Pair(kTy1, kTy2), Is(KType.Pair(refTy1, refTy2))) =>
        val fstTy = kTy1.satisfies(Is(refTy1))
        val sndTy = kTy2.satisfies(Is(refTy2))
        Some(KType.Pair(kTy1, kTy2))

    case (kTy, Is(refTy)) =>
        if kTy == refTy then Some(kTy)
        else ctx.error(TypeError.TypeMismatch(kTy, refTy)((0, 0)))

    case (KType.Array(kTy), IsEither(KType.Array(?), KType.Pair(?, ?))) => Some(KType.Array(kTy))
    case (KType.Pair(kTy1, kTy2), IsEither(KType.Array(?), KType.Pair(?, ?))) => Some(KType.Pair(kTy1, kTy2))

    case (semTy, IsEither(ty1, ty2)) =>
        if semTy == ty1 || semTy == ty2 then Some(semTy)
        else ctx.error(TypeError.TypeMismatch(semTy, ty1)((0, 0)))

    case _ => None
}

def weakenType(semTy: Option[SemType]): SemType = semTy match {
    case Some(KType.Str) => KType.Array(KType.Char)
    case Some(semType)   => semType
    case None            => ?
}

def assertKnownType(semTy: Option[SemType], pos: Position)
                   (using ctx: TypeCheckerContext[?]): SemType = semTy match {
    case Some(?)        => ctx.error(TypeError.TypeCannotBeInfered(pos))
                           ?
    case Some(semType)  => semType
    case None           => ?
}

/** Returns the most specific type between two types. 
  * 
  * At least one of the types must be known.
*/
def mostSpecific(ty1: Option[SemType], ty2: Option[SemType], pos: Position)
                (using ctx: TypeCheckerContext[?]): SemType = (ty1, ty2) match {
    case (Some(?), Some(?)) =>
        ctx.error(TypeError.TypeCannotBeInfered(pos))
        ?
    case (Some(?), Some(t)) => t
    case (Some(t), _)       => t
    case (None, t)          => t.getOrElse(?)
}
