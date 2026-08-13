import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.21"
}

paperweight {
    filterPatches = false

    upstreams.register("folia") {
        repo = github("PaperMC", "Folia")
        ref = providers.gradleProperty("foliaRef")

        patchFile {
            path = "folia-server/build.gradle.kts"
            outputFile = file("scissors-server/build.gradle.kts")
            patchFile = file("scissors-server/build.gradle.kts.patch")
        }
        patchFile {
            path = "folia-api/build.gradle.kts"
            outputFile = file("scissors-api/build.gradle.kts")
            patchFile = file("scissors-api/build.gradle.kts.patch")
        }
        patchRepo("paperApi") {
            upstreamPath = "paper-api"
            excludes = setOf("build.gradle.kts")
            patchesDir = file("scissors-api/paper-patches")
            outputDir = file("paper-api")
        }
        patchDir("foliaCheckstyle") {
            upstreamPath = "folia-checkstyle"
            excludes = setOf("build.gradle.kts.patch")
            patchesDir = file("scissors-api/folia-checkstyle-patches")
            outputDir = file("folia-checkstyle")
        }
        patchRepo("paperCheckstyle") {
            upstreamPath = "paper-checkstyle"
            patchesDir = file("scissors-api/paper-checkstyle-patches")
            outputDir = file("paper-checkstyle")
        }
        patchRepo("paperCheckstyleConfig") {
            upstreamPath = ".checkstyle"
            patchesDir = file("scissors-api/paper-checkstyle-config-patches")
            outputDir = file(".checkstyle")
        }
    }
}

// Keep build output out of the generated upstream patch worktree so rebuilding its empty patch set never scans class
// files or test results as candidate source changes.
project(":folia-checkstyle") {
    layout.buildDirectory = rootProject.layout.buildDirectory.dir("folia-checkstyle")
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
        options.isFork = true
        options.compilerArgs.addAll(listOf("-Xlint:-deprecation", "-Xlint:-removal"))
    }
    tasks.withType<Javadoc>().configureEach {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources>().configureEach {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test>().configureEach {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }

    extensions.configure<PublishingExtension> {
        repositories {
            maven("https://artifactory.papermc.io/artifactory/releases/") {
                name = "paperReleases"
                credentials(PasswordCredentials::class)
            }
        }
    }
}
