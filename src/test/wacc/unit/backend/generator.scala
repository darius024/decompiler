package wacc.unit.backend

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import wacc.backend.generator.*
import wacc.backend.ir.*
import immediate.*
import instructions.*

import wacc.semantics.typing.*
import TyExpr.*
import TyStmt.*

def emptyCodeGenerator: CodeGenerator =
    new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)

/** Tests the code generator functionality. */
class CodeGeneratorTests extends AnyFlatSpec {

    "CodeGenerator" should "generate code for integer literals" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val intExpr = IntLit(42)
        generate(intExpr)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 1
        instructions.head match {
            case Mov(_, src: Imm) => src.value shouldBe 42
            case _ => fail("Expected Mov instruction with immediate value")
        }
    }

    it should "generate code for boolean literals" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val boolExpr = BoolLit(true)
        generate(boolExpr)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 1
        instructions.head match {
            case Mov(_, src: Imm) => src.value shouldBe 1
            case _ => fail("Expected Mov instruction with immediate value")
        }
    }

    it should "generate code for character literals" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val charExpr = CharLit('A')
        generate(charExpr)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 1
        instructions.head match {
            case Mov(_, src: Imm) => src.value shouldBe 'A'.toInt
            case _ => fail("Expected Mov instruction with immediate value")
        }
    }

    it should "generate code for string literals" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val strExpr = StrLit("Hello")
        generate(strExpr)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 1
        instructions.head.isInstanceOf[Lea] shouldBe true
    }

    it should "generate code for binary arithmetic operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val addExpr = BinaryArithmetic(IntLit(5), IntLit(3), OpArithmetic.Add)
        generate(addExpr)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 8 // two moves for literals, one add, one overflow check
    }

    it should "generate code for binary comparison operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val compExpr = BinaryComp(IntLit(5), IntLit(3), OpComp.GreaterThan)
        generate(compExpr)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 8 // two moves for literals, one compare, one set
    }

    it should "generate code for unary operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val negExpr = TyExpr.Neg(IntLit(5))
        generate(negExpr)
        
        val instructions = codeGen.ir
        instructions.length shouldBe 4 // move literal, move 0, subtract, overflow check
    }

    it should "generate code for if statements" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val ifStmt = If(BoolLit(true), List(Exit(IntLit(0))), List(Exit(IntLit(1))))
        generate(ifStmt)
        
        val instructions = codeGen.ir
        instructions.exists(_.isInstanceOf[Jump]) shouldBe true
    }

    it should "generate code for while loops" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val whileStmt = While(BoolLit(true), List(Exit(IntLit(0))))
        generate(whileStmt)
        
        val instructions = codeGen.ir
        instructions.exists(_.isInstanceOf[Jump]) shouldBe true
        instructions.count(_.isInstanceOf[Label]) shouldBe 2
    }

    it should "handle short-circuit evaluation for boolean operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val andExpr = BinaryBool(BoolLit(true), BoolLit(false), OpBool.And)
        shortCircuit(andExpr)
        
        val instructions = codeGen.ir
        instructions.exists(_.isInstanceOf[JumpComp]) shouldBe true
    }

    it should "generate code for division operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val divExpr = BinaryArithmetic(IntLit(10), IntLit(2), OpArithmetic.Div)
        generate(divExpr)
        
        val instructions = codeGen.ir
        instructions.collectFirst { case _: Div => succeed } getOrElse fail("Expected Div instruction")
        instructions.collectFirst { case _: Cmp => succeed } getOrElse fail("Expected Cmp instruction for zero check")
    }
}
