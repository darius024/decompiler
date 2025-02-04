package wacc.semantics.typing

import cats.data.NonEmptyList
import scala.collection.mutable

import wacc.error.*
import wacc.semantics.scoping.*
import semanticTypes.*
import wacc.syntax.*
import bridges.*
import exprs.*
import stmts.*
import prog.*

import Constraint.*

/** Type constraints that can be imposed on types. */
enum Constraint {
    case Is(ty: SemType)
    case IsEither(ty1: SemType, ty2: SemType)
}
object Constraint {
    val Unconstrained = Is(?)
    def IsPair = Is(KType.Pair(?, ?))
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
    def typeOf(id: String): SemType =
        tyInfo.vars.get(id).map(_._1).getOrElse(?)
    // get the return type of the function
    def returnTypeOf(funcName: String): SemType =
        tyInfo.funcs.get(funcName).map(_._1).getOrElse(?)
    // get the signature of the function
    def signatureOf(funcName: String): List[SemType] =
        tyInfo.funcs.get(funcName).map(_._2.map(_._1)).getOrElse(Nil)

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
         (using ctx: TypeCheckerContext[?]): Option[TyStmt] = stmt match {
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
        // if one of the sides misses a type, impose the other side's type
        if (lvTyped.ty == ?) lvTyped.ty = ty
        if (rvTyped.ty == ?) rvTyped.ty = ty

        Some(TyStmt.Assignment(lvTyped, rvTyped))

    case r @ Read(lvalue) =>
        val (lvTy, lvTyped) = checkLValue(lvalue, IsEither(KType.Int, KType.Char))
        assertKnownType(lvTy, r.pos)
        Some(TyStmt.Read(lvTyped))

    case p @ Print(expr) =>
        val (exprTy, exprTyped) = checkExpr(expr, Unconstrained)
        Some(TyStmt.Print(exprTyped))

    case p @ Println(expr) =>
        val (exprTy, exprTyped) = checkExpr(expr, Unconstrained)
        Some(TyStmt.Println(exprTyped))

    case Free(expr) =>
        val (_, exprTyped) = checkExpr(expr, IsEither(KType.Array(?), KType.Pair(?, ?)))
        Some(TyStmt.Print(exprTyped))

    case r @ Return(expr) =>
        retTy match {
            case None => ctx.error(ReturnInMainBody(r.pos))
            case Some(returnTy) =>
                val (exprTy, exprTyped) = checkExpr(expr, Is(returnTy))
                Some(TyStmt.Return(exprTyped))
        }

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
        // instead of finding out the type of the array literal, impose the type within the constraint
        cons match {
            case Is(KType.Array(elemTy)) =>
                // impose the inner array type to all elements of the array literal 
                val exprsTyped = exprs.map { expr => checkExpr(expr, Is(elemTy))._2 }
                val arrayType = KType.Array(elemTy)
                (arrayType.satisfies(cons), TyExpr.ArrayLit(exprsTyped, arrayType))
            case _ =>
                // there is a type mismatch between the constraint and the array literal
                val exprsTyped = exprs.map { expr => checkExpr(expr, Unconstrained)._2 }
                // need the constraint to be an array type
                (KType.Array(?).satisfies(cons), TyExpr.ArrayLit(exprsTyped, KType.Array(?)))
        }
    
    case NewPair(fst, snd) =>
        // get the types of both sides of the pair
        val (fstTy, fstTyped) = checkExpr(fst, Unconstrained)
        val (sndTy, sndTyped) = checkExpr(snd, Unconstrained)
        val firstTy = fstTy.getOrElse(?)
        val secondTy = sndTy.getOrElse(?)
        (KType.Pair(firstTy, secondTy).satisfies(cons), TyExpr.NewPair(fstTyped, sndTyped, firstTy, secondTy))
    
    case pairElem: PairElem => checkPairElem(pairElem, cons)
    
    case Call(id, args) =>
        // retrieve the return type and the signature of the function
        val retTy = ctx.returnTypeOf(id.value)
        val argsTy = ctx.signatureOf(id.value)
        
        // check if the number of arguments match
        if (argsTy.length != args.length) {
            ctx.error(NumberArgumentsMismatch(args.length, argsTy.length)(id.pos))
        }

        // transform the arguments into typed expressions
        val (argTys, argsTyped) = args.zip(argsTy).map { (arg, argTy) =>
            // verify that the the argument types match the definition
            val (exprTy, exprTyped) = checkExpr(arg, Is(argTy))
            (exprTy.getOrElse(?), exprTyped)
        }.unzip

        // the return type must satisfy the constraint
        (retTy.satisfies(cons), TyExpr.Call(TyExpr.LVal.Id(id.value, retTy), argsTyped, argTys))
}

/** Checks the type soundness of an lvalue. */
def checkLValue(lvalue: LValue, cons: Constraint)
               (using ctx: TypeCheckerContext[?]): (Option[SemType], TyExpr.LVal) = lvalue match {
    case Id(value) =>
        // retrieve the type of the identifier
        val kTy = ctx.typeOf(value)
        (kTy.satisfies(cons), TyExpr.LVal.Id(value, kTy))
    
    case ArrayElem(id, idx) =>
        // get the type of the array
        val (baseTy, idTyped) = checkLValue(id, Unconstrained)
        
        // check that all indices are integers
        val (idxTy, idxTyped) = idx.map { idx =>
            val (exprTy, exprTyped) = checkExpr(idx, Is(KType.Int))
            (exprTy.getOrElse(?), exprTyped)
        }.unzip

        // calculate the resulting type by unwrapping the array type
        val resultTy = baseTy match {
            case Some(arrayTy) =>
                var currentTy = arrayTy
                for (_ <- idx.indices) {
                    currentTy match {
                        case KType.Array(elemTy) => currentTy = elemTy
                        case semTy => semTy.satisfies(Is(KType.Array(?)))
                    }
                }
                Some(currentTy)
            case None => None
        }

        // the unwrapped type must satisfy the constraint
        (resultTy.getOrElse(?).satisfies(cons), TyExpr.LVal.ArrayElem(idTyped, idxTyped, resultTy.getOrElse(?)))
    
    case pairElem: PairElem => checkPairElem(pairElem, cons)
}

/** Checks the type soundness of a pair element.
  * 
  * Extracts the type of the pair element and imposes the constraint on it.
  * If the pair element is not a pair, it asserts that it must have a pair type.
  */
def checkPairElem(pairElem: PairElem, cons: Constraint)
                 (using TypeCheckerContext[?]): (Option[SemType], TyExpr.LVal) = pairElem match {
    case Fst(lval) =>
        val (fstTy, fstTyped) = checkLValue(lval, IsPair)
        fstTy match {
            case Some(KType.Pair(fst, _)) => (fst.satisfies(cons), TyExpr.LVal.PairFst(fstTyped, fst))
            case _ => (fstTy.getOrElse(?).satisfies(IsPair), TyExpr.LVal.PairFst(fstTyped, ?))
        }
    
    case Snd(lval) =>
        val (sndTy, sndTyped) = checkLValue(lval, IsPair)
        sndTy match {
            case Some(KType.Pair(_, snd)) => (snd.satisfies(cons), TyExpr.LVal.PairSnd(sndTyped, snd))
            case _ => (sndTy.getOrElse(?).satisfies(IsPair), TyExpr.LVal.PairSnd(sndTyped, ?))
        }
}

/** Checks the type soundness of a binary expression. */
def checkBinExpr(x: Expr, y: Expr, semType: SemType, cons: Constraint)
                (build: (TyExpr, TyExpr) => TyExpr)
                (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (lhsTy, xTyped) = checkExpr(x, Is(semType))
    val (rhsTy, yTyped) = checkExpr(y, lhsTy.fold(Is(semType))(Is(_)))

    // if one of the sides misses a type, impose it to the other side's type
    // this is useful for the type out of a pair extraction in unknown
    // TODO: add proper position for the error
    val ty = mostSpecific(lhsTy, rhsTy, (0, 0))
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
    val (rhsTy, yTyped) = checkExpr(y, lhsTy.fold(IsEither(KType.Int, KType.Char))(Is(_)))
    val exprTyped = build(xTyped, yTyped)

    // if one of the sides misses a type, impose it to the other side's type
    // this is useful for the type out of a pair extraction in unknown
    // TODO: add proper position for the error
    val ty = mostSpecific(lhsTy, rhsTy, (0, 0))
    if (xTyped.ty == ?) xTyped.ty = ty
    if (yTyped.ty == ?) yTyped.ty = ty

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
    case (?, _)         => Some(?)
    case (ty, Is(?))    => Some(ty)

    // allow string to be coerced to array of characters
    case (KType.Array(KType.Char), Is(KType.Str)) => Some(KType.Array(KType.Char))
    case (KType.Str, Is(KType.Array(KType.Char))) => Some(KType.Str)

    // do not allow array covariance
    case (KType.Array(kTy), Is(KType.Array(refTy))) => kTy match {
        case KType.Array(KType.Char) => ctx.error(TypeMismatch(kTy, Set(refTy))((0, 0)))
        case _ => Some(KType.Array(kTy.satisfies(Is(refTy)).getOrElse(?)))
    }

    // do not allow pair covariance
    case (KType.Pair(kTy1, kTy2), Is(KType.Pair(refTy1, refTy2))) =>
        if kTy1 == KType.Array(KType.Char) && refTy1 == KType.Str
        || kTy2 == KType.Array(KType.Char) && refTy2 == KType.Str then {
            // TODO: add proper position for the error
            ctx.error(TypeMismatch(KType.Pair(kTy1, kTy2), Set(KType.Pair(refTy1, refTy2)))((0, 0)))
        }
        val fstTy = kTy1.satisfies(Is(refTy1))
        val sndTy = kTy2.satisfies(Is(refTy2))
        Some(KType.Pair(kTy1, kTy2))

    // handle the memory-allocated structures
    case (kTy @ KType.Array(_), IsEither(KType.Array(?), KType.Pair(?, ?))) => Some(kTy)
    case (kTy @ KType.Pair(_, _), IsEither(KType.Array(?), KType.Pair(?, ?))) => Some(kTy)
    case (semTy, IsEither(ty1, ty2)) =>
        if semTy == ty1 || semTy == ty2 then Some(semTy)
        // TODO: add proper position for the error
        else ctx.error(TypeMismatch(semTy, Set(ty1, ty2))((0, 0)))

    case (kTy, Is(refTy)) =>
        if kTy == refTy then Some(kTy)
        // TODO: add proper position for the error
        else ctx.error(TypeMismatch(kTy, Set(refTy))((0, 0)))
    
    case _ => None
}

/** Weakens the type of a string to an array of characters. */
def weakenType(semTy: Option[SemType]): SemType = semTy match {
    case Some(KType.Str) => KType.Array(KType.Char)
    case Some(semType)   => semType
    case None            => ?
}

/** Checks if a type is known. Types must be known. */
def assertKnownType(semTy: Option[SemType], pos: Position)
                   (using ctx: TypeCheckerContext[?]): SemType = semTy match {
    case Some(?)        => ctx.error(TypeCannotBeInfered(pos))
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
        ctx.error(TypeCannotBeInfered(pos))
        ?
    case (Some(?), Some(t)) => t
    case (Some(t), _)       => t
    case (None, t)          => t.getOrElse(?)
}
