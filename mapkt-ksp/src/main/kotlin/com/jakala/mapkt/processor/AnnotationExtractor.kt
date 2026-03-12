package com.jakala.mapkt.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration

const val MAPKT_FROM_TO_CLASSES_ANNOTATION_ARG_NAME = "mapTo"

internal fun extractMapKtAnnotation(
    targetClass: KSClassDeclaration,
    logger: KSPLogger,
): KSAnnotation? {
    val mapKtAnnotation =
        targetClass.annotations
            .firstOrNull { targetClassAnnotations ->
                targetClassAnnotations.shortName.asString() == MAP_KT_ANNOTATION_NAME
            }

    val argsProvided =
        mapKtAnnotation?.arguments?.all { constructorParam ->
            constructorParam.name?.asString() == MAPKT_FROM_TO_CLASSES_ANNOTATION_ARG_NAME
        } ?: true

    if (!argsProvided) {
        logger.logAndThrowError(
            errorMessage =
                "Classes annotated with `@$MAP_KT_ANNOTATION_NAME` must contain " +
                    "at least one class as a parameter like: `$MAP_KT_ANNOTATION_NAME(mapTo = [YourClassToMap::kt])",
            targetClass = targetClass,
        )
    }

    return mapKtAnnotation
}