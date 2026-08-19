package com.jakala.mapkt.processor.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.jakala.mapkt.processor.TARGET_PACKAGE_NAME
import com.jakala.mapkt.processor.extensions.getAliases
import com.jakala.mapkt.processor.extensions.getIgnores
import com.jakala.mapkt.processor.extensions.getMapKtAnnotations
import com.jakala.mapkt.processor.extensions.getMapToDeclaration
import com.jakala.mapkt.processor.generator.implementations.MappingFunctionGenerator
import com.jakala.mapkt.processor.generator.implementations.MappingSealedClassGenerator
import com.jakala.mapkt.processor.util.generateFileName
import com.squareup.kotlinpoet.FileSpec
import java.io.OutputStream

internal class MapKtClassVisitor(
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
                annotatedClass = classDeclaration,
                mapKtAnnotation = mapKtAnnotation,
            )
        }
    }

    private fun generateMapFunction(
        annotatedClass: KSClassDeclaration,
        mapKtAnnotation: KSAnnotation,
    ) {
        val mapToClass = mapKtAnnotation.getMapToDeclaration(resolver)
        // Nothing to do if none of the mapping arguments is filled
        if (mapToClass == null) {
            logger.warn("Missing mapping functions for annotated class $annotatedClass.")
            return
        }

        val fileSpec =
            FileSpec.builder(
                packageName = TARGET_PACKAGE_NAME,
                fileName = annotatedClass.simpleName.asString(),
            )

        val (mappingFunctionGenerator, suffix) =
            when {
                Modifier.SEALED in annotatedClass.modifiers ->
                    MappingSealedClassGenerator to
                        "Sealed"

                else -> MappingFunctionGenerator(resolver, logger) to "Class"
            }

        val aliases = mapKtAnnotation.getAliases()
        val ignores = mapKtAnnotation.getIgnores()
        fileSpec.addFunction(
            mappingFunctionGenerator.generateMappingFunction(
                targetClass = annotatedClass,
                sourceClass = mapToClass,
                aliases = aliases,
                ignores = ignores,
            ),
        )
        fileSpec.addFunction(
            mappingFunctionGenerator.generateMappingFunction(
                targetClass = mapToClass,
                sourceClass = annotatedClass,
                aliases = aliases,
                ignores = ignores,
            ),
        )

        codeGenerator
            .createNewFile(
                dependencies = Dependencies(true, annotatedClass.containingFile!!),
                packageName = TARGET_PACKAGE_NAME,
                fileName =
                    generateFileName(annotatedClass)
                        .plus(mapToClass.simpleName.asString())
                        .plus(GENERATED_CLASS_SUFFIX)
                        .plus(suffix),
            ).use { generatedFileOutputStream: OutputStream ->
                generatedFileOutputStream.write(fileSpec.build().toString().toByteArray())
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

    companion object {
        internal const val GENERATED_CLASS_SUFFIX = "MapKt"
    }
}