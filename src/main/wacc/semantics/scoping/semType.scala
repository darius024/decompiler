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
        case Array(ty: SemType, arity: Int)
        case Pair(fst: SemType, snd: SemType)
    }

    def convertType(ty: IdType | PairElemType): SemType = ty match {
        case IntType => KType.Int
        case BoolType => KType.Bool
        case CharType => KType.Char
        case StringType => KType.Str
        case ArrayType(idType, idx) => KType.Array(convertType(idType), idx)
        case PairType(fst, snd) => KType.Pair(convertType(fst), convertType(snd))
        case Pair => KType.Pair(?, ?)
    }
}
