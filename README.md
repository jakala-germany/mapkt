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

Annotate one class of a pair with the class it should be mapped to:

```kotlin
@MapKt(
    mapTo = UserDto::class, // Only need to annotate one class in the pair
)
data class UserEntity(val name: String, val age: Int)

data class UserDto(val name: String, val age: Int)
```

### 3. Generate and Use Mapping Functions

Both directions are generated as extension functions in the `com.jakala.mapkt` package:

```kotlin
import com.jakala.mapkt.toUserDto
import com.jakala.mapkt.toUserEntity

val entity = UserEntity("Fido", 5)
val dto = entity.toUserDto()
println(dto.name)                    // Output: Fido
println(dto.age)                     // Output: 5

// Works in reverse!
val convertedEntity = dto.toUserEntity()
```

## Annotations Reference

### `@MapKt` - Map Generation Annotation

Define mappings on data classes that should generate conversion functions.

```kotlin
/**
 * Generates mapping functions between the annotated class and `mapTo`,
 * one for each direction.
 */
@Repeatable
annotation class MapKt(
    val mapTo: KClass<*>,                     // Type this class is mapped from and to
    val aliases: Array<PropertyMapping> = [], // Properties that differ in name
    val ignores: Array<String> = [],          // Properties that are not mapped
)
```

If you need more than one mapping you can just add more `@MapKt` annotations,
every annotation carries its own `aliases` and `ignores`:

```kotlin
@MapKt(mapTo = UserDto::class)
@MapKt(mapTo = UserResponse::class)
data class UserEntity(val name: String, val age: Int)
```

### Parameter Mapping

By default, MapKt matches properties by name and type. Properties that are named
differently on the two classes are paired up with `aliases`:

```kotlin
data class UserDto(
    val name: String,
    val age: Int,
    val addressCount: Int,
)

@MapKt(
    mapTo = UserDto::class,
    aliases = [
        PropertyMapping(source = "shippingAddressCount", target = "addressCount"),
    ],
)
data class UserEntity(
    val name: String,
    val age: Int,
    val shippingAddressCount: Int,
)
```

`source` is the property of the annotated class, `target` the property of the `mapTo`
class. The alias is applied in both directions, so it only has to be declared once.

### Parameter Ignoring

By default, MapKt tries to map all properties. You can ignore properties that have no
corresponding match (or that you just don't want mapped) with `ignores`. An ignored
property has to be passed as a parameter of the generated function instead:

```kotlin
@MapKt(
    mapTo = UserDto::class,
    ignores = ["dbId"], // This property will be ignored during mapping
)
data class UserEntity(
    val dbId: Long,
    val name: String,
    val age: Int,
)

// region Generated Code

// The ignored property is simply dropped in this direction
public fun UserEntity.toUserDto(): UserDto = UserDto(
    name = this.name,
    age = this.age,
)

// And the reverse mapping requires you to set it as a parameter
public fun UserDto.toUserEntity(dbId: Long): UserEntity = UserEntity(
    name = this.name,
    age = this.age,
    dbId = dbId,
)

// endregion
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

### Releasing a new version
Releases are published automatically to Maven Central via GitHub Actions.

1. Update the version in the root `build.gradle.kts`.
2. Commit the change with the format: `release version x.y.z`
3. Create a git tag `git tag v<version>` and `git push origin v<version>`