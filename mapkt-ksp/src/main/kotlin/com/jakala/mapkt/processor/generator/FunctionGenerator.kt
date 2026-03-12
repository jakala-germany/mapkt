package com.jakala.mapkt.processor.generator

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FunSpec

interface FunctionGenerator {
    fun generateMappingFunction(
        sourceClass: KSClassDeclaration,
        targetClass: KSClassDeclaration,
    ): FunSpec
}
