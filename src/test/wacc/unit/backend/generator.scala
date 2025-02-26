package wacc.unit.backend

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import wacc.backend.generator.*
import wacc.backend.ir.*
import wacc.backend.ir.instructions.*
import wacc.backend.ir.registers.*
import wacc.backend.ir.immediate.*
import wacc.semantics.typing.*
import TyStmt.*
import TyExpr.{IntLit, BoolLit, CharLit, StrLit, BinaryArithmetic, BinaryComp, BinaryBool, OpArithmetic, OpComp, OpBool}

/** Tests the code generator functionality. */
class CodeGeneratorTests extends AnyFlatSpec {

    "CodeGenerator" should "correctly generate code for integer literals" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val intExpr = IntLit(42)
        generate(intExpr)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 1
        instructions.head match {
            case Mov(_, src: Imm) => src.value shouldBe 42
            case _ => fail("Expected Mov instruction with immediate value")
        }
    }

    it should "correctly generate code for boolean literals" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val boolExpr = BoolLit(true)
        generate(boolExpr)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 1
        instructions.head match {
            case Mov(_, src: Imm) => src.value shouldBe 1
            case _ => fail("Expected Mov instruction with immediate value")
        }
    }

    it should "correctly generate code for character literals" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val charExpr = CharLit('A')
        generate(charExpr)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 1
        instructions.head match {
            case Mov(_, src: Imm) => src.value shouldBe 'A'.toInt
            case _ => fail("Expected Mov instruction with immediate value")
        }
    }

    it should "correctly generate code for string literals" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val strExpr = StrLit("Hello")
        generate(strExpr)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 1
        instructions.head shouldBe a[Lea]
    }

    it should "correctly generate code for binary arithmetic operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val addExpr = BinaryArithmetic(IntLit(5), IntLit(3), OpArithmetic.Add)
        generate(addExpr)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 4 // Two moves for literals, one add, one overflow check
        instructions(2) shouldBe a[Add]
    }

    it should "correctly generate code for binary comparison operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val compExpr = BinaryComp(IntLit(5), IntLit(3), OpComp.GreaterThan)
        generate(compExpr)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 4 // Two moves for literals, one compare, one set
        instructions(2) shouldBe a[Cmp]
        instructions(3) shouldBe a[SetComp]
    }

    it should "correctly generate code for unary operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val negExpr = TyExpr.Neg(IntLit(5))
        generate(negExpr)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 4 // Move literal, move 0, subtract, overflow check
        instructions(2) shouldBe a[Sub]
    }

    it should "correctly generate code for if statements" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val ifStmt = If(BoolLit(true), List(Exit(IntLit(0))), List(Exit(IntLit(1))))
        generate(ifStmt)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.exists(_.isInstanceOf[Jump]) shouldBe true
    }

    it should "correctly generate code for while loops" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val whileStmt = While(BoolLit(true), List(Exit(IntLit(0))))
        generate(whileStmt)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.exists(_.isInstanceOf[Jump]) shouldBe true
        instructions.count(_.isInstanceOf[Label]) shouldBe 2 // While body and condition labels
    }

    it should "correctly handle short-circuit evaluation for boolean operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val andExpr = BinaryBool(BoolLit(true), BoolLit(false), OpBool.And)
        shortCircuit(andExpr)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.exists(_.isInstanceOf[JumpComp]) shouldBe true
    }

    it should "correctly generate code for division operations with zero checks" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val divExpr = BinaryArithmetic(IntLit(10), IntLit(2), OpArithmetic.Div)
        generateDivMod(divExpr)(using codeGen)
        
        val instructions = codeGen.ir
        instructions.exists(_.isInstanceOf[Div]) shouldBe true
        instructions.exists(_.isInstanceOf[Cmp]) shouldBe true // Zero check
    }
} 