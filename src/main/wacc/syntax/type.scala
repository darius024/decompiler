package wacc.syntax

import parsley.generic.*

object types {
    sealed trait IdType

    sealed trait BaseType extends IdType with PairElemType
    case object IntType extends BaseType with ParserBridge0[BaseType]
    case object BoolType extends BaseType with ParserBridge0[BaseType]
    case object CharType extends BaseType with ParserBridge0[BaseType]
    case object StringType extends BaseType with ParserBridge0[BaseType]

    case class ArrayType(idType: IdType, arity: Int) extends IdType with PairElemType
    case class PairType(fst: PairElemType, snd: PairElemType) extends IdType

    sealed trait PairElemType
    case object Pair extends PairElemType with ParserBridge0[PairElemType]


    object ArrayType extends ParserBridge2[IdType, Int, ArrayType]
    object PairType extends ParserBridge2[PairElemType, PairElemType, PairType]
}
