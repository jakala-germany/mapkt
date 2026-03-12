plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.publish)
}

kotlin {
    jvm()
    androidNativeX64()
    androidNativeArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}

mavenPublishing {
    publishToMavenCentral()

    // Sign only if the key exists (CI environment)
    if (project.hasProperty("signingInMemoryKey")) {
        signAllPublications()
    }

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
