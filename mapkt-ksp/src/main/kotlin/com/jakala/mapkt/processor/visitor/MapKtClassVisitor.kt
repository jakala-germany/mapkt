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
import com.jakala.mapkt.processor.extensions.extractArgumentClass
import com.jakala.mapkt.processor.extensions.getAliases
import com.jakala.mapkt.processor.extensions.getIgnores
import com.jakala.mapkt.processor.generator.implementations.MappingFunctionGenerator
import com.jakala.mapkt.processor.generator.implementations.MappingSealedClassGenerator
import com.jakala.mapkt.processor.util.MAPKT_FROM_TO_CLASS_ANNOTATION_ARG_NAME
import com.jakala.mapkt.processor.util.extractMapKtAnnotation
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
        val annotatedClass: KSClassDeclaration = classDeclaration
        val kcmAnnotation: List<KSAnnotation> =
            extractMapKtAnnotation(
                targetClass = annotatedClass,
                logger = logger,
            )

        if (kcmAnnotation.isEmpty()) {
            logger.warn("Missing annotation for class $annotatedClass.")
            return
        }

        kcmAnnotation.forEach { annotation ->
            generateMapFunction(
                classDeclaration = classDeclaration,
                annotatedClass = annotatedClass,
                kcmAnnotation = annotation,
            )
        }
    }

    fun generateMapFunction(
        classDeclaration: KSClassDeclaration,
        annotatedClass: KSClassDeclaration,
        kcmAnnotation: KSAnnotation,
    ) {
        val mapToClass =
            resolver.extractArgumentClass(
                kcmAnnotation,
                MAPKT_FROM_TO_CLASS_ANNOTATION_ARG_NAME,
            )
        // Nothing to do if none of the mapping arguments is filled
        if (mapToClass == null) {
            logger.warn("Missing mapping functions for annotated class $annotatedClass.")
            return
        }

        val fileSpec =
            FileSpec.builder(
                packageName = TARGET_PACKAGE_NAME,
                fileName = classDeclaration.simpleName.asString(),
            )

        val (mappingFunctionGenerator, suffix) =
            when {
                Modifier.SEALED in annotatedClass.modifiers ->
                    MappingSealedClassGenerator to
                        "Sealed"

                else -> MappingFunctionGenerator(resolver, logger) to "Class"
            }

        val aliases = kcmAnnotation.getAliases()
        val ignores = kcmAnnotation.getIgnores()
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
                dependencies = Dependencies(true, classDeclaration.containingFile!!),
                packageName = TARGET_PACKAGE_NAME,
                fileName =
                    generateFileName(classDeclaration)
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