import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.multiplatform)
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
    jvm()
}
