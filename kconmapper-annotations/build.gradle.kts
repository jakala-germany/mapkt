plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser()
        nodejs()
    }
    linuxX64()
    // requires Android SDK
    androidNativeX64()
    androidNativeArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}

repositories {
    mavenCentral()
}
