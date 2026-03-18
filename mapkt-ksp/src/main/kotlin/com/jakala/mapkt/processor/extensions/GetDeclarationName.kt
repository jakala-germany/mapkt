package com.jakala.mapkt.processor.extensions

import com.google.devtools.ksp.symbol.KSDeclaration

internal fun KSDeclaration.getName(): String = simpleName.asString()