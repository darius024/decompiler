package wacc.semantics.typing

import wacc.error.*
import wacc.semantics.scoping.*
import semanticTypes.*
import wacc.syntax.bridges.*

import Constraint.*

/** Checks if a type satisfies a constraint. */
extension (ty: SemType) def satisfies(cons: Constraint, pos: Position)
                                     (using funcScope: String)
                                     (using ctx: TypeCheckerContext[?]): Option[SemType] = (ty, cons) match {
    // allow for string and char[] type coercion
    case (kTy @ KType.Array(KType.Char, 1), Constraint.Is(KType.Str)) => Some(kTy)
    case _ => satisfiesInvariant(ty, cons, pos)
}

/** Checks if a type satisfies a constraint invariantly in the subtypes.
  * 
  * Used to disallow variance in the array and pair types. 
  */
def satisfiesInvariant(ty: SemType, cons: Constraint, pos: Position)
                      (using funcScope: String)
                      (using ctx: TypeCheckerContext[?]): Option[SemType] = (ty, cons) match {
    // handle unknwon and unconstrained types
    case (?, Constraint.Is(refTy)) => Some(refTy)
    case (?, _)                    => Some(?)
    case (ty, Constraint.Is(?))    => Some(ty)

    // check array type by unwrapping the inner type
    case (KType.Array(ty, kId), Constraint.Is(KType.Array(refTy, refId)))
        if kId >= refId && matchType(ty, refTy) => Some(KType.Array(moreSpecific(ty, refTy), kId))

    // check pair type by unwrapping the inner types
    case (kTy @ KType.Pair(KType.Pair(ty1, ty2), ty3), Constraint.Is(KType.Pair(KType.Pair(?, ?), _))) => Some(kTy)
    case (kTy @ KType.Pair(ty1, KType.Pair(ty2, ty3)), Constraint.Is(KType.Pair(_, KType.Pair(?, ?)))) => Some(kTy)
    case (KType.Pair(kTy1, kTy2), Constraint.Is(KType.Pair(refTy1, refTy2)))
        if matchType(kTy1, refTy1) && matchType(kTy2, refTy2) => Some(KType.Pair(moreSpecific(kTy1, refTy1), moreSpecific(kTy2, refTy2)))

    // check if the type constraint is either of the two types
    case (kTy @ KType.Array(ty, _), Constraint.IsEither(KType.Array(refTy, _), KType.Pair(?, ?)))
        if matchType(ty, refTy) => Some(kTy)
    case (kTy @ KType.Pair(ty1, ty2), Constraint.IsEither(KType.Array(_, _), KType.Pair(refTy1, refTy2)))
        if matchType(ty1, refTy1) && matchType(ty2, refTy2) => Some(kTy)
    case (semTy, Constraint.IsEither(ty1, ty2)) =>
        if semTy == ty1 || semTy == ty2 then Some(semTy)
        else ctx.error(TypeMismatch(semTy, Set(ty1, ty2))(funcScope, pos))

    case (kTy, Constraint.Is(refTy)) =>
        if kTy == refTy then Some(kTy)
        else ctx.error(TypeMismatch(kTy, Set(refTy))(funcScope, pos))
    
    case _ => None
}

/** Checks if the two types match. */
def matchType(ty: SemType, refTy: SemType): Boolean = (ty, refTy) match {
    case (_, ?) => true
    case (?, _) => true
    case (KType.Array(ty1, AnyDimension), KType.Array(refTy1, _))              => matchType(ty1, refTy1)
    case (KType.Array(ty1, idx), KType.Array(refTy1, refIdx)) if idx == refIdx => matchType(ty1, refTy1)
    case (KType.Pair(ty1, ty2), KType.Pair(refTy1, refTy2))                    => matchType(ty1, refTy1) && matchType(ty2, refTy2)
    case (_, _) => false
}

/** Checks if a type Constraint.Is known. Types must be known. */
def assertKnownType(semTy: Option[SemType], pos: Position)
                   (using funcScope: String)
                   (using ctx: TypeCheckerContext[?]): SemType = semTy match {
    case Some(?)       => ctx.error(TypeCannotBeInfered(funcScope, pos))
                          ?
    case Some(semType) => semType
    case None          => ?
}

/** Unifies the types of two expressions.
  *
  * Used to track the types of unknown values, like from erased pairs.
  * If one of the types Constraint.Is known, it Constraint.Is imposed to the other type.
  */
def unifyTypes(typedLhs: TyExpr, typedRhs: TyExpr, lhsTy: Option[SemType], rhsTy: Option[SemType], pos: Position)
              (using funcScope: String)
              (using ctx: TypeCheckerContext[?]): Unit = (lhsTy, rhsTy) match {
    case (Some(?), Some(?)) => ctx.error(TypeCannotBeInfered(funcScope, pos))
    case (Some(?), Some(t)) => typedLhs.ty = t
    case (Some(t), _)       => typedRhs.ty = t
    case (_, _)             => ()
}

/** Chooses the more specific type out of the two. */
def moreSpecific(ty1: SemType, ty2: SemType): SemType = (ty1, ty2) match {
    case (KType.Pair(kTy1, kTy2), KType.Pair(refTy1, refTy2)) =>
        KType.Pair(moreSpecific(kTy1, refTy1), moreSpecific(kTy2, refTy2))
    case (KType.Array(ty1, id), KType.Array(refTy1, _)) =>
        KType.Array(moreSpecific(ty1, refTy1), id)

    case (ty, ?) => ty
    case (?, ty) => ty
    case (ty, _) => ty
}

/** Flattens an array type. */
def wrapArrayType(semType: SemType): SemType = semType match {
    case KType.Array(KType.Array(ty, idxIn), idxOut) => KType.Array(ty, idxIn + idxOut)
    case ty                                          => ty
}

/** Unwraps an array type. */
def unwrapArrayType(semType: Option[SemType], arity: Int): SemType = semType match {
    case Some(KType.Array(ty, idx)) if idx > arity  => KType.Array(ty, idx - arity)
    case Some(KType.Array(ty, idx)) if idx == arity => ty
    case Some(ty)                                   => ty
    case None                                       => ?
}
