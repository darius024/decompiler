package wacc.semantics.scoping

import wacc.syntax.types.*

/** Semantic types of the WACC language. */
object semanticTypes {
    sealed trait SemType

    /** Represents an unknown type. */
    case object ? extends SemType {
        override def toString: String = "any"
    }

    /** Represents a known type. */
    enum KType extends SemType {
        case Int
        case Bool
        case Char
        case Str
        case Array(ty: SemType, arity: Int)
        case Pair(fst: SemType, snd: SemType)

        override def toString: String = this match {
            case Int            => "int"
            case Bool           => "bool"
            case Char           => "char"
            case Str            => "string"
            case Array(ty, idx) => (ty, idx) match {
                case (?, AnyDimension) => s"any-dimensional array"
                case (?, _)            => s"$idx-dimensional array"
                case (_, _)            => s"${ty.toString}${"[]" * idx}"
            }
            case Pair(fst, snd) => (fst, snd) match {
                case (?, ?) => "pair"
                case _      => s"pair(${fst.toString}, ${snd.toString})"
            }
        }
    }

    /** Converts a syntactic type to a semantic type. */
    def convertType(ty: IdType | PairElemType): KType = ty match {
        case IntType                => KType.Int
        case BoolType               => KType.Bool
        case CharType               => KType.Char
        case StringType             => KType.Str
        case ArrayType(idType, idx) => KType.Array(convertType(idType), idx)
        case PairType(fst, snd)     => KType.Pair(convertType(fst), convertType(snd))
        case Pair                   => KType.Pair(?, ?)
    }

    /** Matches any dimension. */
    final val AnyDimension = -1
}
