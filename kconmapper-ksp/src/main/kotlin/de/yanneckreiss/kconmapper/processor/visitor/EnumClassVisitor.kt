package de.yanneckreiss.kconmapper.processor.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.FileSpec
import de.yanneckreiss.kconmapper.processor.KCMConstants.GENERATED_FILE_PATH
import de.yanneckreiss.kconmapper.processor.KCONMAPPER_ANNOTATION_NAME
import de.yanneckreiss.kconmapper.processor.generator.MappingEnumClassGenerator
import de.yanneckreiss.kconmapper.processor.visitor.KCMVisitor.Companion.GENERATED_CLASS_SUFFIX
import de.yanneckreiss.kconmapper.processor.visitor.KCMVisitor.Companion.KCONMAPPER_FROM_CLASSES_ANNOTATION_ARG_NAME
import de.yanneckreiss.kconmapper.processor.visitor.KCMVisitor.Companion.KCONMAPPER_TARGET_CLASSES_ANNOTATION_ARG_NAME
import de.yanneckreiss.kconmapper.processor.visitor.KCMVisitor.Companion.extractArgumentClasses
import de.yanneckreiss.kconmapper.processor.visitor.KCMVisitor.Companion.extractKCMAnnotation

class EnumClassVisitor(
    private val codeGenerator: CodeGenerator,
    private val resolver: Resolver,
    private val logger: KSPLogger,
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val kcmAnnotation: KSAnnotation = extractKCMAnnotation(logger, classDeclaration)
        val mapFromClasses: List<KSClassDeclaration> = extractArgumentClasses(
            resolver = resolver,
            kcmAnnotation = kcmAnnotation,
            paramName = KCONMAPPER_FROM_CLASSES_ANNOTATION_ARG_NAME
        )
        val mapToClasses: List<KSClassDeclaration> = extractArgumentClasses(
            resolver = resolver,
            kcmAnnotation = kcmAnnotation,
            paramName = KCONMAPPER_TARGET_CLASSES_ANNOTATION_ARG_NAME
        )

        // Nothing to do if none of the mapping arguments is filled
        if (mapFromClasses.isEmpty() && mapToClasses.isEmpty()) {
            logger.warn("Missing mapping functions for @$KCONMAPPER_ANNOTATION_NAME annotated class $classDeclaration.")
            return
        }

        val fileSpec = FileSpec.builder(classDeclaration.packageName.asString(), fileName = "Test")

        mapFromClasses.forEach { sourceClass ->
            fileSpec.addFunction(
                MappingEnumClassGenerator.generateMappingFunction(
                    targetClass = classDeclaration,
                    sourceClass = sourceClass,
                )
            )
        }

        mapToClasses.forEach { targetClass ->
            fileSpec.addFunction(
                MappingEnumClassGenerator.generateMappingFunction(
                    targetClass = targetClass,
                    sourceClass = classDeclaration,
                )
            )
        }

        codeGenerator.createNewFile(
            dependencies = Dependencies(true, classDeclaration.containingFile!!),
            packageName = GENERATED_FILE_PATH,
            fileName = "${classDeclaration}$GENERATED_CLASS_SUFFIX"
        ).use { stream ->
            stream.write(fileSpec.build().toString().toByteArray())
        }
    }

    override fun visitAnnotation(annotation: KSAnnotation, data: Unit) {
        annotation.annotationType.resolve().declaration.accept(this, data)
    }
}