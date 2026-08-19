package com.jakala.mapkt.processor.generator.implementations

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter
import com.jakala.mapkt.processor.generator.FunctionGenerator
import com.jakala.mapkt.processor.generator.argument.MatchingArgument
import com.jakala.mapkt.processor.util.Alias
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName

internal class MappingFunctionGenerator(
    private val resolver: Resolver,
    private val logger: KSPLogger,
) : FunctionGenerator {
    override fun generateMappingFunction(
        sourceClass: KSClassDeclaration,
        targetClass: KSClassDeclaration,
        aliases: List<Alias>,
        ignores: List<String>,
    ): FunSpec {
        val targetClassTypeParameters: List<KSTypeParameter> = targetClass.typeParameters

        return generateExtensionMapperFunctionForSourceClass(
            sourceClass = sourceClass,
            annotatedClass = targetClass,
            targetClassTypeParameters = targetClassTypeParameters,
            aliases = aliases,
            ignores = ignores,
        )
    }

    @OptIn(KspExperimental::class)
    private fun generateExtensionMapperFunctionForSourceClass(
        sourceClass: KSClassDeclaration,
        annotatedClass: KSClassDeclaration,
        targetClassTypeParameters: List<KSTypeParameter>,
        aliases: List<Alias>,
        ignores: List<String>,
    ): FunSpec {
        val (missingConstructorArguments, matchingConstructorArguments) =
            extractMatchingAndMissingConstructorArguments(
                annotatedClass = annotatedClass,
                sourceClass = sourceClass,
                targetClassTypeParameters = targetClassTypeParameters,
                aliases = aliases,
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
            val paramName = param.name!!.asString()
            functionBuilder.addParameter(
                ParameterSpec
                    .builder(paramName, param.type.resolve().toTypeName())
                    .run {
                        if (paramName !in ignores) {
                            this.defaultValue(
                                createDefaultBlock(param.name, sourceClass, aliases, param),
                            )
                        } else {
                            this
                        }
                    }.build(),
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

    @OptIn(KspExperimental::class)
    private fun createDefaultBlock(
        name: KSName?,
        sourceClass: KSClassDeclaration,
        aliases: List<Alias>,
        param: KSValueParameter,
    ): CodeBlock =
        CodeBlock
            .builder()
            .apply {
                val targetName = name!!.asString()

                val sourceProperty =
                    sourceClass.getAllProperties().firstOrNull { prop ->
                        val propName = prop.simpleName.asString()

                        fun Alias.sourceFitsProp() = source == propName && target == targetName

                        fun Alias.targetFitsProp() = target == propName && source == targetName

                        propName == targetName ||
                            aliases.any { alias ->
                                alias.sourceFitsProp() || alias.targetFitsProp()
                            }
                    }

                val sourceName = sourceProperty?.simpleName?.asString() ?: targetName

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
            }.build()

    private fun extractMatchingAndMissingConstructorArguments(
        annotatedClass: KSClassDeclaration,
        sourceClass: KSClassDeclaration,
        targetClassTypeParameters: List<KSTypeParameter>,
        aliases: List<Alias>,
    ): Pair<MutableList<KSValueParameter>, MutableList<MatchingArgument>> {
        val missingArguments = mutableListOf<KSValueParameter>()
        val matchingArguments = mutableListOf<MatchingArgument>()

        annotatedClass.primaryConstructor?.parameters?.forEach { annotatedClassParam ->

            var targetPropName = annotatedClassParam.name!!.asString()
            var matchingArgument: MatchingArgument? = null

            for (sourceProp in sourceClass.getAllProperties()) {
                var sourcePropName = sourceProp.simpleName.asString()
                val targetMatchesTarget =
                    aliases.any { mapping ->
                        mapping.target == targetPropName && mapping.source == sourcePropName
                    }

                val targetMatchesSource =
                    aliases.any { mapping ->
                        mapping.target == sourcePropName && mapping.source == targetPropName
                    }

                if (sourcePropName == targetPropName ||
                    targetMatchesSource ||
                    targetMatchesTarget
                ) {
                    if (targetMatchesSource) {
                        // Replace with the mapping target name if an alias is found
                        val matchingAlias =
                            aliases.first { mapping ->
                                mapping.target == sourcePropName &&
                                    mapping.source == targetPropName
                            }
                        targetPropName = matchingAlias.source
                        sourcePropName = matchingAlias.target
                    } else if (targetMatchesTarget) {
                        // Replace with the mapping target name if an alias is found
                        val matchingAlias =
                            aliases.first { mapping ->
                                mapping.target == targetPropName &&
                                    mapping.source == sourcePropName
                            }
                        targetPropName = matchingAlias.target
                        sourcePropName = matchingAlias.source
                    }
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