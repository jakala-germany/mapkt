package com.jakala.mapkt.processor.extensions

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueArgument
import com.jakala.mapkt.processor.util.Alias
import com.jakala.mapkt.processor.util.MAPKT_ALIASES_ANNOTATION_ARG_NAME

@Suppress("UNCHECKED_CAST")
internal fun KSAnnotation.getAliases(): List<Alias> =
    this.arguments
        .find { argument ->
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