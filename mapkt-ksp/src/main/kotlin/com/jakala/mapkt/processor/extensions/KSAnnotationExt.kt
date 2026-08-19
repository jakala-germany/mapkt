package com.jakala.mapkt.processor.extensions

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.annotations.PropertyMapping
import com.jakala.mapkt.processor.util.Alias

// Typed access to the members of a `MapKt` annotation.
//
// The members are read off the KSP model instead of a `getAnnotationsByType`
// proxy, that proxy throws for a `KClass` member such as `MapKt.mapTo` and
// throws again for every member that was left at its default value.

/** [MapKt.mapTo] resolved to its declaration. */
internal fun KSAnnotation.getMapToDeclaration(resolver: Resolver): KSClassDeclaration? {
    val mapTo = getMapToType()?.declaration?.qualifiedName ?: return null
    return resolver.getClassDeclarationByName(mapTo)
}

/** The simple name of [MapKt.mapTo], for callers that have no [Resolver] at hand. */
internal fun KSAnnotation.getMapToSimpleName(): String? =
    getMapToType()?.declaration?.simpleName?.asString()

/** [MapKt.aliases] in the representation used by the generators. */
internal fun KSAnnotation.getAliases(): List<Alias> =
    getArgument<List<KSAnnotation>>(MapKt::aliases.name)
        .orEmpty()
        .map { propertyMapping ->
            Alias(
                source = propertyMapping.getArgument(PropertyMapping::source.name) ?: "",
                target = propertyMapping.getArgument(PropertyMapping::target.name) ?: "",
            )
        }

/** [MapKt.ignores], the properties that have to be passed to the generated function instead. */
internal fun KSAnnotation.getIgnores(): List<String> =
    getArgument<List<String>>(MapKt::ignores.name).orEmpty()

private fun KSAnnotation.getMapToType(): KSType? = getArgument(MapKt::mapTo.name)

@Suppress("UNCHECKED_CAST")
private fun <T> KSAnnotation.getArgument(name: String): T? =
    (arguments + defaultArguments)
        .firstOrNull { argument -> argument.name?.asString() == name }
        ?.value as? T