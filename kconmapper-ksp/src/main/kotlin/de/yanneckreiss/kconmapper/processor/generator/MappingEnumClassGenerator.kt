package de.yanneckreiss.kconmapper.processor.generator

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

object MappingEnumClassGenerator {

    fun generateMappingFunction(
        sourceClass: KSClassDeclaration,
        targetClass: KSClassDeclaration,
    ): FunSpec {
        val sourceClassName = sourceClass.simpleName.asString()
        val targetClassName = targetClass.simpleName.asString()
        val sourcePackageName = sourceClass.packageName.asString()
        val targetPackageName = targetClass.packageName.asString()

        val enumCases = sourceClass.declarations.filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.ENUM_ENTRY }
            .map { it.simpleName.asString() }

        val funName = "to$targetClassName"
        val receiverType = ClassName(sourcePackageName, sourceClassName)
        val returnType = ClassName(targetPackageName, targetClassName)

        val funBuilder = FunSpec.builder(funName)
            .receiver(receiverType)
            .returns(returnType)
            .addModifiers(KModifier.PUBLIC)

        funBuilder.beginControlFlow("return when(this)")
        enumCases.forEach { enumCase ->
            funBuilder.addStatement("$sourceClassName.$enumCase -> $targetClassName.$enumCase")
        }
        funBuilder.endControlFlow()

        return funBuilder.build()
    }
}