package wacc.extension.decompiler

import wacc.semantics.scoping.semanticTypes.*

/** High-level intermediate representation that supports all programs. */
object representation {
    type StatementList = List[Statement]
    type TypeId = (Type, Id)

    // programs and functions

    case class Program(funcs: List[Func], main: StatementList)
    case class Func(typeId: TypeId, params: Array[TypeId], stmts: StatementList)

    // expressions

    sealed trait Expression
    case class BinaryOp(lhs: Expression, rhs: Expression, op: BinaryOperation) extends Expression
    case class UnaryOp(expr: Expression, op: UnaryOperation) extends Expression

    enum BinaryOperation {
        case Or
        case And
        case Equal
        case NotEqual
        case Greater
        case GreaterEq
        case Less
        case LessEq
        case Add
        case Sub
        case Mul
        case Div
        case Mod
    }

    enum UnaryOperation {
        case Not
        case Neg
        case Len
        case Ord
        case Chr
    }

    sealed trait Atom extends Expression
    case class IntLit(value: Int) extends Atom
    case class BoolLit(value: Boolean) extends Atom
    case class CharLit(value: Char) extends Atom
    case class StrLit(value: String) extends Atom
    case object PairLit extends Atom
    case class Id(value: String) extends Atom
    case class ArrayElem(id: Id, indices: List[Expression]) extends Atom
    case class ParensExpression(expr: Expression) extends Atom

    sealed trait RValue extends Expression
    case class ArrayLit(exprs: List[Expression]) extends RValue
    case class NewPair(fst: Expression, snd: Expression) extends RValue
    case class Call(func: Id, args: List[Expression]) extends RValue

    sealed trait PairElem extends Expression
    case class Fst(value: Expression) extends PairElem
    case class Snd(value: Expression) extends PairElem

    // types

    sealed trait Type
    case object IntType extends Type
    case object BoolType extends Type
    case object CharType extends Type
    case object StrType extends Type
    case class ArrayType(ty: Type) extends Type
    case class PairType(fst: Type, snd: Type) extends Type
    case object Unset extends Type

    // statements

    sealed trait Statement
    case class Declaration(typeId: TypeId, rvalue: Expression) extends Statement
    case class Assignment(lvalue: Expression, rvalue: Expression) extends Statement
    case class Read(lvalue: Expression) extends Statement
    case class Print(expr: Expression) extends Statement
    case class Println(expr: Expression) extends Statement
    case class Free(expr: Expression) extends Statement
    case class Return(expr: Expression) extends Statement
    case class Exit(expr: Expression) extends Statement

    case class If(cond: Expression, thenStatements: StatementList, elseStatements: StatementList) extends Statement
    case class While(cond: Expression, doStatements: StatementList) extends Statement
    case class Block(block: StatementList) extends Statement
}
