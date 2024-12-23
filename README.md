# kmp-mapper

Mostly blatantly stolen from [KConMapper](https://github.com/YanneckReiss/KConMapper) but with added
support for Kotlin Multiplatform.

## Usage

```kotlin
@KConMapper(
    toClasses = [Cat::class],
    fromClasses = []
)
data class Dog(val name: String, val age: Int)

@KConMapper(
    toClasses = [Dog::class],
    fromClasses = [],
)
data class Cat(val name: String, val age: Int)

fun test() {
    val dog = Dog("Fido", 5)
    val cat = Cat("Whiskers", 3)
    println(dog.toCat())
    println(cat.toDog())
}
```

## Setup

add the following to your `build.gradle` file:

```kotlin
val commonMain by getting {
    dependencies {
        implementation("com.github.yanneckreiss.kconmapper:kconmapper-annotations:x.y.z-kmp")
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") // imports generated code
    }
}
```

and

```kotlin
dependencies {
    add("kspCommonMainMetadata", "com.github.yanneckreiss.kconmapper:kconmapper-ksp:1.0.0-kmp")
}
```

This allows you to use the `@KConMapper` annotation in your **commonMain** code.

## Development

Publish both packages to your local maven repository:

```shell
./gradlew publishToMavenLocal
```