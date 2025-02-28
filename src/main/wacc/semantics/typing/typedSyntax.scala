package wacc.semantics.typing

import wacc.semantics.scoping.semanticTypes.*

type TyStmtList = List[TyStmt]

/** Typed expression nodes.
  * 
  * Each node has a specific type associated with it,
  * which stores type information.
  */
sealed abstract class TyExpr(var ty: SemType)
object TyExpr {
    sealed trait Operation
    enum OpComp extends Operation {
        case Equal
        case NotEqual
        case GreaterThan
        case GreaterEqual
        case LessThan
        case LessEqual
    }
    case class BinaryComp(lhs: TyExpr, rhs: TyExpr, op: OpComp) extends TyExpr(KType.Bool)

    enum OpBool extends Operation {
        case And
        case Or
    }
    case class BinaryBool(lhs: TyExpr, rhs: TyExpr, op: OpBool) extends TyExpr(KType.Bool)

    enum OpArithmetic extends Operation {
        case Add
        case Sub
        case Mul
        case Div
        case Mod 
    }
    case class BinaryArithmetic(lhs: TyExpr, rhs: TyExpr, op: OpArithmetic) extends TyExpr(KType.Int)

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

    /** Typed l-values. */
    sealed trait LVal extends TyExpr
    case class Id(value: String, semTy: SemType) extends TyExpr(semTy) with LVal
    case class ArrayElem(id: Id, idx: List[TyExpr], semTy: SemType) extends TyExpr(semTy) with LVal
    sealed trait TyPairElem extends LVal { val lval: LVal }
    case class PairFst(lval: LVal, semTy: SemType) extends TyExpr(semTy) with TyPairElem
    case class PairSnd(lval: LVal, semTy: SemType) extends TyExpr(semTy) with TyPairElem

    case class ArrayLit(exprs: List[TyExpr], semTy: SemType) extends TyExpr(semTy)
    case class NewPair(fst: TyExpr, snd: TyExpr, fstTy: SemType, sndTy: SemType) extends TyExpr(KType.Pair(fstTy, sndTy))
    case class Call(func: String, args: List[TyExpr], retTy: SemType, argTys: List[SemType]) extends TyExpr(retTy)
}

/** Typed statement nodes. */
enum TyStmt {
    case Assignment(ref: TyExpr.LVal, expr: TyExpr)
    case Read(expr: TyExpr.LVal)
    case Free(expr: TyExpr)
    case Return(expr: TyExpr)
    case Exit(expr: TyExpr)
    case Print(expr: TyExpr)
    case Println(expr: TyExpr)
    case If(cond: TyExpr, thenStmts: TyStmtList, elseStmts: TyStmtList)
    case While(cond: TyExpr, doStmts: TyStmtList)
    case Block(stmts: TyStmtList)
}

/** Typed function and program nodes. */
case class TyFunc(name: String, params: Array[TyExpr.Id], stmts: TyStmtList)
case class TyProg(funcs: List[TyFunc], stmts: TyStmtList)
