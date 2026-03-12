package com.jakala.mapkt.processor.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.jakala.mapkt.processor.MAPKT_FROM_TO_CLASSES_ANNOTATION_ARG_NAME
import com.jakala.mapkt.processor.TARGET_PACKAGE_NAME
import com.jakala.mapkt.processor.extractArgumentClasses
import com.jakala.mapkt.processor.extractMapKtAnnotation
import com.jakala.mapkt.processor.generateFileName
import com.jakala.mapkt.processor.generator.MappingFunctionGenerator
import com.jakala.mapkt.processor.generator.MappingSealedClassGenerator
import com.squareup.kotlinpoet.FileSpec
import java.io.OutputStream

class MapKtClassVisitor(
    private val codeGenerator: CodeGenerator,
    private val resolver: Resolver,
    private val logger: KSPLogger,
) : KSVisitorVoid() {
    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: Unit,
    ) {
        val annotatedClass: KSClassDeclaration = classDeclaration
        val kcmAnnotation: KSAnnotation? =
            extractMapKtAnnotation(
                targetClass = annotatedClass,
                logger = logger,
            )

        if (kcmAnnotation == null) {
            logger.warn("Missing annotation for class $annotatedClass.")
            return
        }

        val mapToClasses =
            resolver.extractArgumentClasses(
                kcmAnnotation,
                MAPKT_FROM_TO_CLASSES_ANNOTATION_ARG_NAME,
            )
        // Nothing to do if none of the mapping arguments is filled
        if (mapToClasses.isEmpty()) {
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
                Modifier.SEALED in annotatedClass.modifiers -> MappingSealedClassGenerator to "Sealed"
                else -> MappingFunctionGenerator(resolver, logger) to "Class"
            }

        mapToClasses.forEach {
            fileSpec.addFunction(
                mappingFunctionGenerator.generateMappingFunction(
                    targetClass = annotatedClass,
                    sourceClass = it,
                ),
            )
            fileSpec.addFunction(
                mappingFunctionGenerator.generateMappingFunction(
                    targetClass = it,
                    sourceClass = annotatedClass,
                ),
            )
        }

        codeGenerator
            .createNewFile(
                dependencies = Dependencies(true, classDeclaration.containingFile!!),
                packageName = TARGET_PACKAGE_NAME,
                fileName = generateFileName(classDeclaration) + GENERATED_CLASS_SUFFIX + suffix,
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
