plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.publish)
}

kotlin {
    jvm()

    explicitApi()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlinPoet)
                implementation(libs.ksp.processing.api)
                implementation(kotlin("stdlib"))
                implementation(project(":mapkt-annotations"))
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}

mavenPublishing {
    pom {
        name.set(property("POM_NAME") as String)
        description.set(property("POM_DESCRIPTION") as String)
        url.set(property("POM_URL") as String)

        scm {
            url.set(property("POM_SCM_URL") as String)
            connection.set(property("POM_SCM_CONNECTION") as String)
            developerConnection.set(property("POM_SCM_DEV_CONNECTION") as String)
        }
    }
}