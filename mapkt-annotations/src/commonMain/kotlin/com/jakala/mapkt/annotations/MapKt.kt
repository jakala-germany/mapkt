package com.jakala.mapkt.annotations

import kotlin.reflect.KClass

/**
 * Generates mapping functions between the annotated class and [mapTo],
 * one for each direction.
 *
 * Repeat the annotation to map onto more than one class, every annotation
 * carries its own [aliases] and [ignores].
 *
 * @param mapTo the class the annotated class is mapped from and to.
 * @param aliases property pairs that do not share the same name on both classes.
 * @param ignores properties that are not mapped, they have to be passed
 * as a parameter of the generated function instead.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class MapKt(
    val mapTo: KClass<*>,
    val aliases: Array<PropertyMapping> = [],
    val ignores: Array<String> = [],
)