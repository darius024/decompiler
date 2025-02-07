package wacc.semantics.typing

import wacc.error.*
import wacc.semantics.scoping.*
import semanticTypes.*
import wacc.syntax.bridges.*

import Constraint.*

/** Checks if a type satisfies a constraint. */
extension (ty: SemType) def satisfies(cons: Constraint, pos: Position)
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
                      (using ctx: TypeCheckerContext[?]): Option[SemType] = (ty, cons) match {
    // handle unknwon and unconstrained types
    case (?, Constraint.Is(refTy)) => Some(refTy)
    case (?, _)         => Some(?)
    case (ty, Constraint.Is(?))    => Some(ty)

    // check array type by unwrapping the inner type
    case (kTy @ KType.Array(_, AnyDimension), Constraint.Is(KType.Array(?, _))) => Some(kTy)
    case (kTy @ KType.Array(_, AnyDimension), Constraint.Is(KType.Array(refTy, _))) if kTy == refTy => Some(kTy)
    case (kTy @ KType.Array(_, kId), Constraint.Is(KType.Array(?, refId))) if kId >= refId => Some(kTy)
    case (kTy @ KType.Array(ty, kId), Constraint.Is(KType.Array(refTy, refId))) if kId == refId =>
        Some(KType.Array(satisfiesInvariant(ty, Constraint.Is(refTy), pos).getOrElse(?), kId))

    // check pair type by unwrapping the inner types
    case (KType.Pair(kTy1, kTy2), Constraint.Is(KType.Pair(refTy1, refTy2))) =>
        val fstTy = satisfiesInvariant(kTy1, Constraint.Is(refTy1), pos)
        val sndTy = satisfiesInvariant(kTy2, Constraint.Is(refTy2), pos)
        Some(KType.Pair(fstTy.getOrElse(?), sndTy.getOrElse(?)))

    // check if the type constraint is either of the two types
    case (kTy @ KType.Array(_, _), Constraint.IsEither(KType.Array(?, _), KType.Pair(?, ?))) => Some(kTy)
    case (kTy @ KType.Pair(_, _), Constraint.IsEither(KType.Array(?, _), KType.Pair(?, ?))) => Some(kTy)
    case (semTy, Constraint.IsEither(ty1, ty2)) =>
        if semTy == ty1 || semTy == ty2 then Some(semTy)
        else ctx.error(TypeMismatch(semTy, Set(ty1, ty2))(pos))

    case (kTy, Constraint.Is(refTy)) =>
        if kTy == refTy then Some(kTy)
        else ctx.error(TypeMismatch(kTy, Set(refTy))(pos))
    
    case _ => None
}

/** Checks if a type Constraint.Is known. Types must be known. */
def assertKnownType(semTy: Option[SemType], pos: Position)
                   (using ctx: TypeCheckerContext[?]): SemType = semTy match {
    case Some(?)        => ctx.error(TypeCannotBeInfered(pos))
                           ?
    case Some(semType)  => semType
    case None           => ?
}

/** Unifies the types of two expressions.
  *
  * Used to track the types of unknown values, like from erased pairs.
  * If one of the types Constraint.Is known, it Constraint.Is imposed to the other type.
  */
def unifyTypes(typedLhs: TyExpr, typedRhs: TyExpr, lhsTy: Option[SemType], rhsTy: Option[SemType], pos: Position)
              (using ctx: TypeCheckerContext[?]): Unit = (lhsTy, rhsTy) match {
    case (Some(?), Some(?)) => ctx.error(TypeCannotBeInfered(pos))
    case (Some(?), Some(t)) => typedLhs.ty = t
    case (Some(t), _)       => typedRhs.ty = t
    case (_, _)             => ()
}

/** Flattens an array type. */
def wrapArrayType(semType: SemType): SemType = semType match {
    case KType.Array(KType.Array(ty, idxIn), idxOut) => KType.Array(ty, idxIn + idxOut)
    case ty => ty
}

/** Unwraps an array type. */
def unwrapArrayType(semType: Option[SemType], arity: Int): SemType = semType match {
    case Some(KType.Array(ty, idx)) if idx > arity  => KType.Array(ty, idx - arity)
    case Some(KType.Array(ty, idx)) if idx == arity => ty
    case Some(ty)                                   => ty
    case None                                       => ?
}
