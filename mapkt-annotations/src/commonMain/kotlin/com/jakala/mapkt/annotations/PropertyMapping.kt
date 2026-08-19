package com.jakala.mapkt.annotations

/**
 * Maps a property of the annotated class onto a differently named
 * property of the `mapTo` target class.
 *
 * The mapping is applied in both directions, it only has to be declared once.
 *
 * @param source the property name on the annotated class
 * @param target the property name on the `mapTo` target class
 */
annotation class PropertyMapping(
    val source: String,
    val target: String,
)