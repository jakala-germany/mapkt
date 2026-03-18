package com.jakala.mapkt.processor.util

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jakala.mapkt.processor.MAPKT_ANNOTATION_NAME
import com.jakala.mapkt.processor.extensions.logAndThrowError

internal const val MAPKT_FROM_TO_CLASS_ANNOTATION_ARG_NAME: String = "mapTo"
internal const val MAPKT_ALIASES_ANNOTATION_ARG_NAME: String = "aliases"
internal const val MAPKT_IGNORES_ANNOTATION_ARG_NAME: String = "ignores"

internal fun extractMapKtAnnotation(
    targetClass: KSClassDeclaration,
    logger: KSPLogger,
): List<KSAnnotation> {
    val mapKtAnnotations =
        targetClass.annotations
            .filter { targetClassAnnotations ->
                targetClassAnnotations.shortName.asString() == MAPKT_ANNOTATION_NAME
            }

    mapKtAnnotations.forEach { mapKtAnnotation ->
        val argsProvided =
            mapKtAnnotation.arguments.any { constructorParam ->
                constructorParam.name?.asString() == MAPKT_FROM_TO_CLASS_ANNOTATION_ARG_NAME
            }
        if (!argsProvided) {
            logger.logAndThrowError(
                errorMessage =
                    "Classes annotated with `@${MAPKT_ANNOTATION_NAME}` must contain " +
                        "at least one class as a parameter like: `${MAPKT_ANNOTATION_NAME}(mapTo = YourClassToMap::kt)",
                targetClass = targetClass,
            )
        }
    }
    return mapKtAnnotations.toList()
}