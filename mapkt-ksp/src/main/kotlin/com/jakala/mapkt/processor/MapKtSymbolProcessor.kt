package com.jakala.mapkt.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.processor.visitor.MapKtClassVisitor
import com.jakala.mapkt.processor.visitor.MapKtEnumClassVisitor

const val TARGET_PACKAGE_NAME = "com.jakala.mapkt"
const val MAP_KT_ANNOTATION_NAME = "MapKt"

class MapKtSymbolProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(annotationName = MapKt::class.qualifiedName!!)
            .filter { ksAnnotated -> ksAnnotated is KSClassDeclaration && ksAnnotated.validate() }
            .forEach { ksAnnotated: KSAnnotated ->
                val classDeclaration: KSClassDeclaration = (ksAnnotated as KSClassDeclaration)
                when (classDeclaration.classKind) {
                    ClassKind.INTERFACE,
                    ClassKind.ENUM_ENTRY,
                    ClassKind.OBJECT,
                    ClassKind.ANNOTATION_CLASS,
                    -> {
                        logger.logAndThrowError(
                            errorMessage =
                                "Cannot generate function for " +
                                    "class `${classDeclaration.getName()}`, " +
                                    "class type `${classDeclaration.classKind}` is not supported.",
                            targetClass = classDeclaration,
                        )
                    }

                    ClassKind.ENUM_CLASS -> {
                        val visitor =
                            MapKtEnumClassVisitor(
                                codeGenerator = codeGenerator,
                                resolver = resolver,
                                logger = logger,
                            )
                        ksAnnotated.accept(
                            visitor = visitor,
                            data = Unit,
                        )
                    }

                    ClassKind.CLASS -> {
                        val visitor =
                            MapKtClassVisitor(
                                codeGenerator = codeGenerator,
                                resolver = resolver,
                                logger = logger,
                            )
                        // Class type is supported
                        ksAnnotated.accept(
                            visitor = visitor,
                            data = Unit,
                        )
                    }
                }
            }

        return emptyList()
    }
}