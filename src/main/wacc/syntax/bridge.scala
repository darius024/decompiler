package wacc.syntax

import parsley.Parsley
import parsley.ap.*
import parsley.generic.*
import parsley.position.pos

object bridges {
    type Position = (Int, Int)

    trait ParserSingletonBridgePos[+A] extends ErrorBridge {
        protected def con(pos: (Int, Int)): A
        infix def from(op: Parsley[Any]): Parsley[A] = error(pos.map(this.con(_)) <* op)
        final def <#(op: Parsley[Any]): Parsley[A] = this from op
    }

    trait ParserBridgePos1[-A, +B] extends ParserSingletonBridgePos[A => B] {
        def apply(x: A)(pos: (Int, Int)): B
        def apply(x: Parsley[A]): Parsley[B] = error(ap1(pos.map(con), x))

        override final def con(pos: (Int, Int)): A => B = this.apply(_)(pos)
    }

    trait ParserBridgePos2[-A, -B, +C] extends ParserSingletonBridgePos[(A, B) => C] {
        def apply(x: A, y: B)(pos: (Int, Int)): C
        def apply(x: Parsley[A], y: =>Parsley[B]): Parsley[C] = error(ap2(pos.map(con), x, y))

        override final def con(pos: (Int, Int)): (A, B) => C = this.apply(_, _)(pos)
    }

    trait ParserBridgePos3[-A, -B, -C, +D] extends ParserSingletonBridgePos[(A, B, C) => D] {
        def apply(x: A, y: B, z: C)(pos: (Int, Int)): D
        def apply(x: Parsley[A], y: =>Parsley[B], z: =>Parsley[C]): Parsley[D] = error(ap3(pos.map(con), x, y, z))

        override final def con(pos: (Int, Int)): (A, B, C) => D = this.apply(_, _, _)(pos)
    }
}
