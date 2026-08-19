package com.jakala.mapkt.processor.extensions

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.jakala.mapkt.annotations.MapKt

/**
 * All [MapKt] annotations of a declaration, [MapKt] is repeatable.
 *
 * The annotations are returned as [KSAnnotation] instead of [MapKt] because
 * `getAnnotationsByType` cannot be used here, see `KSAnnotationExt.kt`.
 */
internal fun KSAnnotated.getMapKtAnnotations(): List<KSAnnotation> =
    annotations
        .filter { annotation ->
            annotation.annotationType
                .resolve()
                .declaration.qualifiedName
                ?.asString() == MapKt::class.qualifiedName
        }.toList()