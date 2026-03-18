package com.jakala.mapkt.processor.generator

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jakala.mapkt.processor.Alias
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec

internal object MappingEnumClassGenerator : FunctionGenerator {
    override fun generateMappingFunction(
        sourceClass: KSClassDeclaration,
        targetClass: KSClassDeclaration,
        aliases: List<Alias>,
        ignores: List<String>,
    ): FunSpec {
        val enumCases =
            sourceClass.declarations
                .filterIsInstance<KSClassDeclaration>()
                .filter { it.classKind == ClassKind.ENUM_ENTRY }
                .map { it.simpleName.asString() }

        val funBuilder =
            FunSpec
                .builder(name = "to${targetClass.simpleName.asString()}")
                .receiver(ClassName.bestGuess(sourceClass.qualifiedName!!.asString()))
                .returns(ClassName.bestGuess(targetClass.qualifiedName!!.asString()))
                .addModifiers()

        funBuilder.beginControlFlow("return when(this)")
        enumCases.forEach { enumCase ->
            funBuilder.addStatement(
                "${sourceClass.qualifiedName!!.asString()}.$enumCase ->" +
                    " ${targetClass.qualifiedName!!.asString()}.$enumCase",
            )
        }
        funBuilder.endControlFlow()

        return funBuilder.build()
    }
}