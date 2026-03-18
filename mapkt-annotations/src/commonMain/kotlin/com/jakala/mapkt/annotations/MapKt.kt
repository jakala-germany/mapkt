package com.jakala.mapkt.annotations

import kotlin.reflect.KClass

/**
 * @param mapTo define classes you want to map to from and to the annotated class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class MapKt(
    val mapTo: KClass<*>,
    val aliases: Array<PropertyMapping> = [],
    val ignores: Array<String> = [],
)