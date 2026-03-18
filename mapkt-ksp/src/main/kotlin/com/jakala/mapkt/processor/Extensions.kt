package com.jakala.mapkt.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument

internal fun KSDeclaration.getName(): String = simpleName.asString()

@Throws(IllegalArgumentException::class)
internal fun KSPLogger.logAndThrowError(
    errorMessage: String,
    targetClass: KSClassDeclaration,
) {
    error(errorMessage, targetClass as KSNode)
    throw IllegalArgumentException(errorMessage)
}

internal fun Resolver.extractArgumentClass(
    kcmAnnotation: KSAnnotation,
    paramName: String,
): KSClassDeclaration? =
    kcmAnnotation
        .arguments
        .find { annotationArgument: KSValueArgument ->
            annotationArgument.name?.asString() == paramName
        }?.let { ksValueArgument -> ksValueArgument.value as KSType }
        ?.let { argumentClassType ->
            this.getClassDeclarationByName(argumentClassType.declaration.qualifiedName!!)
        }

@Suppress("UNCHECKED_CAST")
internal fun KSClassDeclaration.getAliases(): List<Alias> =
    this.annotations
        .firstOrNull { annotation ->
            annotation.shortName.asString() == MAP_KT_ANNOTATION_NAME
        }?.arguments
        ?.find { argument ->
            argument.name?.asString() == MAPKT_ALIASES_ANNOTATION_ARG_NAME
        }?.value
        .let {
            fun List<KSValueArgument>.getArgumentValueByName(name: String): String =
                this.firstOrNull { arg -> arg.name?.asString() == name }?.value as String

            (it as? List<KSAnnotation>).orEmpty().map { mapping ->
                Alias(
                    source = mapping.arguments.getArgumentValueByName("src"),
                    target = mapping.arguments.getArgumentValueByName("tar"),
                )
            }
        }

@Suppress("UNCHECKED_CAST")
internal fun KSClassDeclaration.getIgnores(): List<String> =
    this.annotations
        .firstOrNull { annotation ->
            annotation.shortName.asString() == MAP_KT_ANNOTATION_NAME
        }?.arguments
        ?.find { argument ->
            argument.name?.asString() == MAPKT_IGNORES_ANNOTATION_ARG_NAME
        }?.value
        ?.let {
            (it as? List<String>).orEmpty()
        }.orEmpty()