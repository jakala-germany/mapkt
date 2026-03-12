package com.jakala.mapkt.processor.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.jakala.mapkt.processor.MAPKT_FROM_TO_CLASSES_ANNOTATION_ARG_NAME
import com.jakala.mapkt.processor.TARGET_PACKAGE_NAME
import com.jakala.mapkt.processor.extractArgumentClasses
import com.jakala.mapkt.processor.extractMapKtAnnotation
import com.jakala.mapkt.processor.generateFileName
import com.jakala.mapkt.processor.generator.MappingEnumClassGenerator
import com.jakala.mapkt.processor.visitor.MapKtClassVisitor.Companion.GENERATED_CLASS_SUFFIX
import com.squareup.kotlinpoet.FileSpec

class MapKtEnumClassVisitor(
    private val codeGenerator: CodeGenerator,
    private val resolver: Resolver,
    private val logger: KSPLogger,
) : KSVisitorVoid() {
    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: Unit,
    ) {
        val kcmAnnotation: KSAnnotation? =
            extractMapKtAnnotation(
                targetClass = classDeclaration,
                logger = logger,
            )

        if (kcmAnnotation == null) {
            logger.warn("Missing annotation for class $classDeclaration.")
            return
        }

        // Nothing to do if none of the mapping arguments is filled
        val mappingTargets =
            resolver.extractArgumentClasses(
                kcmAnnotation,
                MAPKT_FROM_TO_CLASSES_ANNOTATION_ARG_NAME,
            )

        // Nothing to do if none of the mapping arguments is filled
        if (mappingTargets.isEmpty()) {
            logger.warn("Missing mapping functions for annotated class $classDeclaration.")
            return
        }

        val fileSpec =
            FileSpec.builder(
                packageName = TARGET_PACKAGE_NAME,
                fileName = classDeclaration.simpleName.asString(),
            )

        mappingTargets.forEach {
            fileSpec.addFunction(
                MappingEnumClassGenerator.generateMappingFunction(
                    targetClass = classDeclaration,
                    sourceClass = it,
                ),
            )
            fileSpec.addFunction(
                MappingEnumClassGenerator.generateMappingFunction(
                    targetClass = it,
                    sourceClass = classDeclaration,
                ),
            )
        }

        codeGenerator
            .createNewFile(
                dependencies = Dependencies(true, classDeclaration.containingFile!!),
                packageName = TARGET_PACKAGE_NAME,
                fileName = generateFileName(classDeclaration) + GENERATED_CLASS_SUFFIX,
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