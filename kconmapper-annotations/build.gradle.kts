plugins {
    kotlin("multiplatform")
}


kotlin {
    jvm {
        withJava()
    }
    androidNativeX64()
    androidNativeArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}

repositories {
    mavenCentral()
}
