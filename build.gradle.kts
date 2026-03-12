plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.multiplatform)
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