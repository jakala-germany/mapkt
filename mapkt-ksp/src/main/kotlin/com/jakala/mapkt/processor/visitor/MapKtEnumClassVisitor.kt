package com.jakala.mapkt.processor.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.jakala.mapkt.processor.TARGET_PACKAGE_NAME
import com.jakala.mapkt.processor.extensions.getMapKtAnnotations
import com.jakala.mapkt.processor.extensions.getMapToDeclaration
import com.jakala.mapkt.processor.generator.implementations.MappingEnumClassGenerator
import com.jakala.mapkt.processor.util.generateFileName
import com.jakala.mapkt.processor.visitor.MapKtClassVisitor.Companion.GENERATED_CLASS_SUFFIX
import com.squareup.kotlinpoet.FileSpec

internal class MapKtEnumClassVisitor(
    private val codeGenerator: CodeGenerator,
    private val resolver: Resolver,
    private val logger: KSPLogger,
) : KSVisitorVoid() {
    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: Unit,
    ) {
        val mapKtAnnotations = classDeclaration.getMapKtAnnotations()

        if (mapKtAnnotations.isEmpty()) {
            logger.warn("Missing annotation for class $classDeclaration.")
            return
        }
        mapKtAnnotations.forEach { mapKtAnnotation ->
            generateMapFunction(
                classDeclaration = classDeclaration,
                mapKtAnnotation = mapKtAnnotation,
            )
        }
    }

    private fun generateMapFunction(
        classDeclaration: KSClassDeclaration,
        mapKtAnnotation: KSAnnotation,
    ) {
        val mappingTarget = mapKtAnnotation.getMapToDeclaration(resolver)

        // Nothing to do if none of the mapping arguments is filled
        if (mappingTarget == null) {
            logger.warn("Missing mapping functions for annotated class $classDeclaration.")
            return
        }

        val fileSpec =
            FileSpec.builder(
                packageName = TARGET_PACKAGE_NAME,
                fileName = classDeclaration.simpleName.asString(),
            )

        fileSpec.addFunction(
            MappingEnumClassGenerator.generateMappingFunction(
                targetClass = classDeclaration,
                sourceClass = mappingTarget,
            ),
        )
        fileSpec.addFunction(
            MappingEnumClassGenerator.generateMappingFunction(
                targetClass = mappingTarget,
                sourceClass = classDeclaration,
            ),
        )

        codeGenerator
            .createNewFile(
                dependencies = Dependencies(true, classDeclaration.containingFile!!),
                packageName = TARGET_PACKAGE_NAME,
                fileName =
                    generateFileName(classDeclaration)
                        .plus(mappingTarget.simpleName.asString())
                        .plus(GENERATED_CLASS_SUFFIX),
            ).use { stream ->
                stream.write(fileSpec.build().toString().toByteArray())
            }
    }

    override fun visitAnnotation(
        annotation: KSAnnotation,
        data: Unit,
    ) {
        annotation.annotationType
            .resolve()
            .declaration
            .accept(this, data)
    }
}