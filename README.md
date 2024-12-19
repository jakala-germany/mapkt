# kmp-mapper

Mostly blatantly stolen from [KConMapper](https://github.com/YanneckReiss/KConMapper) but with added support for Kotlin Multiplatform.

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