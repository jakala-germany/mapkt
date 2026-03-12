plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.ktlint)
    id("maven-publish")
}

kotlin {
    jvm()

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

publishing {
    publications {
        filterIsInstance<MavenPublication>().forEach { pub ->
            pub.configureMaven(
                artifactId = pub.artifactId,
                pomDescription = "Kotlin Symbol Processing",
                pomName = "Mapkt KSP",
            )
        }
    }
    repositories {
        /** GITHUB PUBLISHING */
        val githubActor: String? = System.getenv("GITHUB_ACTOR")
        val githubToken: String? = System.getenv("GITHUB_TOKEN")

        if (githubActor != null && githubToken != null) {
            maven(url = "https://maven.pkg.github.com/jakala-germany/mapkt") {
                name = "GitHubPackages"
                credentials {
                    username = githubActor
                    password = githubToken
                }
            }
        }
    }
}

fun MavenPublication.configureMaven(
    artifactId: String,
    pomDescription: String,
    pomName: String,
) {
    this.artifactId = artifactId
    this.version = project.version.toString()
    this.groupId = project.group.toString()

    pom {
        this.name.set(pomName)
        this.description.set(pomDescription)
        this.url.set("https://github.com/jakala-germany/mapkt")
        this.inceptionYear.set("2025")
        this.organization {
            this.name.set("Jakala Germany")
            this.url.set("https://jakala.com")
        }
        this.licenses {
            this.license {
                this.name.set("Apache-2.0")
                this.url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
    }
}