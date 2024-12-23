package com.github.yanneckreiss.kconmapper.annotations

import kotlin.reflect.KClass

/**
 * @param mapTo define classes you want to map to from and to the annotated class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class KConMap(
    val mapTo: Array<KClass<*>> = [],
)