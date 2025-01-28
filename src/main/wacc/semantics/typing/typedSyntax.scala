package wacc.semantics.typing

import wacc.semantics.scoping.semanticTypes.*

sealed abstract class TyExpr(val ty: SemType)
object TyExpr {
    case class Or(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Bool)
    case class And(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Bool)

    case class Equal(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Bool)
    case class NotEqual(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Bool)

    case class Greater(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Bool)
    case class GreaterEqual(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Bool)
    case class Less(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Bool)
    case class LessEqual(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Bool)

    case class Add(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Int)
    case class Sub(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Int)

    case class Mul(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Int)
    case class Div(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Int)
    case class Mod(lhs: TyExpr, rhs: TyExpr) extends TyExpr(KType.Int)

    case class Not(expr: TyExpr) extends TyExpr(KType.Bool)
    case class Neg(expr: TyExpr) extends TyExpr(KType.Int)
    case class Len(expr: TyExpr) extends TyExpr(KType.Int)
    case class Ord(expr: TyExpr) extends TyExpr(KType.Int)
    case class Chr(expr: TyExpr) extends TyExpr(KType.Char)

    case class IntLit(value: Int) extends TyExpr(KType.Int)
    case class BoolLit(value: Boolean) extends TyExpr(KType.Bool)
    case class CharLit(value: Char) extends TyExpr(KType.Char)
    case class StrLit(value: String) extends TyExpr(KType.Str)
    case object PairLit extends TyExpr(KType.Pair(?, ?))

    enum LVal(ty: SemType) extends TyExpr(ty) {
        case Id(value: String, kTy: KType) extends LVal(kTy)
        case ArrayElem(lVal: LVal, idx: TyExpr, kTy: SemType) extends LVal(kTy)
        case PairFst(lval: LVal, kTy: SemType) extends LVal(kTy)
        case PairSnd(lval: LVal, kTy: SemType) extends LVal(kTy)
    }

    case class ArrayLit(exprs: List[TyExpr], kTy: SemType) extends TyExpr(kTy)
    case class NewPair(fst: TyExpr, snd: TyExpr, fstTy: SemType, sndTy: SemType) extends TyExpr(KType.Pair(fstTy, sndTy))
    case class Call(func: LVal.Id, args: List[TyExpr], retTy: SemType, argTys: List[SemType]) extends TyExpr(KType.Func(retTy, argTys))
}

enum TyStmt {
    case Assignment(ref: TyExpr.LVal, expr: TyExpr)
    case Read(expr: TyExpr.LVal)
    case Free(expr: TyExpr)
    case Return(expr: TyExpr)
    case Exit(expr: TyExpr)
    case Print(expr: TyExpr)
    case Println(expr: TyExpr)
    case If(cond: TyExpr, thenStmts: List[TyStmt], elseStmts: List[TyStmt])
    case While(cond: TyExpr, doStmts: List[TyStmt])
    case Block(stmts: List[TyStmt])
}

case class TyFunc(id: TyExpr.LVal.Id, params: List[TyExpr.LVal.Id], stmts: List[TyStmt])
case class TyProg(funcs: List[TyFunc], stmts: List[TyStmt])
