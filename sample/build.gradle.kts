import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    alias(libs.plugins.ktlint)
}

ktlint {
    filter {
        exclude { it.file.path.contains("/generated/") }
    }
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        commonMain {
            kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
            dependencies {
                implementation(project(":mapkt-annotations"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

val kspCommonMain = "kspCommonMainKotlinMetadata"

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    dependsOn(kspCommonMain)
}
tasks.withType<KspAATask>().configureEach {
    if (name != kspCommonMain) dependsOn(kspCommonMain)
}
tasks.matching { it.name.startsWith("runKtlint") }.configureEach {
    dependsOn(kspCommonMain)
}

dependencies {
    add("kspCommonMainMetadata", project(":mapkt-ksp"))
}