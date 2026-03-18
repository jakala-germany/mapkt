package com.jakala.mapkt.processor.util

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal fun generateFileName(ksClass: KSClassDeclaration?): String =
    when {
        ksClass == null -> ""
        ksClass.parent != null ->
            generateFileName(ksClass.parent as? KSClassDeclaration).plus(
                ksClass.simpleName.asString(),
            )

        else ->
            ksClass.simpleName.asString()
    }