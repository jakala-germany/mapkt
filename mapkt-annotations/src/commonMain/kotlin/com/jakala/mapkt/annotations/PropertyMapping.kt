package com.jakala.mapkt.annotations

/**
 * class for property mappings
 *
 * @param src the source property name (annotated class)
 * @param tar the target property name (mapTo target class)
 */
annotation class PropertyMapping(
    val src: String,
    val tar: String,
)