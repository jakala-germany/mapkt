package com.jakala.mapkt.processor.generator

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter
import com.jakala.mapkt.processor.generator.argument.MatchingArgument
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName

private const val MAP_KT_PROPERTY_ANNOTATION_NAME = "MapKtProperty"
private const val MAP_KT_PROPERTY_ANNOTATION_PARAM_NAME_ALIASES = "aliases"

class MappingFunctionGenerator(
    private val resolver: Resolver,
    private val logger: KSPLogger,
) : FunctionGenerator {
    override fun generateMappingFunction(
        sourceClass: KSClassDeclaration,
        targetClass: KSClassDeclaration,
    ): FunSpec {
        val targetClassTypeParameters: List<KSTypeParameter> = targetClass.typeParameters

        return generateExtensionMapperFunctionForSourceClass(
            sourceClass = sourceClass,
            annotatedClass = targetClass,
            targetClassTypeParameters = targetClassTypeParameters,
        )
    }

    @OptIn(KspExperimental::class)
    private fun generateExtensionMapperFunctionForSourceClass(
        sourceClass: KSClassDeclaration,
        annotatedClass: KSClassDeclaration,
        targetClassTypeParameters: List<KSTypeParameter>,
    ): FunSpec {
        val (missingConstructorArguments, matchingConstructorArguments) =
            extractMatchingAndMissingConstructorArguments(
                annotatedClass = annotatedClass,
                sourceClass = sourceClass,
                targetClassTypeParameters = targetClassTypeParameters,
            )

        val functionBuilder =
            FunSpec
                .builder(generateExtensionFunctionName(annotatedClass))
                .receiver(sourceClass.getClassName())
                .returns(annotatedClass.getClassName())

        if (targetClassTypeParameters.isNotEmpty()) {
            targetClassTypeParameters.forEach { typeParam ->
                functionBuilder.addTypeVariable(
                    TypeVariableName(typeParam.name.asString()),
                )
            }
        }

        missingConstructorArguments.forEach { param ->
            functionBuilder.addParameter(
                ParameterSpec
                    .builder(param.name!!.asString(), param.type.resolve().toTypeName())
                    .defaultValue(
                        CodeBlock
                            .builder()
                            .apply {
                                val targetName = param.name!!.asString()

                                val sourceProperty =
                                    sourceClass.getAllProperties().firstOrNull { prop ->
                                        val propName = prop.simpleName.asString()
                                        val aliases = findAliases(prop.annotations)

                                        propName == targetName || aliases.contains(targetName)
                                    }

                                val sourceName =
                                    sourceProperty?.simpleName?.asString() ?: targetName

                                add("this.%L", sourceName)

                                if (param.type.resolve().isMarkedNullable) {
                                    add("?")
                                }

                                val isList =
                                    param.type
                                        .resolve()
                                        .arguments
                                        .takeIf { it.isNotEmpty() }
                                        ?.let {
                                            val collection =
                                                resolver
                                                    .getKotlinClassByName("kotlin.collections.List")
                                                    ?.asType(it)
                                                    ?.apply {
                                                        if (param.type.resolve().isMarkedNullable) {
                                                            makeNullable()
                                                        }
                                                    }

                                            param.type.resolve().isAssignableFrom(collection!!)
                                        } ?: false

                                if (isList) {
                                    add(
                                        ".map{ it.to%L() }",
                                        param.type
                                            .resolve()
                                            .arguments
                                            .first()
                                            .type!!,
                                    )
                                } else {
                                    add(".to%L()", param.type.toString().replace("?", ""))
                                }
                            }.build(),
                    ).build(),
            )
        }

        val constructorBuilder =
            CodeBlock
                .builder()
                .add(
                    "return %T(\n",
                    annotatedClass.getClassName(),
                )

        matchingConstructorArguments.forEach { matchingArg ->
            constructorBuilder.addStatement(
                "\t%L = this.%L%L,",
                matchingArg.targetClassPropertyName,
                matchingArg.sourceClassPropertyName,
                matchingArg.targetClassPropertyGenericTypeName?.let { " as $it" } ?: "",
            )
        }

        missingConstructorArguments.forEach { param ->
            constructorBuilder.addStatement(
                "\t%L = %L,",
                param.name!!.asString(),
                param.name!!.asString(),
            )
        }

        constructorBuilder.add(")")

        functionBuilder.addCode(constructorBuilder.build())

        return functionBuilder.build()
    }

    private fun extractMatchingAndMissingConstructorArguments(
        annotatedClass: KSClassDeclaration,
        sourceClass: KSClassDeclaration,
        targetClassTypeParameters: List<KSTypeParameter>,
    ): Pair<MutableList<KSValueParameter>, MutableList<MatchingArgument>> {
        val missingArguments = mutableListOf<KSValueParameter>()
        val matchingArguments = mutableListOf<MatchingArgument>()

        annotatedClass.primaryConstructor?.parameters?.forEach { annotatedClassParam ->

            val targetPropName = annotatedClassParam.name!!.asString()
            var matchingArgument: MatchingArgument? = null

            for (sourceProp in sourceClass.getAllProperties()) {
                val sourcePropName = sourceProp.simpleName.asString()
                val sourceAliases = findAliases(sourceProp.annotations)

                val aliasMatch = sourceAliases.contains(targetPropName)

                if (sourcePropName == targetPropName || aliasMatch) {
                    val acPropType: KSType = annotatedClassParam.type.resolve()
                    val sourcePropType: KSType = sourceProp.type.resolve()

                    val genericTypeParam =
                        targetClassTypeParameters.firstOrNull {
                            it.name.asString() == acPropType.getName()
                        }

                    val isAssignable =
                        evaluateKSTypeAssignable(
                            parameterTypeFromSourceClass = sourcePropType,
                            parameterTypeFromTargetClass = acPropType,
                            isGenericType = genericTypeParam != null,
                        )

                    if (isAssignable) {
                        matchingArgument =
                            MatchingArgument(
                                targetClassPropertyName = targetPropName,
                                sourceClassPropertyName = sourcePropName,
                                targetClassPropertyGenericTypeName =
                                    genericTypeParam?.let {
                                        "${it.name.asString()}${acPropType.markedNullableAsString()}"
                                    },
                            )

                        break
                    }
                }
            }

            if (matchingArgument != null) {
                matchingArguments.add(matchingArgument)
            } else {
                missingArguments.add(annotatedClassParam)
            }
        }

        return Pair(missingArguments, matchingArguments)
    }

    private fun evaluateKSTypeAssignable(
        parameterTypeFromSourceClass: KSType,
        parameterTypeFromTargetClass: KSType,
        isGenericType: Boolean,
    ): Boolean {
        if (parameterTypeFromSourceClass.isMarkedNullable &&
            !parameterTypeFromTargetClass.isMarkedNullable
        ) {
            return false
        }

        if (!isGenericType &&
            !parameterTypeFromSourceClass.compareByDeclaration(
                parameterTypeFromTargetClass.declaration,
            )
        ) {
            return false
        }

        return isGenericType ||
            parameterTypeFromSourceClass.arguments.matches(
                parameterTypeFromTargetClass.arguments,
            )
    }

    private fun List<KSTypeArgument>.matches(otherArguments: List<KSTypeArgument>): Boolean {
        if (this.size != otherArguments.size) return false

        return this.zip(otherArguments).all { (a, b) ->

            val typeA = a.type?.resolve() ?: return@all false
            val typeB = b.type?.resolve() ?: return@all false

            typeA.compareByDeclaration(typeB.declaration) &&
                (if (typeA.isMarkedNullable) typeB.isMarkedNullable else true) &&
                typeA.arguments.matches(typeB.arguments)
        }
    }

    private fun generateExtensionFunctionName(targetClass: KSDeclaration): String {
        val targetClassName: String = targetClass.simpleName.getShortName()
        return "to$targetClassName"
    }

    private fun findAliases(annotations: Sequence<KSAnnotation>): Set<String> =
        annotations
            .filter { it.shortName.asString() == MAP_KT_PROPERTY_ANNOTATION_NAME }
            .flatMap { it.arguments }
            .filter { it.name?.asString() == MAP_KT_PROPERTY_ANNOTATION_PARAM_NAME_ALIASES }
            .mapNotNull { arg ->
                when (val value = arg.value) {
                    is Collection<*> -> value.filterIsInstance<String>().toSet()
                    else -> null
                }
            }.fold(emptySet()) { acc, set -> acc + set }

    private fun KSType.toTypeName(): TypeName {
        val className =
            this.declaration.qualifiedName?.asString()?.let {
                ClassName.bestGuess(it)
            } ?: ClassName("", "Unit")

        return if (this.arguments.isNotEmpty()) {
            val typeArguments =
                this.arguments.map {
                    it.type?.resolve()?.toTypeName() ?: STAR
                }

            className.parameterizedBy(*typeArguments.toTypedArray())
        } else {
            className
        }.copy(nullable = this.isMarkedNullable)
    }

    private fun KSType.compareByDeclaration(other: KSDeclaration): Boolean =
        this.declaration.qualifiedName?.asString() ==
            other.qualifiedName?.asString()

    private fun KSDeclaration.getClassName(): ClassName =
        ClassName.bestGuess(
            this.qualifiedName?.asString() ?: "",
        )

    private fun KSType.getName(): String = this.toTypeName().toString()

    private fun KSType.markedNullableAsString(): String = if (this.isMarkedNullable) "?" else ""
}