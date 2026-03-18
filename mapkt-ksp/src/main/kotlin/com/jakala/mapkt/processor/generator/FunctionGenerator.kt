package com.jakala.mapkt.processor.generator

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jakala.mapkt.processor.Alias
import com.squareup.kotlinpoet.FunSpec

internal interface FunctionGenerator {
    fun generateMappingFunction(
        sourceClass: KSClassDeclaration,
        targetClass: KSClassDeclaration,
        aliases: List<Alias> = emptyList(),
        ignores: List<String> = emptyList(),
    ): FunSpec
}