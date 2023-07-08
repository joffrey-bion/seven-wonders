import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        binaries.executable()
        browser()
        useCommonJs()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.swClient)

                implementation(libs.kotlinx.coroutines.core)

                implementation(platform(libs.kotlin.wrappers.bom.get()))
                implementation(libs.kotlin.wrappers.react.base)
                implementation(libs.kotlin.wrappers.react.dom)
                implementation(libs.kotlin.wrappers.react.redux)
                implementation(libs.kotlin.wrappers.react.router.dom)
                implementation(libs.kotlin.wrappers.blueprintjs.core)
                implementation(libs.kotlin.wrappers.blueprintjs.icons)
                implementation(libs.kotlin.wrappers.emotion)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

tasks.named<ProcessResources>("jsProcessResources") {
    val webpack = project.tasks.withType(KotlinWebpack::class).first()

    val bundleFile = webpack.outputFileName
    val publicPath = "./" // TODO get public path from webpack config

    filesMatching("*.html") {
        expand("bundle" to bundleFile, "publicPath" to publicPath)
    }
}

private val frontendDistribution by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(frontendDistribution.name, tasks.named("jsBrowserDistribution"))
}
