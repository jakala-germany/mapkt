package com.jakala.mapkt.processor.generator.implementations

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jakala.mapkt.processor.extensions.getMapKtAnnotations
import com.jakala.mapkt.processor.extensions.getMapToSimpleName
import com.jakala.mapkt.processor.generator.FunctionGenerator
import com.jakala.mapkt.processor.util.Alias
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec

internal object MappingSealedClassGenerator : FunctionGenerator {
    override fun generateMappingFunction(
        sourceClass: KSClassDeclaration,
        targetClass: KSClassDeclaration,
        aliases: List<Alias>,
        ignores: List<String>,
    ): FunSpec {
        val subclasses = sourceClass.getSealedSubclasses()
        val targetSubclasses = targetClass.getSealedSubclasses()

        val funBuilder =
            FunSpec
                .builder(name = "to${targetClass.simpleName.asString()}")
                .receiver(
                    ClassName.bestGuess(
                        requireNotNull(sourceClass.qualifiedName) {
                            "Receiver Source class must have a qualified name ${sourceClass.simpleName.asString()}"
                        }.asString(),
                    ),
                ).returns(
                    ClassName.bestGuess(
                        requireNotNull(targetClass.qualifiedName) {
                            "Return Target class must have a qualified name ${targetClass.simpleName.asString()}"
                        }.asString(),
                    ),
                ).addModifiers()

        fun KSClassDeclaration.mapToNames() =
            this.getMapKtAnnotations().mapNotNull { mapKtAnnotation ->
                mapKtAnnotation.getMapToSimpleName()
            }

        funBuilder.beginControlFlow("return when(this)")
        subclasses
            .map { subclass ->
                val targetSubClass =
                    targetSubclasses.firstOrNull { target ->
                        target.simpleName.asString() == subclass.simpleName.asString() ||
                            subclass.simpleName.asString() in target.mapToNames() ||
                            target.simpleName.asString() in subclass.mapToNames()
                    }
                subclass to
                    requireNotNull(targetSubClass) {
                        "No target class found for ${subclass.simpleName.asString()}"
                    }
            }.forEach { (sourceSubClass, targetSubClass) ->
                funBuilder.addStatement(
                    "is %T -> to${targetSubClass.simpleName.asString()}()",
                    ClassName.bestGuess(
                        requireNotNull(sourceSubClass.qualifiedName) {
                            "Statement Source class must have a qualified name ${sourceSubClass.simpleName.asString()}"
                        }.asString(),
                    ),
                )
            }
        funBuilder.endControlFlow()

        return funBuilder.build()
    }
}