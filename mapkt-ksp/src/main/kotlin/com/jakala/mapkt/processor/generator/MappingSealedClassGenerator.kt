package com.jakala.mapkt.processor.generator

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jakala.mapkt.processor.Alias
import com.jakala.mapkt.processor.MAP_KT_ANNOTATION_NAME
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

        fun KSClassDeclaration.annotationValue() =
            this.annotations
                .firstOrNull { it.shortName.asString() == MAP_KT_ANNOTATION_NAME }
                ?.arguments
                ?.get(0)
                ?.let {
                    if (it.name?.asString() == ANNOTATION_MAP_TO_PARAM_NAME) {
                        it.value
                            .toString()
                            .removePrefix("[")
                            .removeSuffix("]")
                    } else {
                        null
                    }
                }

        funBuilder.beginControlFlow("return when(this)")
        subclasses
            .map { subclass ->
                val targetSubClass =
                    targetSubclasses.firstOrNull { target ->
                        target.simpleName.asString() == subclass.simpleName.asString() ||
                            target
                                .annotationValue()
                                ?.equals(subclass.simpleName.asString()) == true ||
                            subclass
                                .annotationValue()
                                ?.equals(target.simpleName.asString()) == true
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

    private const val ANNOTATION_MAP_TO_PARAM_NAME = "mapTo"
}