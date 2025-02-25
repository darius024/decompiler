// package wacc.backend.generator

// import scala.collection.mutable

// import wacc.backend.ir.*
// import errors.*
// import flags.*
// import immediate.*
// import instructions.*
// import memory.*
// import registers.*
// import widgets.*

// def allocate(codeGen: CodeGenerator): CodeGenerator = {
//     val instructions = codeGen.ir

//     instructions.map {
//         case Push(reg: Register) =>
//         case Pop(reg: Register) =>

//         case Cmp(dest: Register, src: RegImm) =>
//         case SetComp(dest: Register, compFlag: CompFlag) =>

//         case Add(dest: Register, src: RegImm) =>
//         case Sub(dest: Register, src: RegImm) => 

//         case Mul(dest: Register, src1: RegImm, src2: RegImm) =>
//         case Mod(src: RegImm) =>
//         case Div(src: RegImm) =>

//         case And(dest: Register, src: RegImm) =>
//         case Or (dest: Register, src: RegImm) =>
//         case Neg(dest: Register, src: RegImm) =>
//         case Not(dest: Register, src: RegImm) =>
//         case Test(dest: Register, src1: RegImm) =>

//         // move
//         case Mov(dest: RegMem, src: RegImmMem) =>
//         case Lea(dest: Register, addr: MemAccess) =>

//         case instr => instr
//     }

//     codeGen
// }
