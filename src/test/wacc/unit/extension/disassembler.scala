package wacc.unit.extension

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import scala.collection.mutable

import wacc.backend.ir.*
import wacc.extension.decompiler.*
import controlGraph.*
import intermediate.*
import immediate.*
import instructions.*
import registers.*

/** Helper function to emulate the role of the disassembler. */
private def emulateDisassembler(instrs: List[Instruction]): List[Instr] = {
    val label = Label("example")
    val blocks: Map[Label, Block] = Map(label -> Block(label, instrs))
    val directives = Set.empty[StrLabel]
    val instructions = mutable.ListBuffer.empty[Instr]
    val stack = mutable.Stack.empty[String]
    given disassembler: Disassembler =
        Disassembler(label, blocks, directives, stack)
    instrs.foreach(disassemble(_, instructions))
    instructions.toList
}

/** Tests the second pass of the decompiler backend. */
class DisassemblerTests extends AnyFlatSpec {
    "Disassembler" should "correctly translate mov instructions" in {
        val instrs = List(Mov(RAX(), RBX()))
        emulateDisassembler(instrs) shouldBe List(Assignment("x0", "x1"))
    }
    
    it should "correctly translate add instructions" in {
        val instrs = List(Mov(RAX(), Imm(0)), Add(RAX(), Imm(2)))
        emulateDisassembler(instrs) shouldBe List(Assignment("x0", 0), Assignment("x0", Arithmetic("x0", 2, ArithmeticOp.Add)))
    }
    
    it should "correctly translate sub instructions" in {
        val instrs = List(Mov(RAX(), Imm(10)), Sub(RAX(), Imm(5)))
        emulateDisassembler(instrs) shouldBe List(Assignment("x0", 10), Assignment("x0", Arithmetic("x0", 5, ArithmeticOp.Sub)))
    }
    
    it should "correctly translate mul instructions" in {
        val instrs = List(Mov(RAX(), Imm(4)), Mul(RAX(), RBX()))
        emulateDisassembler(instrs) shouldBe List(Assignment("x0", 4), Assignment("x0", Arithmetic("x0", "x1", ArithmeticOp.Mul)))
    }
    
    it should "correctly translate div instructions" in {
        val instrs = List(Mov(RAX(), Imm(20)), Div(RBX()))
        emulateDisassembler(instrs) shouldBe List(Assignment("x0", 20), Assignment("x0", Arithmetic("x0", "x1", ArithmeticOp.Div)))
    }
}
