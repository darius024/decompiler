package wacc.backend.generator

import wacc.backend.ir.*
import registers.*

import wacc.semantics.scoping.semanticTypes.*
import wacc.semantics.typing.*


/** Utility functions for working with typed AST nodes */
object utils {
  
  /** Gets the base type and arity of an array element
   * @param arrayElem The array element to analyze
   * @return A tuple of (element type, arity)
   */
  def getBaseArrayInfo(arrayElem: TyExpr.ArrayElem): (SemType, Int) = arrayElem.ty match {
      case KType.Array(elemType, arity) => (elemType, arity)
      case ty => (ty, 1)
    }

  /** Gets the identifier name from an lvalue
   * @param lvalue The lvalue to get the name from
   * @return The name of the underlying identifier
   */
  def getLvalName(lvalue: TyExpr.LVal): String = lvalue match {
    case TyExpr.Id(value, _) => value
    case TyExpr.ArrayElem(lval, _, _) => getLvalName(lval)
    case TyExpr.PairFst(lval, _) => getLvalName(lval)
    case TyExpr.PairSnd(lval, _) => getLvalName(lval)
    case _ => ""
  }

  /** Gets the name of the array from an array element
   * @param arrayElem The array element to get the name from
   * @return The name of the array
   */
  def getArrayName(arrayElem: TyExpr.ArrayElem): String = getLvalName(arrayElem.lval)

  /** Gets the index of an array element
   * @param arrayElem The array element to get the index from
   * @return The index of the array element
   */
  def getArrayIndex(arrayElem: TyExpr.ArrayElem): List[TyExpr] = arrayElem.idx


  /** Gets the nested lvalue from a complex lvalue
   * @param lvalue The complex lvalue
   * @return The nested lvalue
   */
  def getNestedLval(lvalue: TyExpr.LVal): TyExpr.LVal = lvalue match {
    case TyExpr.ArrayElem(lval, _, _) => lval
    case TyExpr.PairFst(lval, _) => lval
    case TyExpr.PairSnd(lval, _) => lval
    case _ => lvalue
  }

  /** Gets the nested pair element from a complex pair element
   * @param arrayElem The complex pair element
   * @return The nested pair element
   */
  def getNestedPairLval(arrayElem: TyExpr.TyPairElem): TyExpr.LVal = arrayElem match {
    case TyExpr.PairFst(lval, _) => lval
    case TyExpr.PairSnd(lval, _) => lval
  }

  /** Gets the size of a type in bytes
   * @param semType The type to get the size of
   * @return The size of the type in bytes
   */
  def getTypeSize(semType: SemType): Int = semType match {
        case KType.Int => DOUBLE_WORD
        case KType.Bool => BYTE
        case KType.Char => BYTE
        case KType.Str => QUAD_WORD
        case KType.Pair(_,_) => QUAD_WORD
        case KType.Array(_,_) => QUAD_WORD
        case _ => QUAD_WORD
    }

}