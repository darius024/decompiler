package wacc.unit.backend

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.*

import wacc.backend.generator.{CodeGenerator, Labeller, WidgetManager, generate, generateDivMod}
import wacc.backend.generator.widgets.{widgets => widgetSet, ReadInt, ReadChar, PrintInt, PrintBool, PrintChar, PrintString, PrintLn, ExitProg}
import wacc.backend.generator.errors.{ErrDivZero, ErrOverflow, ErrOutOfBounds, ErrNull}
import wacc.backend.ir.registers.*
import wacc.semantics.typing.*
import wacc.semantics.scoping.semanticTypes.{KType}
import TyStmt.*
import TyExpr.{IntLit, BoolLit, CharLit, StrLit, BinaryArithmetic, OpArithmetic}

/** Tests the widget functionality and coverage. */
class WidgetTests extends AnyFlatSpec with Matchers {

    "Widget system" should "have widget for integer read operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val readIntStmt = Read(TyExpr.Id("x", KType.Int))
        generate(readIntStmt)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ReadInt)
    }

    it should "have widget for character read operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val readCharStmt = Read(TyExpr.Id("y", KType.Char))
        generate(readCharStmt)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ReadChar)
    }

    it should "have widget for integer print operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val printIntStmt = Print(IntLit(42))
        generate(printIntStmt)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(PrintInt)
    }

    it should "have widget for boolean print operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val printBoolStmt = Print(BoolLit(true))
        generate(printBoolStmt)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(PrintBool)
    }

    it should "have widget for character print operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val printCharStmt = Print(CharLit('A'))
        generate(printCharStmt)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(PrintChar)
    }

    it should "have widget for string print operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val printStrStmt = Print(StrLit("Hello"))
        generate(printStrStmt)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(PrintString)
    }

    it should "have widget for println operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val printlnStmt = Println(StrLit("Hello"))
        generate(printlnStmt)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(PrintString)
        usedWidgets should contain(PrintLn)
    }

    it should "have widget for exit operations" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val exitStmt = Exit(IntLit(0))
        generate(exitStmt)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ExitProg)
    }

    it should "have widget for division by zero error" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val divExpr = BinaryArithmetic(IntLit(10), IntLit(0), OpArithmetic.Div)
        generateDivMod(divExpr)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ErrDivZero)
    }

    it should "have widget for overflow error" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val addExpr = BinaryArithmetic(IntLit(Int.MaxValue), IntLit(1), OpArithmetic.Add)
        generate(addExpr)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ErrOverflow)
    }

    it should "have widget for array bounds error" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val arrayType = KType.Array(KType.Int, 1)
        val arrayElem = TyExpr.ArrayElem(TyExpr.Id("arr", arrayType), List(IntLit(-1)), arrayType)
        generate(arrayElem)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ErrOutOfBounds)
    }

    it should "have widget for null pointer error" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        val freePairStmt = Free(TyExpr.PairLit)
        generate(freePairStmt)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets should contain(ErrNull)
    }

    it should "have all used widgets in the widgets set" in {
        val codeGen = new CodeGenerator(List.newBuilder, Set.newBuilder, new Labeller, new Temporary, new WidgetManager)
        
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
        
        generate(readIntStmt)(using codeGen)
        generate(readCharStmt)(using codeGen)
        generate(printIntStmt)(using codeGen)
        generate(printBoolStmt)(using codeGen)
        generate(printCharStmt)(using codeGen)
        generate(printStrStmt)(using codeGen)
        generate(printlnStmt)(using codeGen)
        generate(exitStmt)(using codeGen)
        generateDivMod(divExpr)(using codeGen)
        generate(addExpr)(using codeGen)
        generate(arrayElem)(using codeGen)
        generate(freePairStmt)(using codeGen)
        
        val usedWidgets = codeGen.dependencies
        usedWidgets.foreach { widget =>
            widgetSet should contain(widget)
        }
    }
} 