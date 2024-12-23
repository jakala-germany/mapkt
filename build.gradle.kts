import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") apply false
    id("org.gradle.maven-publish") apply true
}

repositories {
    mavenCentral()
    mavenLocal()
}

allprojects {
    version = "1.0.0-kmp"
    group = "com.github.yanneckreiss.kconmapper"

    repositories {
        mavenCentral()
        mavenLocal()
    }

    val publishExtension =
        extensions.create("mavenPublishing", MavenPublishingExtension::class.java)
    val configuration = MavenPublishConfiguration()
    afterEvaluate {
        configuration.artifactId = publishExtension.artifactId
        configuration.name = publishExtension.name
        configuration.description = publishExtension.description
        configuration.inceptionYear = publishExtension.inceptionYear
    }

    with(pluginManager) {
        apply("org.gradle.maven-publish")
    }

    if (!project.name.contains("sample", ignoreCase = true)) {
        apply(plugin = "maven-publish")

        configure<PublishingExtension> {
            publications {
                register<MavenPublication>("maven") {
                    groupId = project.group.toString()
                    artifactId = when {
                        project.name.contains("ksp") -> "ksp"
                        project.name.contains("annotations") -> "annotations"
                        else -> project.name
                    }
                    version = project.version.toString()
                }
            }
        }
    }

}

tasks.register<GradleBuild>("publishLocal") {
    tasks = listOf(
        ":kconmapper-ksp:publishToMavenLocal",
        ":kconmapper-annotations:publishToMavenLocal"
    )
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

open class MavenPublishingExtension {
    var artifactId: String? = null
    var name: String? = null
    var description: String? = null
    var inceptionYear: String? = null
}

private data class MavenPublishConfiguration(
    var artifactId: String? = null,
    var name: String? = null,
    var description: String? = null,
    var inceptionYear: String? = null,
)
