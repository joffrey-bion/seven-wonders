import de.fayard.refreshVersions.core.*

plugins {
    id("com.gradle.develocity") version "3.18"
    id("de.fayard.refreshVersions") version "0.60.5"
}

rootProject.name = "seven-wonders"

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

include("sw-common-model")
include("sw-engine")
include("sw-server")
include("sw-client")
include("sw-ui")
include("sw-bot")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        uploadInBackground = false // background upload is bad for CI, and not critical for local runs
    }
}

refreshVersions {
    versionsPropertiesFile = file("build/tmp/refreshVersions/versions.properties").apply { parentFile.mkdirs() }
    rejectVersionIf {
        @Suppress("UnstableApiUsage")
        candidate.stabilityLevel != StabilityLevel.Stable || "-alpha" in candidate.value || "-beta" in candidate.value
    }
}
