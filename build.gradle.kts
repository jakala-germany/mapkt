plugins {
    kotlin("multiplatform") apply false
}

allprojects {
    version = "1.0.0-alpha08"
    group = "com.github.yanneckreiss.kconmapper"

    repositories {
        mavenCentral()
    }
}