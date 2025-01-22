package wacc.syntax

import parsley.generic.*
import bridges.*

/** Type System Abstract Syntax Tree (AST) Nodes for WACC
 *
 * Implements the following BNF grammar rules:
 * 
 * <type> ::= <base-type>
 *          | <array-type>
 *          | <pair-type>
 *
 * <base-type> ::= 'int' 
 *               | 'bool' 
 *               | 'char' 
 *               | 'string'
 *
 * <array-type> ::= <type> '[]'
 *
 * <pair-type> ::= 'pair' '(' <pair-elem-type> ',' <pair-elem-type> ')'
 *
 * <pair-elem-type> ::= <base-type>
 *                    | <array-type>
 *                    | 'pair'
 *
 * The type system consists of:
 * 1. Base types (int, bool, char, string)
 * 2. Array types with arbitrary dimensions
 * 3. Pair types for structured data
 *
 * Example types:
 * ```
 * int
 * bool[]
 * pair(int, string)
 * int[][]
 * pair(pair(int, bool), char[])
 * ```
 */

object types {
    sealed trait IdType

    // ========== Base Types ==========
    // <base-type> ::= 'int' | 'bool' | 'char' | 'string'
    sealed trait BaseType extends IdType with PairElemType
    case object IntType extends BaseType with ParserBridge0[BaseType]
    case object BoolType extends BaseType with ParserBridge0[BaseType]
    case object CharType extends BaseType with ParserBridge0[BaseType]
    case object StringType extends BaseType with ParserBridge0[BaseType]

    // ========== Array Types ==========
    // <array-type> ::= <type> '[]'
    case class ArrayType(idType: IdType, arity: Int)(val pos: Position) extends IdType with PairElemType

    // ========== Pair Types ==========
    // <pair-type> ::= 'pair' '(' <pair-elem-type> ',' <pair-elem-type> ')'
    case class PairType(fst: PairElemType, snd: PairElemType)(val pos: Position) extends IdType

    // ========== Pair Element Types ==========
    // <pair-elem-type> ::= <base-type> | <array-type> | 'pair'
    sealed trait PairElemType
    case object Pair extends PairElemType with ParserBridge0[PairElemType]

    // ========== Companion Objects ==========
    object ArrayType extends ParserBridgePos2[IdType, Int, ArrayType]
    object PairType extends ParserBridgePos2[PairElemType, PairElemType, PairType]
}
