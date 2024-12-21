pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    plugins {
        id("com.google.devtools.ksp") version kspVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
        id("org.gradle.maven-publish")
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "multiplatform"

include(":sample")
include(":kconmapper-ksp")
include(":kconmapper-annotations")