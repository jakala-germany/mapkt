package com.jakala.mapkt.processor.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.jakala.mapkt.processor.util.MAPKT_FROM_TO_CLASS_ANNOTATION_ARG_NAME
import com.jakala.mapkt.processor.TARGET_PACKAGE_NAME
import com.jakala.mapkt.processor.extensions.extractArgumentClass
import com.jakala.mapkt.processor.util.extractMapKtAnnotation
import com.jakala.mapkt.processor.util.generateFileName
import com.jakala.mapkt.processor.generator.implementations.MappingEnumClassGenerator
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
        val kcmAnnotation: List<KSAnnotation> =
            extractMapKtAnnotation(
                targetClass = classDeclaration,
                logger = logger,
            )

        if (kcmAnnotation.isEmpty()) {
            logger.warn("Missing annotation for class $classDeclaration.")
            return
        }
        kcmAnnotation.forEach {
            generateMapFunction(
                classDeclaration = classDeclaration,
                kcmAnnotation = it,
            )
        }
    }

    fun generateMapFunction(
        classDeclaration: KSClassDeclaration,
        kcmAnnotation: KSAnnotation,
    ) {
        // Nothing to do if none of the mapping arguments is filled
        val mappingTarget =
            resolver.extractArgumentClass(
                kcmAnnotation,
                MAPKT_FROM_TO_CLASS_ANNOTATION_ARG_NAME,
            )

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