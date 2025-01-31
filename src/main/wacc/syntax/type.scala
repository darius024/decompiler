package wacc.syntax

import parsley.generic.*
import bridges.*

/** Type system AST nodes. */
object types {
    sealed trait IdType

    /** <base-type> ::= 'int'
      *               | 'bool'
      *               | 'char'
      *               | 'string'
      */
    sealed trait BaseType extends IdType with PairElemType
    case object IntType extends BaseType with ParserBridge0[BaseType]
    case object BoolType extends BaseType with ParserBridge0[BaseType]
    case object CharType extends BaseType with ParserBridge0[BaseType]
    case object StringType extends BaseType with ParserBridge0[BaseType]

    /** <array-type> ::= <type> '[' ']' */
    case class ArrayType(idType: IdType, arity: Int)(val pos: Position) extends IdType with PairElemType

    /** <pair-type> ::= 'pair' '(' <pair-elem-type> ',' <pair-elem-type> ')' */
    case class PairType(fst: PairElemType, snd: PairElemType)(val pos: Position) extends IdType

    /** <pair-elem-type> ::= <base-type>
      *                    | <array-type>
      *                    | 'pair'
      */
    sealed trait PairElemType
    case object Pair extends PairElemType with ParserBridge0[PairElemType]

    // companion objects

    object ArrayType extends ParserBridgePos2[IdType, Int, ArrayType]
    object PairType extends ParserBridgePos2[PairElemType, PairElemType, PairType]

    // optimisation to reduce backtracking for `PairElemType`s
    object BaseArrayType extends ParserBridgePos2[IdType & PairElemType, Int, PairElemType] {
        def apply(ty: IdType & PairElemType, arity: Int)(pos: Position): PairElemType = arity match {
            case 0 => ty
            case _ => ArrayType(ty, arity)(pos)
        }
    }

    // optimisation to reduce backtracking for `IdType`s
    object BaseArraPairType extends ParserBridgePos2[IdType, Int, IdType] {
        def apply(ty: IdType, arity: Int)(pos: Position): IdType = arity match {
            case 0 => ty
            case _ => ArrayType(ty, arity)(pos)
        }
    }
}
