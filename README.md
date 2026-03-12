# MapKt

A Kotlin Multiplatform library that automatically generates type-safe mapping/conversion code
between similar data classes using KSP (Kotlin Symbol Processing).

Similar to [KConMapper](https://github.com/YanneckReiss/KConMapper) but with MultiPlattform support.

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
    mapTo = [Cat::class], // Map FROM Dog TO Cat
)
data class Dog(val name: String, val age: Int)

@MapKt(
    mapTo = [Dog::class], // Map FROM Cat TO Dog  
)
data class Cat(val name: String, val age: Int)
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
    val mapTo: Array<KClass<*>> // Types this class maps TO
)
```

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
