package wacc.syntax

import parsley.generic.*
import bridges.*

/** Type Nodes of the Abstract Syntax Tree.
  *
  * Implements the data types that the WACC language supports.
  */
object types {
    sealed trait IdType

    // ========== Base Types ==========
    sealed trait BaseType extends IdType with PairElemType
    case object IntType extends BaseType with ParserBridge0[BaseType]
    case object BoolType extends BaseType with ParserBridge0[BaseType]
    case object CharType extends BaseType with ParserBridge0[BaseType]
    case object StringType extends BaseType with ParserBridge0[BaseType]

    // ========== Array Types ==========
    case class ArrayType(idType: IdType, arity: Int)(val pos: Position) extends IdType with PairElemType

    // ========== Pair Types ==========
    case class PairType(fst: PairElemType, snd: PairElemType)(val pos: Position) extends IdType

    // ========== Pair Element Types ==========
    sealed trait PairElemType
    case object Pair extends PairElemType with ParserBridge0[PairElemType]


    // ========== Companion Objects ==========
    object ArrayType extends ParserBridgePos2[IdType, Int, ArrayType]
    object PairType extends ParserBridgePos2[PairElemType, PairElemType, PairType]
}
