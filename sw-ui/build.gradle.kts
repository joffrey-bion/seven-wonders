import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
    // for kotlin-wrappers resolutions
    maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    // for kotlinx-html (dependency of kotlin-react-dom)
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    jcenter() // for kotlinx-html-jvm:0.7.2 needed by dokka (and not migrated)
}

val kotlinWrappersVersion = "pre.150-kotlin-1.4.31"

kotlin {
    js {
        browser()
        useCommonJs()
    }
    sourceSets {
        main {
            dependencies {
                implementation(project(":sw-client"))

                val reactVersion = "17.0.2"
                implementation("org.jetbrains:kotlin-react:$reactVersion-$kotlinWrappersVersion")
                implementation(npm("react", reactVersion))
                implementation("org.jetbrains:kotlin-react-dom:$reactVersion-$kotlinWrappersVersion")
                implementation(npm("react-dom", reactVersion))

                val reactReduxVersion = "7.2.2"
                implementation("org.jetbrains:kotlin-react-redux:$reactReduxVersion-$kotlinWrappersVersion")
                implementation(npm("react-redux", reactReduxVersion))
                // redux version aligned with the wrapper's build:
                // https://github.com/JetBrains/kotlin-wrappers/blob/master/gradle.properties#L42
                implementation(npm("redux", "4.0.5"))

                val reactRouterDomVersion = "5.2.0"
                implementation("org.jetbrains:kotlin-react-router-dom:$reactRouterDomVersion-$kotlinWrappersVersion")
                implementation(npm("react-router-dom", reactRouterDomVersion))

                val styledComponentsVersion = "5.2.1"
                implementation("org.jetbrains:kotlin-styled:$styledComponentsVersion-$kotlinWrappersVersion")
                implementation(npm("styled-components", styledComponentsVersion))
                implementation(npm("inline-style-prefixer", "6.0.0"))

                implementation(npm("@blueprintjs/core", "3.38.1"))
                implementation(npm("@blueprintjs/icons", "3.24.0"))
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
