package wacc.semantics.scoping

import wacc.syntax.types.*

/** Semantic types of the WACC language. */
object semanticTypes {
    sealed trait SemType

    /** Represents an unknown type. */
    case object ? extends SemType

    /** Represents a known type. */
    enum KType extends SemType {
        case Int
        case Bool
        case Char
        case Str
        case Array(ty: SemType)
        case Pair(fst: SemType, snd: SemType)
        case Func(retTy: SemType, argsTy: List[SemType])

        override def toString: String = this match {
            case Int          => "int"
            case Bool         => "bool"
            case Char         => "char"
            case Str          => "string"
            case Array(ty)    => s"${ty.toString}[]"
            case Pair(fst, snd) => s"pair(${fst.toString}, ${snd.toString})"
            case Func(retTy, argsTy) => 
                val argsStr = argsTy.map(_.toString).mkString(", ")
                s"${retTy.toString}($argsStr)"
        }
    }

    /** Converts a syntactic type to a semantic type. */
    def convertType(ty: IdType | PairElemType): KType = ty match {
        case IntType => KType.Int
        case BoolType => KType.Bool
        case CharType => KType.Char
        case StringType => KType.Str
        case at @ ArrayType(idType, idx) => idx match {
            case 1 => KType.Array(convertType(idType))
            case _ => KType.Array(convertType(ArrayType(idType, idx - 1)(at.pos)))
        }
        case PairType(fst, snd) => KType.Pair(convertType(fst), convertType(snd))
        case Pair => KType.Pair(?, ?)
    }
}
