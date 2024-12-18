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

    extensions.configure<PublishingExtension> {
        publishing {
            publications {
                afterEvaluate {
                    filterIsInstance<MavenPublication>().forEach { pub ->
                        pub.artifactId = configuration.artifactId
                        pub.version = this.version.toString() + "-SNAPSHOT3"
                        pub.pom {
                            name.set(configuration.name)
                            description.set(configuration.description)
//                        url.set("https://github.com/jakala-germany/kmm-hvv-switch")
                            inceptionYear.set(configuration.inceptionYear)
                            organization {
//                            name.set("FFW")
//                            url.set("https://ffw.com/")
                            }
                        }
                    }
                }
            }
        }
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
