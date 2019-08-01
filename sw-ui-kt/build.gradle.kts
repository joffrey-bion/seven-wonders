import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("js")
}

repositories {
    // repository added for kotlin-wrappers resolutions
    maven(url = "https://kotlin.bintray.com/kotlin-js-wrappers")
}

kotlin {
    target {
        browser()
    }
    sourceSets {
        main {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(project(":sw-client"))

                implementation("org.jetbrains:kotlin-react:16.8.6-pre.80-kotlin-1.3.41")
                implementation(npm("react", "16.8.6"))

                implementation("org.jetbrains:kotlin-react-dom:16.8.6-pre.80-kotlin-1.3.41")
                implementation(npm("react-dom", "16.8.6"))

                implementation("org.jetbrains:kotlin-react-redux:5.0.7-pre.80-kotlin-1.3.41")
                implementation(npm("react-redux", "5.0.7"))
                implementation(npm("redux", "4.0.4"))

                // seems to be required by "kotlin-extensions" JS lib
                implementation(npm("core-js", "3.1.4"))

                // implementation(npm("@blueprintjs/core", "3.15.1"))
            }
        }
    }
}

tasks {
    compileKotlinJs {
        kotlinOptions.metaInfo = true
        kotlinOptions.sourceMap = true
        // non-plain module kind is necessary to use top-level declarations from UMD modules (e.g. react-redux)
        // because the Kotlin wrapper didn't specify @JsNonModule
        kotlinOptions.moduleKind = "commonjs"
        kotlinOptions.main = "call"
    }

    "processResources"(ProcessResources::class) {
        val webpack = project.tasks.withType(KotlinWebpack::class).first()
        into(webpack.destinationDirectory!!)

        val bundleFile = webpack.archiveFile.name
        val publicPath = "./" // TODO get public path from webpack config

        filesMatching("*.html") {
            expand("bundle" to bundleFile, "publicPath" to publicPath)
        }
    }
}
