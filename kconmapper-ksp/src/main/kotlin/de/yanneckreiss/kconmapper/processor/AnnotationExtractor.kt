package de.yanneckreiss.kconmapper.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration

const val KCONMAPPER_FROM_CLASSES_ANNOTATION_ARG_NAME = "fromClasses"
const val KCONMAPPER_TARGET_CLASSES_ANNOTATION_ARG_NAME = "toClasses"
const val KCONMAP_FROM_TO_CLASSES_ANNOATATION_ARG_NAME = "mapTo"

internal fun extractKConMapAnnotation(targetClass: KSClassDeclaration, logger: KSPLogger): KSAnnotation? {
    // Checks if the class is annotated with the [KConMap] annotation
    val kcmAnnotation = targetClass.annotations
        .firstOrNull { targetClassAnnotations ->
            targetClassAnnotations.shortName.asString() == KCONMAP_ANNOTATION_NAME
        }

    // Checks if the class that pretends to be the [KConMap] annotation has the `mapTo` argument
    val argsProvided = kcmAnnotation?.arguments?.all { constructorParam ->
        constructorParam.name?.asString() == KCONMAP_FROM_TO_CLASSES_ANNOATATION_ARG_NAME
    } ?: true

    if (!argsProvided) {
        logger.logAndThrowError(
            errorMessage = "Classes annotated with `@$KCONMAP_ANNOTATION_NAME` must contain " +
                    "at least one class as a parameter like: `$KCONMAP_ANNOTATION_NAME(mapTo = [YourClassToMap::kt])",
            targetClass = targetClass
        )
    }

    return kcmAnnotation
}

internal fun extractKConMapperAnnotation(targetClass: KSClassDeclaration, logger: KSPLogger): KSAnnotation? {

    // Checks if the class is annotated with the [KConMapper] annotation
    val kcmAnnotation = targetClass.annotations
        .firstOrNull { targetClassAnnotations ->
            targetClassAnnotations.shortName.asString() == KCONMAPPER_ANNOTATION_NAME
        }

    // Checks if the class that pretends to be the [KConMapper] annotation has the `classes` argument
    val argsAreProvided = kcmAnnotation?.arguments?.any { constructorParam ->
        constructorParam.name?.asString() == KCONMAPPER_TARGET_CLASSES_ANNOTATION_ARG_NAME ||
                constructorParam.name?.asString() == KCONMAPPER_FROM_CLASSES_ANNOTATION_ARG_NAME
    } ?: true

    if (!argsAreProvided) {
        logger.logAndThrowError(
            errorMessage = "Classes annotated with `@$KCONMAPPER_ANNOTATION_NAME` must contain " +
                    "at least one class as a parameter like: `$KCONMAPPER_TARGET_CLASSES_ANNOTATION_ARG_NAME(toClasses = [YourClassToMap::kt])",
            targetClass = targetClass
        )
    }

    return kcmAnnotation
}