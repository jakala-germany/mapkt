package com.jakala.mapkt.processor.extensions

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode

@Throws(IllegalArgumentException::class)
internal fun KSPLogger.logAndThrowError(
    errorMessage: String,
    targetClass: KSClassDeclaration,
) {
    error(errorMessage, targetClass as KSNode)
    throw IllegalArgumentException(errorMessage)
}