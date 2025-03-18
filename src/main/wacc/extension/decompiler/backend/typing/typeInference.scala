package wacc.extension.decompiler

import scala.collection.mutable

import representation.*
import BinaryOperation.*
import UnaryOperation.*

/** Stores information about the types of all  variables and functions. */
class Typer(val vars: mutable.Map[Id, Type],
            val funcs: mutable.Map[Id, Type]) {
    var returnTy: Type = Unset

    // insert a variable to the map
    def addVar(id: Id, ty: Type): Unit = {
        vars += id -> ty
    }

    // insert a function to the map
    def addFunc(id: Id, ty: Type): Unit = {
        funcs += id -> ty
    }
}

/** Performs local type inference on a function. */
def infer(function: Func): (Func, Typer) = {
    given typer: Typer =
        Typer(mutable.Map.empty[Id, Type], mutable.Map.empty[Id, Type])
    
    val Func((ty, id), params, stmts) = function

    val newTy = ty
    val newParams = params
    val newStmts = stmts.map(infer(id, _))
    
    (Func((newTy, id), newParams, newStmts), typer)
}

/** Performs local type inference on a statement. */
def infer(funcName: Id, stmt: Statement)
         (using typer: Typer): Statement = stmt match {
    case Declaration(typeId, rvalue) =>
        stmt
    case Assignment(lvalue, rvalue) =>
        val (expr, ty) = infer(rvalue, Unset)
        val (lv, lvTy) = infer(lvalue, ty)
        Assignment(lv, expr)
    case Read(lvalue) =>
        val (exprTy, _) = infer(lvalue, Unset)
        Read(exprTy)
    case Print(expr) =>
        val (exprTy, _) = infer(expr, Unset)
        Print(exprTy)
    case Println(expr) =>
        val (exprTy, _) = infer(expr, Unset)
        Println(exprTy)
    case Free(expr) =>
        val (exprTy, _) = infer(expr, ArrayType(Unset))
        Free(exprTy)
    case Return(expr) =>
        val (exprTy, ty) = infer(expr, Unset)
        typer.funcs(funcName) = ty
        Return(exprTy)
    case Exit(expr) =>
        val (exprTy, ty) = infer(expr, IntType)
        Exit(exprTy)

    case If(cond, thenStatements, elseStatements) =>
        val (condTy, _) = infer(cond, BoolType)
        val thenStmts = thenStatements.map(infer(funcName, _))
        val elseStmts = elseStatements.map(infer(funcName, _))
        If(condTy, thenStmts, elseStmts)
    case While(cond, doStatements) =>
        val (condTy, _) = infer(cond, BoolType)
        val doStmts = doStatements.map(infer(funcName, _))
        While(condTy, doStmts)
    case Block(block) =>
        val stmts = block.map(infer(funcName, _))
        Block(stmts)
}

/** Performs local type inference on an expression, given the constraint. */
def infer(expr: Expression, constraint: Type)
         (using typer: Typer): (Expression, Type) = expr match {
    case id @ Id(value) => 
        // if the variable has been encountered before and has a type, use that information
        if (typer.vars.contains(id) && typer.vars(id) != Unset) {
            (id, typer.vars(id))
        } else {
            // store more specific information about the type of the variable
            typer.vars(id) = constraint
            (id, constraint)
        }
    case IntLit(value) => constraint match {
        case BoolType => (BoolLit(value != 0), BoolType)
        case CharType => (CharLit(value.toChar), CharType)
        case intLit   => (expr, IntType)
    }
    case CharLit(value) => (expr, CharType)
    case StrLit(value) => (expr, StrType)

    case ArrayElem(id, exprs) =>
        val indices = exprs.map(infer(_, IntType)).map(_._1)
        (ArrayElem(id, indices), constraint)    
    case ArrayLit(exprs: List[Expression]) =>
        var elemTy: Type = Unset
        if (exprs.nonEmpty) {
            val (_, ty) = infer(exprs(0), Unset)
            elemTy = ty
        }
        (ArrayLit(exprs.map(infer(_, elemTy)._1)), ArrayType(elemTy))
    case NewPair(fst: Expression, snd: Expression) =>
        val (fstExpr, fstTy) = infer(fst, Unset)
        val (sndExpr, sndTy) = infer(snd, Unset)
        (NewPair(fstExpr, sndExpr), PairType(fstTy, sndTy))
    case Call(func: Id, args: List[Expression]) =>
        (Call(func, args.map(infer(_, Unset)._1)), Unset)

    case Fst(value) =>
        val (expr, ty) = infer(value, Unset)
        (Fst(expr), extractPairType(ty))
    case Snd(value) =>
        val (expr, ty) = infer(value, Unset)
        (Snd(expr), extractPairType(ty, false))

    case BinaryOp(lhs, rhs, op) => op match {
        case Or | And =>
            (BinaryOp(infer(lhs, BoolType)._1, infer(rhs, BoolType)._1, op), BoolType)
        case Add | Sub | Mul | Div | Mod =>
            (BinaryOp(infer(lhs, IntType)._1, infer(rhs, IntType)._1, op), IntType)
        case Less | LessEq | Greater | GreaterEq =>
            (BinaryOp(infer(lhs, IntType)._1, infer(rhs, IntType)._1, op), IntType)
        case _ =>
            var (lhsExpr, lhsTy) = infer(lhs, Unset)
            var (rhsExpr, rhsTy) = infer(rhs, Unset)
            if (lhsTy != Unset) {
                val expr = infer(rhs, lhsTy)
                rhsExpr = expr._1
                rhsTy = expr._2
            } else {
                val expr = infer(lhs, rhsTy)
                lhsExpr = expr._1
                lhsTy = expr._2
            }
            (BinaryOp(lhsExpr, rhsExpr, op), BoolType)
    }
    case UnaryOp(expr, op)      => op match {
        case Not =>
            (UnaryOp(infer(expr, BoolType)._1, op), BoolType)
        case Neg =>
            (UnaryOp(infer(expr, IntType)._1, op), IntType)
        case Len =>
            (UnaryOp(infer(expr, ArrayType(Unset))._1, op), IntType)
        case Ord =>
            (UnaryOp(infer(expr, CharType)._1, op), IntType)
        case Chr =>
            (UnaryOp(infer(expr, IntType)._1, op), CharType)
    }

    case ParensExpression(expr) =>
        val (exprTy, ty) = infer(expr, constraint)
        (ParensExpression(exprTy), ty)
    
    case expr => (expr, Unset)
}

/** Appends type information to a program.
  * 
  * The main goal is to add type information in function signatures
  * and to replace assignments with declarations when a variable is firstly defined.
  */
def appendTypes(program: Program): Program = {
    // clean program here
    val Program(funcs, main) = program

    val newFuncs = funcs.map(infer(_)).map { (func, typer) =>
        val Func((ty, id), params, stmts) = func

        // get the return type of the function
        val newTy = typer.funcs(id)
        // get the parameters type information
        val newParams = params.map { case (ty, param) =>
            (typer.vars(param), param)
        }
        val newStmts = stmts.map(appendTypes(_, typer))

        Func((newTy, id), newParams, newStmts)
    }

    val (mainStmts, typer) = infer(Func((Unset, Id("main")), Array.empty, main))
    val newMain = mainStmts.stmts.map(appendTypes(_, typer))
    
    Program(newFuncs, newMain)
}

/** Appends type information to a statement. */
def appendTypes(stmt: Statement, typer: Typer): Statement = stmt match {
    case Assignment(id: Id, rvalue) => if (typer.vars.contains(id)) {
        val ty = typer.vars(id)
        val decl = Declaration((ty, id), rvalue)

        // share knowledge of the variable type
        rvalue match {
            case id @ Id(name) if typer.vars.contains(id) && typer.vars(id) == Unset => typer.vars(id) = ty
            case _                                        =>
        }
        typer.vars.remove(id)
        decl
    } else {
        stmt
    }

    case If(cond, thenStatements, elseStatements) =>
        val thenStmts = thenStatements.map(appendTypes(_, typer))
        val elseStmts = elseStatements.map(appendTypes(_, typer))
        If(cond, thenStmts, elseStmts)
    case While(cond, doStatements) =>
        val doStmts = doStatements.map(appendTypes(_, typer))
        While(cond, doStmts)
    case Block(block) =>
        val stmts = block.map(appendTypes(_, typer))
        Block(stmts)
    
    case stmt => stmt
}

def extractPairType(ty: Type, first: Boolean = true): Type = ty match {
    case PairType(fst, snd) => if (first) fst else snd
    case ty             => ty
}
