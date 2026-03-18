package com.jakala.mapkt.processor.extensions

import com.google.devtools.ksp.symbol.KSAnnotation
import com.jakala.mapkt.processor.util.MAPKT_IGNORES_ANNOTATION_ARG_NAME

@Suppress("UNCHECKED_CAST")
internal fun KSAnnotation.getIgnores(): List<String> =
    this.arguments
        .find { argument ->
            argument.name?.asString() == MAPKT_IGNORES_ANNOTATION_ARG_NAME
        }?.value
        ?.let {
            (it as? List<String>).orEmpty()
        }.orEmpty()