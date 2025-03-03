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
enum Constraint(val ty: SemType) {
    case Is(typ: SemType) extends Constraint(typ)
    case IsEither(ty1: SemType, ty2: SemType) extends Constraint(ty1)
}
object Constraint {
    val Unconstrained = Is(?)
    def IsPair = Is(KType.Pair(?, ?))
}

/** Type information received from the scope-checking phase. */
class TypeInfo(
    val funcs: Map[String, FuncInfo],
    val vars: Map[String, IdInfo],
    val errs: List[PartialSemanticError]
)

/** Context for the type checker.
  * 
  * It keeps track of the type information and errors.
  * For identifiers out of scope, it returns unknown types.
  */
class TypeCheckerContext[C](tyInfo: TypeInfo, errs: mutable.Builder[PartialSemanticError, C]) {
    def errors: C = errs.result()

    // get the type of the identifier
    def typeOf(id: String): SemType =
        tyInfo.vars.get(id).map(_._1).getOrElse(?)
    // get the return type of the function
    def returnTypeOf(funcName: String): SemType =
        tyInfo.funcs.get(funcName).map(_._1).getOrElse(?)
    // get the signature of the function
    def signatureOf(funcName: String): Array[KType] =
        tyInfo.funcs.get(funcName).map(_._2.map(_._1)).getOrElse(Array.empty[KType])

    // add an error to the context
    def error(err: PartialSemanticError) = {
        errs += err
        None
    }
}

/** Checks the type soundness of a WACC program.
  *
  * It verifies that all types match within statements.
  */
def typeCheck(prog: Program, tyInfo: TypeInfo): Either[NonEmptyList[PartialSemanticError], TyProg] = {
    // initialise the context
    given ctx: TypeCheckerContext[List[PartialSemanticError]] =
        TypeCheckerContext(tyInfo, List.newBuilder)

    tyInfo.errs.foreach(ctx.error)
    
    val Program(funcs, stmts) = prog
    val typedFuncs = funcs.map(check)
    given funcScope: String = "main"
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
    given funcScope: String = funcName.value

    val retTy = ctx.returnTypeOf(funcName.value)
    val typedParams = params.map { (_, id) => checkId(id, Unconstrained)._2 }
    val typedStmts = stmts.toList.flatMap(check(_, Some(retTy)))

    TyFunc(funcName.value, typedParams, typedStmts)
}

/** Checks the type soundness of a statement.
  * 
  * Uses the type parameter to verify the return values of statements.
  */
def check(stmt: Stmt, retTy: Option[SemType])
         (using funcScope: String)
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
        unifyTypes(typedLv, typedRv, lvTy, rvTy, rvalue.pos)

        Some(TyStmt.Assignment(typedLv, typedRv))

    case Read(lvalue) =>
        val (lvTy, typedLv) = checkLValue(lvalue, IsEither(KType.Int, KType.Char))
        assertKnownType(lvTy, lvalue.pos)
        Some(TyStmt.Read(typedLv))

    case Print(expr) =>
        val (_, typedExpr) = checkExpr(expr, Unconstrained)
        Some(TyStmt.Print(typedExpr))

    case Println(expr) =>
        val (_, typedExpr) = checkExpr(expr, Unconstrained)
        Some(TyStmt.Println(typedExpr))

    case Free(expr) =>
        val (_, typedExpr) = checkExpr(expr, IsEither(KType.Array(?, AnyDimension), KType.Pair(?, ?)))
        Some(TyStmt.Free(typedExpr))

    case Return(expr) =>
        retTy match {
            case None => ctx.error(ReturnInMainBody(funcScope, expr.pos))
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
             (using funcScope: String)
             (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = expr match {
    case Or(lhs, rhs)           => checkBinExpr(lhs, rhs, KType.Bool, TyExpr.OpBool.Or, cons, expr.pos)
    case And(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Bool, TyExpr.OpBool.And, cons, expr.pos)
    
    case Equal(lhs, rhs)        => checkBinExpr(lhs, rhs, ?, TyExpr.OpComp.Equal, cons, expr.pos)
    case NotEqual(lhs, rhs)     => checkBinExpr(lhs, rhs, ?, TyExpr.OpComp.NotEqual, cons, expr.pos)
    
    case Greater(lhs, rhs)      => checkIntChar(lhs, rhs, TyExpr.OpComp.GreaterThan, cons, expr.pos)
    case GreaterEq(lhs, rhs)    => checkIntChar(lhs, rhs, TyExpr.OpComp.GreaterEqual, cons, expr.pos)
    case Less(lhs, rhs)         => checkIntChar(lhs, rhs, TyExpr.OpComp.LessThan, cons, expr.pos)
    case LessEq(lhs, rhs)       => checkIntChar(lhs, rhs, TyExpr.OpComp.LessEqual, cons, expr.pos)
    
    case Add(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Int, TyExpr.OpArithmetic.Add, cons, expr.pos)
    case Sub(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Int, TyExpr.OpArithmetic.Sub, cons, expr.pos)
    case Mul(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Int, TyExpr.OpArithmetic.Mul, cons, expr.pos)
    case Div(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Int, TyExpr.OpArithmetic.Div, cons, expr.pos)
    case Mod(lhs, rhs)          => checkBinExpr(lhs, rhs, KType.Int, TyExpr.OpArithmetic.Mod, cons, expr.pos)
    
    case Not(exp)               => checkUnary(exp, KType.Bool, cons, expr.pos)(TyExpr.Not.apply)
    case Neg(exp)               => checkUnary(exp, KType.Int, cons, expr.pos)(TyExpr.Neg.apply)
    case Len(exp)               => checkUnary(exp, KType.Array(?, AnyDimension), cons, expr.pos)(TyExpr.Len.apply)
    case Ord(exp)               => checkUnary(exp, KType.Char, cons, expr.pos)(TyExpr.Ord.apply)
    case Chr(exp)               => checkUnary(exp, KType.Int, cons, expr.pos)(TyExpr.Chr.apply)

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
               (using funcScope: String)
               (using ctx: TypeCheckerContext[?]): (Option[SemType], TyExpr) = rvalue match {
    case expr: Expr => checkExpr(expr, cons)
        
    case ArrayLit(exprs) =>
        // instead of finding out the type of the array literal, impose the type on the left as a constraint
        val elemTy = cons match {
            case Is(KType.Array(elTy, 1))   => elTy 
            case Is(KType.Array(elTy, idx)) => KType.Array(elTy, idx - 1)
            case Is(KType.Str)              => KType.Char
            case _                          => ?
        }
        // impose the inner array type to all elements of the array literal 
        // or impose no constraint if an array literal is not expected
        val typedExprs = exprs.map(checkExpr(_, Is(elemTy))._2)

        val outerTy = wrapArrayType(KType.Array(elemTy, 1))
        (outerTy.satisfies(cons, rvalue.pos), TyExpr.ArrayLit(typedExprs, outerTy))

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
            ctx.error(NumberArgumentsMismatch(args.length, argsTy.length)(funcScope, func.pos))
        }

        // transform the arguments into typed expressions
        val (argTys, typedArgs) = (args zip argsTy).map { (arg, argTy) =>
            // verify that the the argument types match the definition
            val (exprTy, typedExpr) = checkExpr(arg, Is(argTy))
            (exprTy.getOrElse(?), typedExpr)
        }.unzip

        // the return type must satisfy the constraint
        (retTy.satisfies(cons, func.pos), TyExpr.Call(func.value, typedArgs, retTy, argTys))
}

/** Checks the type soundness of an identifier. */
def checkId(id: Id, cons: Constraint)
           (using funcScope: String)
           (using ctx: TypeCheckerContext[?]): (Option[SemType], TyExpr.Id) = {
    // retrieve the type of the identifier
    val kTy = ctx.typeOf(id.value)
    (kTy.satisfies(cons, id.pos), TyExpr.Id(id.value, kTy))
}

/** Checks the type soundness of an lvalue. */
def checkLValue(lvalue: LValue, cons: Constraint)
               (using funcScope: String)
               (using ctx: TypeCheckerContext[?]): (Option[SemType], TyExpr.LVal) = lvalue match {
    case id @ Id(value) => checkId(id, cons)
    
    case ArrayElem(id, idx) =>
        // get the type of the array
        val (baseTy, typedId) = checkId(id, Is(wrapArrayType(KType.Array(cons.ty, idx.length))))
        
        // check that all indices are integers
        val (idxTy, typedIdx) = idx.map { idx =>
            val (exprTy, typedExpr) = checkExpr(idx, Is(KType.Int))
            (exprTy.getOrElse(?), typedExpr)
        }.unzip

        // unwrap the array by `idx` dimensions
        val innerTy = unwrapArrayType(baseTy, idx.length)

        // the unwrapped type must satisfy the constraint
        (innerTy.satisfies(cons, lvalue.pos), TyExpr.ArrayElem(typedId, typedIdx, innerTy))
    
    case pairElem: PairElem => checkPairElem(pairElem, cons)
}

/** Checks the type soundness of a pair element.
  * 
  * Extracts the type of the pair element and imposes the constraint on it.
  * If the pair element is not a pair, it asserts that it must have a pair type.
  */
def checkPairElem(pairElem: PairElem, cons: Constraint)
                 (using funcScope: String)
                 (using TypeCheckerContext[?]): (Option[SemType], TyExpr.LVal) = pairElem match {
    case Fst(lval) =>
        val (fstTy, typedFst) = checkLValue(lval, IsPair)
        fstTy match {
            case Some(KType.Pair(fst, _)) => (fst.satisfies(cons, lval.pos), TyExpr.PairFst(typedFst, fst))
            case _ => (fstTy.getOrElse(?).satisfies(IsPair, lval.pos), TyExpr.PairFst(typedFst, ?))
        }
    
    case Snd(lval) =>
        val (sndTy, typedSnd) = checkLValue(lval, IsPair)
        sndTy match {
            case Some(KType.Pair(_, snd)) => (snd.satisfies(cons, lval.pos), TyExpr.PairSnd(typedSnd, snd))
            case _ => (sndTy.getOrElse(?).satisfies(IsPair, lval.pos), TyExpr.PairSnd(typedSnd, ?))
        }
}

/** Checks the type soundness of a binary expression. */
def checkBinExpr(lhs: Expr, rhs: Expr, semType: SemType, op: TyExpr.Operation,
                 cons: Constraint, pos: Position)
                (using funcScope: String)
                (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (lhsTy, typedLhs) = checkExpr(lhs, Is(semType))
    val (rhsTy, typedRhs) = checkExpr(rhs, lhsTy.fold(Is(semType))(Is(_)))
    val typedExpr = op match {
        case oper: TyExpr.OpComp       => TyExpr.BinaryComp(typedLhs, typedRhs, oper)
        case oper: TyExpr.OpBool       => TyExpr.BinaryBool(typedLhs, typedRhs, oper)
        case oper: TyExpr.OpArithmetic => TyExpr.BinaryArithmetic(typedLhs, typedRhs, oper)
    }

    unifyTypes(typedLhs, typedRhs, lhsTy, rhsTy, pos)

    (typedExpr.ty.satisfies(cons, pos), typedExpr)
}

/** Checks the type soundness of an integer or character binary expression. */
def checkIntChar(lhs: Expr, rhs: Expr, op: TyExpr.OpComp,
                 cons: Constraint, pos: Position)
                (using funcScope: String)
                (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (lhsTy, typedLhs) = checkExpr(lhs, IsEither(KType.Int, KType.Char))
    val (rhsTy, typedRhs) = checkExpr(rhs, lhsTy.fold(IsEither(KType.Int, KType.Char))(Is(_)))
    val typedExpr = TyExpr.BinaryComp(typedLhs, typedRhs, op)

    unifyTypes(typedLhs, typedRhs, lhsTy, rhsTy, pos)

    (typedExpr.ty.satisfies(cons, pos), typedExpr)
}

/** Checks the type soundness of a unary expression. */
def checkUnary(expr: Expr, argTy: SemType, cons: Constraint, pos: Position)
              (build: (TyExpr) => TyExpr)
              (using funcScope: String)
              (using TypeCheckerContext[?]): (Option[SemType], TyExpr) = {
    val (_, typedExpr) = checkExpr(expr, Is(argTy))
    val typedUExpr = build(typedExpr)

    (typedUExpr.ty.satisfies(cons, pos), typedUExpr)
}
