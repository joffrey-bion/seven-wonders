import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("js")
}

kotlin {
    js {
        binaries.executable()
        browser()
        useCommonJs()
    }
    sourceSets {
        main {
            dependencies {
                implementation(projects.swClient)

                implementation(project.dependencies.enforcedPlatform(libs.kotlin.wrappers.bom))
                implementation(libs.kotlin.wrappers.react.base)
                implementation(libs.kotlin.wrappers.react.dom)
                implementation(libs.kotlin.wrappers.react.redux)
                implementation(libs.kotlin.wrappers.react.router.dom)
                implementation(libs.kotlin.wrappers.styled.next)
                implementation(libs.kotlin.wrappers.blueprintjs.core)
                implementation(libs.kotlin.wrappers.blueprintjs.icons)
            }
        }
        test {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks {
    "processResources"(ProcessResources::class) {
        val webpack = project.tasks.withType(KotlinWebpack::class).first()

        val bundleFile = webpack.outputFileName
        val publicPath = "./" // TODO get public path from webpack config

        filesMatching("*.html") {
            expand("bundle" to bundleFile, "publicPath" to publicPath)
        }
    }
}
