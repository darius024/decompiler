package wacc.backend.generator

import wacc.semantics.scoping.semanticTypes.*

import wacc.backend.ir.*
import registers.*
import widgets.*

/** Utility functions for parsing typed AST nodes. */
object utils {
    /** Gets the base type of an array access. */
    def getArrayType(semTy: SemType): SemType = semTy match {
        case KType.Array(elemType, _) => elemType
        case ty                       => ty
    }

    /** Computes the size of a type in bytes. */
    def getTypeSize(semType: SemType): RegSize = semType match {
        case KType.Int  => RegSize.DOUBLE_WORD
        case KType.Bool => RegSize.BYTE
        case KType.Char => RegSize.BYTE
        case _          => RegSize.QUAD_WORD
    }

    def getArrayElementLoadWidget(elemSize: RegSize): Widget = elemSize match {
        case RegSize.BYTE        => ArrayLoad1
        case RegSize.WORD        => ArrayLoad2
        case RegSize.DOUBLE_WORD => ArrayLoad4
        case RegSize.QUAD_WORD   => ArrayLoad8  
    }

    def getArrayElementStoreWidget(elemSize: RegSize): Widget = elemSize match {
        case RegSize.BYTE        => ArrayStore1
        case RegSize.WORD        => ArrayStore2
        case RegSize.DOUBLE_WORD => ArrayStore4
        case RegSize.QUAD_WORD   => ArrayStore8 
    }
}
