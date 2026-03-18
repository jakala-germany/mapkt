package com.jakala.mapkt.processor.generator.argument

internal data class MatchingArgument(
    val targetClassPropertyName: String,
    val sourceClassPropertyName: String,
    // Only defined if the target class parameter is generic
    val targetClassPropertyGenericTypeName: String? = null,
)