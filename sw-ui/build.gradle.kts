import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("js")
}

repositories {
    // repository added for kotlin-wrappers resolutions
    maven(url = "https://kotlin.bintray.com/kotlin-js-wrappers")
}

val kotlinWrappersVersion = "pre.93-kotlin-1.3.70"

kotlin {
    target {
        browser()
        useCommonJs()
    }
    sourceSets {
        main {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(project(":sw-client"))

                val reactVersion = "16.13.0"
                implementation("org.jetbrains:kotlin-react:$reactVersion-$kotlinWrappersVersion")
                implementation(npm("react", reactVersion))
                implementation("org.jetbrains:kotlin-react-dom:$reactVersion-$kotlinWrappersVersion")
                implementation(npm("react-dom", reactVersion))

                val reactReduxVersion = "5.0.7"
                implementation("org.jetbrains:kotlin-react-redux:$reactReduxVersion-$kotlinWrappersVersion")
                implementation(npm("react-redux", reactReduxVersion))
                implementation(npm("redux", "4.0.4"))

                val reactRouterDomVersion = "4.3.1"
                implementation("org.jetbrains:kotlin-react-router-dom:$reactRouterDomVersion-$kotlinWrappersVersion")
                implementation(npm("react-router-dom", reactRouterDomVersion))

                implementation("org.jetbrains:kotlin-styled:1.0.0-$kotlinWrappersVersion")
                implementation(npm("styled-components", "4.3.2"))
                implementation(npm("inline-style-prefixer", "5.1.0"))

                // seems to be required by "kotlin-extensions" JS lib
                implementation(npm("core-js", "3.1.4"))

                implementation(npm("@blueprintjs/core", "3.24.0"))
                implementation(npm("@blueprintjs/icons", "3.14.0"))
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
        into(webpack.destinationDirectory!!)

        val bundleFile = webpack.outputFileName
        val publicPath = "./" // TODO get public path from webpack config

        filesMatching("*.html") {
            expand("bundle" to bundleFile, "publicPath" to publicPath)
        }
    }
}
