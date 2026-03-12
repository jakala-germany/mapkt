package com.jakala.mapkt.annotations

import kotlin.reflect.KClass

/**
 * @param mapTo define classes you want to map to from and to the annotated class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class MapKt(
    val mapTo: Array<KClass<*>> = [],
)