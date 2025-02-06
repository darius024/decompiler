package wacc.semantics.typing

import cats.data.NonEmptyList
import scala.collection.mutable

import wacc.error.*
import wacc.semantics.scoping.*
import semanticTypes.*
import wacc.syntax.*
import exprs.*
import stmts.*
import prog.*

import Constraint.*

/** Type constraints that can be imposed on types. */
enum Constraint(val ty: SemType) {
    case Is(typ: SemType) extends Constraint(typ)
    case IsEither(ty1: SemType, ty2: SemType) extends Constraint(ty1)
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
    val typedStmts = stmts.toList.flatMap(check(_, None))

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
    val typedParams = params.map { (_, id) => checkLValue(id, Unconstrained)._2 }
    val typedStmts = stmts.toList.flatMap(check(_, Some(retTy)))

    TyFunc(TyExpr.LVal.Id(funcName.value, retTy), typedParams, typedStmts)
}

/** Checks the type soundness of a statement.
  * 
  * Uses the type parameter to verify the return values of statements.
  */
def check(stmt: Stmt, retTy: Option[SemType])
         (using ctx: TypeCheckerContext[?]): Option[TyStmt] = stmt match {
    case Skip => None

    case Declaration((_, id), rvalue) =>
        val (idTy, typedId) = checkLValue(id, Unconstrained)
        val (_, typedRv) = checkRValue(rvalue, Is(idTy.getOrElse(?)))
        Some(TyStmt.Assignment(typedId, typedRv))

    case Assignment(lvalue, rvalue) =>
        val (lvTy, typedLv) = checkLValue(lvalue, Unconstrained)
        val (rvTy, typedRv) = checkRValue(rvalue, Is(lvTy.getOrElse(?)))

        // check that both sides have the same type
        unifyTypes(typedLv, typedRv, lvTy, rvTy, stmt.pos)

        Some(TyStmt.Assignment(typedLv, typedRv))

    case Read(lvalue) =>
        val (lvTy, typedLv) = checkLValue(lvalue, IsEither(KType.Int, KType.Char))
        assertKnownType(lvTy, stmt.pos)
        Some(TyStmt.Read(typedLv))

    case Print(expr) =>
        val (_, typedExpr) = checkExpr(expr, Unconstrained)
        Some(TyStmt.Print(typedExpr))

    case Println(expr) =>
        val (_, typedExpr) = checkExpr(expr, Unconstrained)
        Some(TyStmt.Println(typedExpr))

    case Free(expr) =>
        val (_, typedExpr) = checkExpr(expr, IsEither(KType.Array(?), KType.Pair(?, ?)))
        Some(TyStmt.Print(typedExpr))

    case Return(expr) =>
        retTy match {
            case None => ctx.error(ReturnInMainBody(stmt.pos))
            case Some(returnTy) =>
                val (_, typedExpr) = checkExpr(expr, Is(returnTy))
                Some(TyStmt.Return(typedExpr))
        }

    case Exit(expr) =>
        val (_, typedExpr) = checkExpr(expr, Is(KType.Int))
        Some(TyStmt.Exit(typedExpr))
    
    case If(cond, thenStmts, elseStmts) =>
        val (_, condTyped) = checkExpr(cond, Is(KType.Bool))
        val thenTyped = thenStmts.toList.flatMap(check(_, retTy))
        val elseTyped = elseStmts.toList.flatMap(check(_, retTy))
        Some(TyStmt.If(condTyped, thenTyped, elseTyped))

    case While(cond, doStmts) =>
        val (_, condTyped) = checkExpr(cond, Is(KType.Bool))
        val doTyped = doStmts.toList.flatMap(check(_, retTy))
        Some(TyStmt.While(condTyped, doTyped))

    case Block(stmts) =>
        val blockTyped = stmts.toList.flatMap(check(_, retTy))
        Some(TyStmt.Block(blockTyped))
}

/** Checks the type soundness of an expression.
  * 
  * Imposes specific constraints on the types of the operations.
  */
def checkExpr(expr: Expr, cons: Constraint)
             (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = expr match {
    case Or(lhs, rhs)           => checkBinExpr(lhs, rhs, KType.Bool, cons)(TyExpr.Or.apply)
    case And(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Bool, cons)(TyExpr.And.apply)
    
    case Equal(lhs, rhs)        => checkBinExpr(lhs, rhs, ?, cons)(TyExpr.Equal.apply)
    case NotEqual(lhs, rhs)     => checkBinExpr(lhs, rhs, ?, cons)(TyExpr.NotEqual.apply)
    
    case Greater(lhs, rhs)      => checkIntChar(lhs, rhs, cons)(TyExpr.Greater.apply)
    case GreaterEq(lhs, rhs)    => checkIntChar(lhs, rhs, cons)(TyExpr.GreaterEq.apply)
    case Less(lhs, rhs)         => checkIntChar(lhs, rhs, cons)(TyExpr.Less.apply)
    case LessEq(lhs, rhs)       => checkIntChar(lhs, rhs, cons)(TyExpr.LessEq.apply)
    
    case Add(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Int, cons)(TyExpr.Add.apply)
    case Sub(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Int, cons)(TyExpr.Sub.apply)
    case Mul(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Int, cons)(TyExpr.Mul.apply)
    case Div(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Int, cons)(TyExpr.Div.apply)
    case Mod(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Int, cons)(TyExpr.Mod.apply)
    
    case Not(expr)              => checkUnary(expr, KType.Bool, cons)(TyExpr.Not.apply)
    case Neg(expr)              => checkUnary(expr, KType.Int, cons)(TyExpr.Neg.apply)
    case Len(expr)              => checkUnary(expr, KType.Array(?), cons)(TyExpr.Len.apply)
    case Ord(expr)              => checkUnary(expr, KType.Char, cons)(TyExpr.Ord.apply)
    case Chr(expr)              => checkUnary(expr, KType.Int, cons)(TyExpr.Chr.apply)

    case IntLit(value)          => (KType.Int.satisfies(cons, expr.pos), TyExpr.IntLit(value))
    case BoolLit(value)         => (KType.Bool.satisfies(cons, expr.pos), TyExpr.BoolLit(value))
    case CharLit(value)         => (KType.Char.satisfies(cons, expr.pos), TyExpr.CharLit(value))
    case StrLit(value)          => (KType.Str.satisfies(cons, expr.pos), TyExpr.StrLit(value))
    case PairLit                => (KType.Pair(?, ?).satisfies(cons, expr.pos), TyExpr.PairLit)
    case id: Id                 => checkLValue(id, cons)
    case arrElem: ArrayElem     => checkLValue(arrElem, cons)
    case ParensExpr(expr)       => checkExpr(expr, cons)
}

/** Checks the type soundness of an rvalue. */
def checkRValue(rvalue: RValue, cons: Constraint)
               (using ctx: TypeCheckerContext[?]): (Option[SemType], TyExpr) = rvalue match {
    case expr: Expr => checkExpr(expr, cons)
        
    case ArrayLit(exprs) =>
        // instead of finding out the type of the array literal, impose the type within the constraint
        val elemTy = cons match {
            case Is(KType.Array(elTy)) => elTy
            case Is(KType.Str) => KType.Char
            case _ => ?
        }
        // impose the inner array type to all elements of the array literal 
        // or impose no constraint if an array literal is not expected
        val typedExprs = exprs.map(checkExpr(_, Is(elemTy))._2)
        (KType.Array(elemTy).satisfies(cons, rvalue.pos), TyExpr.ArrayLit(typedExprs, KType.Array(elemTy)))
    
    case NewPair(fst, snd) =>
        // get the types of both sides of the pair
        val (fstTy, typedFst) = checkExpr(fst, Unconstrained)
        val (sndTy, typedSnd) = checkExpr(snd, Unconstrained)
        val firstTy = fstTy.getOrElse(?)
        val secondTy = sndTy.getOrElse(?)
        (KType.Pair(firstTy, secondTy).satisfies(cons, rvalue.pos), TyExpr.NewPair(typedFst, typedSnd, firstTy, secondTy))
    
    case pairElem: PairElem => checkPairElem(pairElem, cons)
    
    case Call(func, args) =>
        // retrieve the return type and the signature of the function
        val retTy = ctx.returnTypeOf(func.value)
        val argsTy = ctx.signatureOf(func.value)
        
        // check if the number of arguments match
        if (argsTy.length != args.length) {
            ctx.error(NumberArgumentsMismatch(args.length, argsTy.length)(func.pos))
        }

        // transform the arguments into typed expressions
        val (argTys, typedArgs) = (args zip argsTy).map { (arg, argTy) =>
            // verify that the the argument types match the definition
            val (exprTy, typedExpr) = checkExpr(arg, Is(argTy))
            (exprTy.getOrElse(?), typedExpr)
        }.unzip

        // the return type must satisfy the constraint
        (retTy.satisfies(cons, func.pos), TyExpr.Call(TyExpr.LVal.Id(func.value, retTy), typedArgs, argTys))
}

/** Checks the type soundness of an lvalue. */
def checkLValue(lvalue: LValue, cons: Constraint)
               (using ctx: TypeCheckerContext[?]): (Option[SemType], TyExpr.LVal) = lvalue match {
    case Id(value) =>
        // retrieve the type of the identifier
        val kTy = ctx.typeOf(value)
        (kTy.satisfies(cons, lvalue.pos), TyExpr.LVal.Id(value, kTy))
    
    case ArrayElem(id, idx) =>
        // get the type of the array
        val (baseTy, typedId) = checkLValue(id, Unconstrained)
        
        // check that all indices are integers
        val (idxTy, typedIdx) = idx.map { idx =>
            val (exprTy, typedExpr) = checkExpr(idx, Is(KType.Int))
            (exprTy.getOrElse(?), typedExpr)
        }.unzip

        // calculate the resulting type by unwrapping the array type
        val resultTy = baseTy.collect { arrayTy =>
            idx.foldLeft(arrayTy) {
                case (KType.Array(elemTy), _) => elemTy
                case (semTy, _) => semTy.satisfies(Is(KType.Array(cons.ty)), lvalue.pos).getOrElse(?)
            }
        }

        // the unwrapped type must satisfy the constraint
        (resultTy.getOrElse(?).satisfies(cons, lvalue.pos), TyExpr.LVal.ArrayElem(typedId, typedIdx, resultTy.getOrElse(?)))
    
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
        val (fstTy, typedFst) = checkLValue(lval, IsPair)
        fstTy match {
            case Some(KType.Pair(fst, _)) => (fst.satisfies(cons, lval.pos), TyExpr.LVal.PairFst(typedFst, fst))
            case _ => (fstTy.getOrElse(?).satisfies(IsPair, lval.pos), TyExpr.LVal.PairFst(typedFst, ?))
        }
    
    case Snd(lval) =>
        val (sndTy, typedSnd) = checkLValue(lval, IsPair)
        sndTy match {
            case Some(KType.Pair(_, snd)) => (snd.satisfies(cons, lval.pos), TyExpr.LVal.PairSnd(typedSnd, snd))
            case _ => (sndTy.getOrElse(?).satisfies(IsPair, lval.pos), TyExpr.LVal.PairSnd(typedSnd, ?))
        }
}

/** Checks the type soundness of a binary expression. */
def checkBinExpr(lhs: Expr, rhs: Expr, semType: SemType, cons: Constraint)
                (build: (TyExpr, TyExpr) => TyExpr)
                (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (lhsTy, typedLhs) = checkExpr(lhs, Is(semType))
    val (rhsTy, typedRhs) = checkExpr(rhs, lhsTy.fold(Is(semType))(Is(_)))
    val typedExpr = build(typedLhs, typedRhs)

    unifyTypes(typedLhs, typedRhs, lhsTy, rhsTy, lhs.pos)

    (typedExpr.ty.satisfies(cons, lhs.pos), typedExpr)
}

/** Checks the type soundness of an integer or character binary expression. */
def checkIntChar(lhs: Expr, rhs: Expr, cons: Constraint)
                (build: (TyExpr, TyExpr) => TyExpr)
                (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (lhsTy, typedLhs) = checkExpr(lhs, IsEither(KType.Int, KType.Char))
    val (rhsTy, typedRhs) = checkExpr(rhs, lhsTy.fold(IsEither(KType.Int, KType.Char))(Is(_)))
    val typedExpr = build(typedLhs, typedRhs)

    unifyTypes(typedLhs, typedRhs, lhsTy, rhsTy, lhs.pos)

    (typedExpr.ty.satisfies(cons, lhs.pos), typedExpr)
}

/** Checks the type soundness of a unary expression. */
def checkUnary(expr: Expr, argTy: SemType, cons: Constraint)
              (build: (TyExpr) => TyExpr)
              (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (_, typedExpr) = checkExpr(expr, Is(argTy))
    val typedUExpr = build(typedExpr)

    (typedUExpr.ty.satisfies(cons, expr.pos), typedExpr)
}
