package wacc.syntax

import parsley.Parsley
import parsley.ap.*
import parsley.generic.*
import parsley.position.pos

/** Extends Parsley's generic bridges with position tracking specifically
  * for constructors with different arities of 0, 1, 2, or 3.
  */
object bridges {
    /** Alias for (line, col) */
    type Position = (Int, Int)
    final val NoPosition = (0, 0)

    /** Parser bridge for () => @tparam A */
    trait ParserSingletonBridgePos[+A] extends ErrorBridge {
        protected def con(pos: Position): A
        infix def from(op: Parsley[Any]): Parsley[A] = error(pos.map(this.con(_)) <* op)
        final def <#(op: Parsley[Any]): Parsley[A] = this from op
    }

    /** Parser bridge for (@tparam A) => @tparam B */
    trait ParserBridgePos1[-A, +B] extends ParserSingletonBridgePos[A => B] {
        def apply(x: A)(pos: Position): B
        def apply(x: Parsley[A]): Parsley[B] = error(ap1(pos.map(con), x))
        override final def con(pos: Position): A => B = this.apply(_)(pos)
    }

    /** Parser bridge for (@tparam A, @tparam B) => @tparam C */
    trait ParserBridgePos2[-A, -B, +C] extends ParserSingletonBridgePos[(A, B) => C] {
        def apply(x: A, y: B)(pos: Position): C
        def apply(x: Parsley[A], y: =>Parsley[B]): Parsley[C] = error(ap2(pos.map(con), x, y))
        override final def con(pos: Position): (A, B) => C = this.apply(_, _)(pos)
    }

    /** Parser bridge for (@tparam A, @tparam B, @tparam C) => @tparam D */
    trait ParserBridgePos3[-A, -B, -C, +D] extends ParserSingletonBridgePos[(A, B, C) => D] {
        def apply(x: A, y: B, z: C)(pos: Position): D
        def apply(x: Parsley[A], y: =>Parsley[B], z: =>Parsley[C]): Parsley[D] = error(ap3(pos.map(con), x, y, z))
        override final def con(pos: Position): (A, B, C) => D = this.apply(_, _, _)(pos)
    }
}
