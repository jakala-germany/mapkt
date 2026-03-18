# MapKt

A Kotlin Multiplatform library that automatically generates type-safe mapping/conversion code
between similar data classes using KSP (Kotlin Symbol Processing).

This started as a fork of [KConMapper](https://github.com/YanneckReiss/KConMapper) but evolved into its own library, including multiplatform support. 

## What is MapKt?

MapKt enables you to define simple `@MapKt` annotations on source data classes and automatically
generates conversion functions like `.toOtherType()` without writing manual `mapTo` boilerplate
code.

## Key Features

- **Automatic Mapping Generation** - KSP processor generates all mapping functions at compile-time
- **Kotlin Multiplatform** - Works across JVM, Android, iOS, and desktop platforms
- **Type-Safe Mappings** - Compile-time checked conversions between similar types
- **Zero Runtime Overhead** - Generated code is pure Kotlin with no reflection

## Quick Start

### 1. Add Dependencies

**Gradle (Kotlin):**

```kotlin
// build.gradle.kts
plugins {
    id("com.google.devtools.ksp") version "2.3.6"
}

dependencies {
    val mapktVersion = "1.1.0" // or use latest
    implementation("com.jakala.mapkt:mapkt-ksp:$mapktVersion")
    ksp("com.jakala.mapkt:mapkt-ksp:$mapktVersion")
}
```

### 2. Define Your Data Classes

Define two or more similar data classes with a shared mapping annotation:

```kotlin
@MapKt(
    mapTo = Cat::class, // Only need to annotate one class in the pair
)
data class Dog(val name: String, val age: Int)
```

### 3. Generate and Use Mapping Functions

```kotlin
val dog = Dog("Fido", 5)
val cat = Dog.toCat(dog)           // or dog.toCat()
println(cat.name)                  // Output: Fido
println(cat.age)                   // Output: 5

// Works in reverse!
val convertedDog = Cat.toDog(cat)  
```

## Annotations Reference

### `@MapKt` - Map Generation Annotation

Define mappings on data classes that should generate conversion functions.

```kotlin
/**
 * Generates mapping functions from this class to the specified types.
 */
annotation class MapKt(
    val mapTo: KClass<*> // Types this class maps TO
)
```

If you need more than one Mapping you can just add more `@MapKt` annotations:

```kotlin
@MapKt(mapTo = Cat::class)
@MapKt(mapTo = Mouse::class)
data class Dog(val name: String, val age: Int)
```

### Parameter Mapping

By default, MapKt matches properties by name and type. You can customize mappings using additional annotations:

```kotlin
data class Dog(
    val name: String,
    val age: Int,
    val ownersCount: Int,
)

@MapKt(
    mapTo = Dog::class,
    aliases = [
        PropertyMapping(source = "butlerCount", target = "ownersCount")
    ]
)
data class Cat(
    val name: String,
    val age: Int,
    val butlerCount: Int, 
)
```

This will generate mapping functions that correctly map `butlerCount` in `Cat` to `ownersCount` in `Dog`.

## Project Structure

```
mapkt-annotations/     # Annotation definitions (no dependencies)
mapkt-ksp/             # KSP processor that generates mapping code
sample/                # Sample project demonstrating usage
```

## Development & Building

Look at the Provided [Justfile](https://github.com/casey/just), [here](Justfile), it contains tasks for building,
testing, and publishing the library. You don't need to use Just, you can run the equivalent Gradle commands directly.

### Publishing Locally

```bash
# Build and publish artifacts to your local Maven repo
./gradlew clean publishToMavenLocal

# Verify installation by building sample again
./gradlew :sample:build
```

### Releasing a new version
Releases are published automatically to Maven Central via GitHub Actions.

1. Update the version in the root `build.gradle.kts`.
2. Commit the change with the format: `release version x.y.z`
3. Create a git tag `git tag v<version>` and `git push origin v<version>`