package wacc.semantics.scoping

import wacc.syntax.types.*

object semanticTypes {
    sealed trait SemType

    case object ? extends SemType
    enum KType extends SemType {
        case Int
        case Bool
        case Char
        case Str
        case Array(ty: SemType)
        case Pair(fst: SemType, snd: SemType)
        case Func(retTy: SemType, argsTy: List[SemType])
    }

    def convertType(ty: IdType | PairElemType): KType = ty match {
        case IntType => KType.Int
        case BoolType => KType.Bool
        case CharType => KType.Char
        case StringType => KType.Str
        case at @ ArrayType(idType, idx) => idx match {
            case 1 => KType.Array(convertType(idType))
            case _ => convertType(ArrayType(idType, idx - 1)(at.pos))
        }
        case PairType(fst, snd) => KType.Pair(convertType(fst), convertType(snd))
        case Pair => KType.Pair(?, ?)
    }
}
