package com.jakala.mapkt.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal const val MAPKT_FROM_TO_CLASSES_ANNOTATION_ARG_NAME: String = "mapTo"
internal const val MAPKT_ALIASES_ANNOTATION_ARG_NAME: String = "aliases"

internal fun extractMapKtAnnotation(
    targetClass: KSClassDeclaration,
    logger: KSPLogger,
): List<KSAnnotation> {
    val mapKtAnnotations =
        targetClass.annotations
            .filter { targetClassAnnotations ->
                targetClassAnnotations.shortName.asString() == MAP_KT_ANNOTATION_NAME
            }

    mapKtAnnotations.forEach { mapKtAnnotation ->
        val argsProvided =
            mapKtAnnotation.arguments.any { constructorParam ->
                constructorParam.name?.asString() == MAPKT_FROM_TO_CLASSES_ANNOTATION_ARG_NAME
            }
        if (!argsProvided) {
            logger.logAndThrowError(
                errorMessage =
                    "Classes annotated with `@$MAP_KT_ANNOTATION_NAME` must contain " +
                        "at least one class as a parameter like: `$MAP_KT_ANNOTATION_NAME(mapTo = [YourClassToMap::kt])",
                targetClass = targetClass,
            )
        }
    }
    return mapKtAnnotations.toList()
}