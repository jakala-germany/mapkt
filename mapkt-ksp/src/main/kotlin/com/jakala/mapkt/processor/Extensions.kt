package com.jakala.mapkt.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument

fun KSDeclaration.getName() = simpleName.asString()

@Throws(IllegalArgumentException::class)
fun KSPLogger.logAndThrowError(
    errorMessage: String,
    targetClass: KSClassDeclaration,
) {
    error(errorMessage, targetClass as KSNode)
    throw IllegalArgumentException(errorMessage)
}

@Suppress("UNCHECKED_CAST")
fun Resolver.extractArgumentClasses(
    kcmAnnotation: KSAnnotation,
    paramName: String,
): List<KSClassDeclaration> =
    kcmAnnotation
        .arguments
        .find { annotationArgument: KSValueArgument ->
            annotationArgument.name?.asString() ==
                paramName
        }?.let { ksValueArgument -> ksValueArgument.value as List<KSType> }
        ?.mapNotNull { argumentClassType ->
            this.getClassDeclarationByName(argumentClassType.declaration.qualifiedName!!)
        } // TODO: Check if !! is okay here
        ?: emptyList()