import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

version = "1.0-SNAPSHOT13"

kotlin {
    jvm {
        withJava()
    }
    androidNativeX64() {
        binaries {
            executable()
        }
    }
    androidNativeArm64() {
        binaries {
            executable()
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kconmapper-annotations"))
            }
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}

project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":kconmapper-ksp"))
/*    add("kspJvm", project(":kconmapper-ksp"))
    add("kspJvmTest", project(":kconmapper-ksp"))
    add("kspJs", project(":kconmapper-ksp"))
    add("kspJsTest", project(":kconmapper-ksp"))
    add("kspAndroidNativeX64", project(":kconmapper-ksp"))
    add("kspAndroidNativeX64Test", project(":kconmapper-ksp"))
    add("kspAndroidNativeArm64", project(":kconmapper-ksp"))
    add("kspAndroidNativeArm64Test", project(":kconmapper-ksp"))
    add("kspLinuxX64", project(":kconmapper-ksp"))*/
//    add("kspLinuxX64Test", project(":kconmapper-ksp"))
//    add("kspMingwX64", project(":kconmapper-ksp"))
//    add("kspMingwX64Test", project(":kconmapper-ksp"))

    // The universal "ksp" configuration has performance issue and is deprecated on multiplatform since 1.0.1
    // ksp(project(":kconmapper-ksp"))
}
