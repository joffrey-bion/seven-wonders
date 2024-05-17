import com.gradle.scan.plugin.BuildScanExtension
import de.fayard.refreshVersions.core.*

plugins {
    id("com.gradle.enterprise") version "3.17.4"
    id("de.fayard.refreshVersions") version "0.60.5"
}

rootProject.name = "seven-wonders"

include("sw-common-model")
include("sw-engine")
include("sw-server")
include("sw-client")
include("sw-ui")
include("sw-bot")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlways()
    }
}

refreshVersions {
    versionsPropertiesFile = file("build/tmp/refreshVersions/versions.properties").apply { parentFile.mkdirs() }
    rejectVersionIf {
        candidate.stabilityLevel != StabilityLevel.Stable || "-alpha" in candidate.value || "-beta" in candidate.value
    }
}
