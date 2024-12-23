val kspVersion: String by project

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("com.squareup:kotlinpoet:2.0.0")
                implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
                implementation(kotlin("stdlib"))
                implementation(project(":kconmapper-annotations"))
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}
