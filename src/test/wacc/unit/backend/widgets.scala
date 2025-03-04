package wacc.unit.backend

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import wacc.backend.generator.*
import errors.*
import wacc.backend.ir.instructions.*
import widgets.*

import wacc.semantics.scoping.semanticTypes.*
import wacc.semantics.typing.*
import TyExpr.*
import TyStmt.*

/** Tests the widget functionality and coverage. */
class WidgetTests extends AnyFlatSpec {

    "Widget system" should "have widget for integer read operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        codeGen.enterScope(Label("main"))
        val readIntStmt = Read(TyExpr.Id("x", KType.Int))
        generate(readIntStmt)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ReadInt)
    }

    it should "have widget for character read operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        codeGen.enterScope(Label("main"))
        val readCharStmt = Read(TyExpr.Id("y", KType.Char))
        generate(readCharStmt)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ReadChar)
    }

    it should "have widget for integer print operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        codeGen.enterScope(Label("main"))
        val printIntStmt = Print(IntLit(42))
        generate(printIntStmt)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(PrintInt)
    }

    it should "have widget for boolean print operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val printBoolStmt = Print(BoolLit(true))
        generate(printBoolStmt)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(PrintBool)
    }

    it should "have widget for character print operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val printCharStmt = Print(CharLit('A'))
        generate(printCharStmt)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(PrintChar)
    }

    it should "have widget for string print operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val printStrStmt = Print(StrLit("Hello"))
        generate(printStrStmt)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(PrintString)
    }

    it should "have widget for println operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val printlnStmt = Println(StrLit("Hello"))
        generate(printlnStmt)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(PrintString)
        usedWidgets should contain(PrintLn)
    }

    it should "have widget for exit operations" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val exitStmt = Exit(IntLit(0))
        generate(exitStmt)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ExitProg)
    }

    it should "have widget for division by zero error" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val divExpr = BinaryArithmetic(IntLit(10), IntLit(0), OpArithmetic.Div)
        generateDivMod(divExpr)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ErrDivZero)
    }

    it should "have widget for overflow error" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val addExpr = BinaryArithmetic(IntLit(Int.MaxValue), IntLit(1), OpArithmetic.Add)
        generate(addExpr)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ErrOverflow)
    }

    it should "have widget for array bounds error" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        codeGen.enterScope(Label("main"))
        val arrayType = KType.Array(KType.Int, 1)
        val arrayElem = TyExpr.ArrayElem(TyExpr.Id("arr", arrayType), List(IntLit(-1)), arrayType)
        generate(arrayElem)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ErrOutOfBounds)
    }

    it should "have widget for null pointer error" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        val freePairStmt = Free(TyExpr.PairLit)
        generate(freePairStmt)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ErrNull)
    }

    it should "have all used widgets in the widgets set" in {
        given codeGen: CodeGenerator = emptyCodeGenerator
        codeGen.enterScope(Label("main"))
        
        val readIntStmt = Read(TyExpr.Id("x", KType.Int))
        val readCharStmt = Read(TyExpr.Id("y", KType.Char))
        val printIntStmt = Print(IntLit(42))
        val printBoolStmt = Print(BoolLit(true))
        val printCharStmt = Print(CharLit('A'))
        val printStrStmt = Print(StrLit("Hello"))
        val printlnStmt = Println(StrLit("Hello"))
        val exitStmt = Exit(IntLit(0))
        val divExpr = BinaryArithmetic(IntLit(10), IntLit(0), OpArithmetic.Div)
        val addExpr = BinaryArithmetic(IntLit(Int.MaxValue), IntLit(1), OpArithmetic.Add)
        val arrayType = KType.Array(KType.Int, 1)
        val arrayElem = TyExpr.ArrayElem(TyExpr.Id("arr", arrayType), List(IntLit(-1)), arrayType)
        val freePairStmt = Free(TyExpr.PairLit)
        
        generate(readIntStmt)
        generate(readCharStmt)
        generate(printIntStmt)
        generate(printBoolStmt)
        generate(printCharStmt)
        generate(printStrStmt)
        generate(printlnStmt)
        generate(exitStmt)
        generateDivMod(divExpr)
        generate(addExpr)
        generate(arrayElem)
        generate(freePairStmt)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets.foreach { widget =>
            widgetSet should contain(widget)
        }
    }
} 
