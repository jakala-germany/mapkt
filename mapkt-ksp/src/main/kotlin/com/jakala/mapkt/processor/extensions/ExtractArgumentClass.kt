package com.jakala.mapkt.processor.extensions

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument

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