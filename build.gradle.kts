plugins {
    kotlin("multiplatform") version "2.2.0"
    id("maven-publish")
}

repositories {
    mavenCentral()
    mavenLocal()
}

allprojects {
    version = "1.2.0"
    group = "com.jakala.mapkt"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

kotlin {
    jvm {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                }
            }
        }
    }
}