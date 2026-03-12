plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.publish) apply false
    alias(libs.plugins.multiplatform)
}

allprojects {
    version = "1.0.0"
    group = "io.github.jakala-germany"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

kotlin {
    jvm()
}