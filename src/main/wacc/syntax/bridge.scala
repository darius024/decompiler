package wacc.syntax

import parsley.Parsley
import parsley.ap.*
import parsley.generic.*
import parsley.position.pos

/** Parser Bridges for Position-Aware Parsing
  *
  * These bridges extend Parsley's generic bridges by adding support for position tracking
  * (line and column numbers). They simplify the construction of parsers that produce
  * position-aware results, such as Abstract Syntax Tree (AST) nodes.
  *
  * Position is tracked using the `pos` parser, and the bridges are specialized for constructors
  * with different arities (0, 1, 2, or 3 arguments). Each bridge allows concise parser definitions
  * that incorporate positional metadata into the resulting objects.
  */

object bridges {
    /** Type alias for position tracking. Represents line and column numbers in the input. */
    type Position = (Int, Int)

    /** Parser bridge for constructing single values with position tracking.
      *
      * @tparam A The type of the result constructed by the bridge.
      */
    trait ParserSingletonBridgePos[+A] extends ErrorBridge {
        /** Constructs a value of type `A` using the given position.
          *
          * @param pos The position (line and column) in the input.
          * @return The constructed value of type `A`.
          */
        protected def con(pos: Position): A

        /** Combines position tracking with an operator parser to produce a parser for `A`.
          *
          * @param op A parser for the operator or other contextual input.
          * @return A parser that produces a value of type `A` with positional metadata.
          */
        infix def from(op: Parsley[Any]): Parsley[A] = error(pos.map(this.con(_)) <* op)

        /** Alias for `from`, providing alternative syntax for defining parsers. */
        final def <#(op: Parsley[Any]): Parsley[A] = this from op
    }

    /** Parser bridge for constructing values with one argument and position tracking.
      *
      * @tparam A The type of the input argument.
      * @tparam B The type of the result constructed by the bridge.
      */
    trait ParserBridgePos1[-A, +B] extends ParserSingletonBridgePos[A => B] {
        /** Constructs a value of type `B` using an argument of type `A` and a position.
          *
          * @param x The input argument of type `A`.
          * @param pos The position (line and column) in the input.
          * @return The constructed value of type `B`.
          */
        def apply(x: A)(pos: Position): B

        /** Combines a parser for `A` with position tracking to produce a parser for `B`.
          *
          * @param x A parser for the input argument of type `A`.
          * @return A parser that produces a value of type `B` with positional metadata.
          */
        def apply(x: Parsley[A]): Parsley[B] = error(ap1(pos.map(con), x))

        /** Constructs a function `A => B` using the given position.
          *
          * @param pos The position (line and column) in the input.
          * @return A function that maps `A` to `B` with the given position.
          */
        override final def con(pos: Position): A => B = this.apply(_)(pos)
    }

    /** Parser bridge for constructing values with two arguments and position tracking.
      *
      * @tparam A The type of the first input argument.
      * @tparam B The type of the second input argument.
      * @tparam C The type of the result constructed by the bridge.
      */
    trait ParserBridgePos2[-A, -B, +C] extends ParserSingletonBridgePos[(A, B) => C] {
        /** Constructs a value of type `C` using two arguments and a position.
          *
          * @param x The first input argument of type `A`.
          * @param y The second input argument of type `B`.
          * @param pos The position (line and column) in the input.
          * @return The constructed value of type `C`.
          */
        def apply(x: A, y: B)(pos: Position): C

        /** Combines parsers for `A` and `B` with position tracking to produce a parser for `C`.
          *
          * @param x A parser for the first input argument of type `A`.
          * @param y A parser for the second input argument of type `B`.
          * @return A parser that produces a value of type `C` with positional metadata.
          */
        def apply(x: Parsley[A], y: =>Parsley[B]): Parsley[C] = error(ap2(pos.map(con), x, y))

        /** Constructs a function `(A, B) => C` using the given position.
          *
          * @param pos The position (line and column) in the input.
          * @return A function that maps `(A, B)` to `C` with the given position.
          */
        override final def con(pos: Position): (A, B) => C = this.apply(_, _)(pos)
    }

    /** Parser bridge for constructing values with three arguments and position tracking.
      *
      * @tparam A The type of the first input argument.
      * @tparam B The type of the second input argument.
      * @tparam C The type of the third input argument.
      * @tparam D The type of the result constructed by the bridge.
      */
    trait ParserBridgePos3[-A, -B, -C, +D] extends ParserSingletonBridgePos[(A, B, C) => D] {
        /** Constructs a value of type `D` using three arguments and a position.
          *
          * @param x The first input argument of type `A`.
          * @param y The second input argument of type `B`.
          * @param z The third input argument of type `C`.
          * @param pos The position (line and column) in the input.
          * @return The constructed value of type `D`.
          */
        def apply(x: A, y: B, z: C)(pos: Position): D

        /** Combines parsers for `A`, `B`, and `C` with position tracking to produce a parser for `D`.
          *
          * @param x A parser for the first input argument of type `A`.
          * @param y A parser for the second input argument of type `B`.
          * @param z A parser for the third input argument of type `C`.
          * @return A parser that produces a value of type `D` with positional metadata.
          */
        def apply(x: Parsley[A], y: =>Parsley[B], z: =>Parsley[C]): Parsley[D] = error(ap3(pos.map(con), x, y, z))

        /** Constructs a function `(A, B, C) => D` using the given position.
          *
          * @param pos The position (line and column) in the input.
          * @return A function that maps `(A, B, C)` to `D` with the given position.
          */
        override final def con(pos: Position): (A, B, C) => D = this.apply(_, _, _)(pos)
    }
}