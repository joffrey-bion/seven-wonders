import com.gradle.scan.plugin.BuildScanExtension
import de.fayard.refreshVersions.core.*

plugins {
    id("com.gradle.enterprise") version "3.10.2"
    id("de.fayard.refreshVersions") version "0.51.0"
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

        val isCIBuild = !System.getenv("CI").isNullOrEmpty()
        publishAlwaysIf(isCIBuild)
        tag(if (isCIBuild) "CI" else "local")

        val isGithubActionsBuild = !System.getenv("GITHUB_ACTIONS").isNullOrEmpty()
        if (isGithubActionsBuild) {
            addGithubActionsData()
        }
    }
}

fun BuildScanExtension.addGithubActionsData() {
    value("GitHub Event", System.getenv("GITHUB_EVENT_NAME"))
    value("GitHub Workflow", System.getenv("GITHUB_WORKFLOW"))
    value("GitHub Run ID", System.getenv("GITHUB_RUN_ID"))
    value("GitHub Run number", System.getenv("GITHUB_RUN_NUMBER"))
    value("Commit", System.getenv("GITHUB_SHA"))

    val ref = System.getenv("GITHUB_REF") ?: ""
    val isTagBuild = ref.startsWith("refs/tags/")
    if (isTagBuild) {
        tag("tag")
        value("Tag", ref.removePrefix("refs/tags/"))
    } else {
        value("Branch", ref.removePrefix("refs/heads/"))
    }
}

refreshVersions {
    rejectVersionIf {
        candidate.stabilityLevel != StabilityLevel.Stable || "-alpha" in candidate.value || "-beta" in candidate.value
    }
}
