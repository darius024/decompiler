package wacc.syntax

import parsley.Parsley
import parsley.ap.*
import parsley.generic.*
import parsley.position.pos

/** 
  * Extend Parsley's generic bridges with position tracking specifically
  * for constructors with different arities of 0, 1, 2, or 3.
  */
object bridges {
    /**
     * Alias for (line, col)
    */
    type Position = (Int, Int)

    /**
      * Parser bridge for () => @tparam A
      */
    trait ParserSingletonBridgePos[+A] extends ErrorBridge {
        // constructor
        protected def con(pos: Position): A

        // wrap existing parser
        infix def from(op: Parsley[Any]): Parsley[A] = error(pos.map(this.con(_)) <* op)

        // convenient notation for `from`
        final def <#(op: Parsley[Any]): Parsley[A] = this from op
    }

    /**
      * Parser bridge for (@tparam A) => @tparam B
      */
    trait ParserBridgePos1[-A, +B] extends ParserSingletonBridgePos[A => B] {
        // constructor with input
        def apply(x: A)(pos: Position): B

        // wrap parser
        def apply(x: Parsley[A]): Parsley[B] = error(ap1(pos.map(con), x))

        // constructor
        override final def con(pos: Position): A => B = this.apply(_)(pos)
    }

    /**
      * Parser bridge for (@tparam A, @tparam B) => @tparam C
      */
    trait ParserBridgePos2[-A, -B, +C] extends ParserSingletonBridgePos[(A, B) => C] {
        // constructor with inputs
        def apply(x: A, y: B)(pos: Position): C

        // wrap parser
        def apply(x: Parsley[A], y: =>Parsley[B]): Parsley[C] = error(ap2(pos.map(con), x, y))

        // constructor
        override final def con(pos: Position): (A, B) => C = this.apply(_, _)(pos)
    }

    /**
      * Parser bridge for (@tparam A, @tparam B, @tparam C) => @tparam D
      */
    trait ParserBridgePos3[-A, -B, -C, +D] extends ParserSingletonBridgePos[(A, B, C) => D] {
        // constructor with inputs
        def apply(x: A, y: B, z: C)(pos: Position): D

        // wrap parser
        def apply(x: Parsley[A], y: =>Parsley[B], z: =>Parsley[C]): Parsley[D] = error(ap3(pos.map(con), x, y, z))

        // constructor
        override final def con(pos: Position): (A, B, C) => D = this.apply(_, _, _)(pos)
    }
}