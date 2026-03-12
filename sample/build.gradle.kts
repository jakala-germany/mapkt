import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    alias(libs.plugins.ktlint)
}

ktlint {
    filter {
        exclude { element ->
            val path = element.file.path
            path.contains("/generated/")
        }
    }
}

version = "1.0-SNAPSHOT13"

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(project(":mapkt-annotations"))
                kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            }
        }
    }
}

project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonTestKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

tasks.matching { it.name.startsWith("runKtlint") }.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}

dependencies {
    add("kspCommonMainMetadata", project(":mapkt-ksp"))
}