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

                implementation("org.jetbrains:kotlin-react:16.6.0-pre.78-kotlin-1.3.41")
                implementation(npm("react", "16.8.3"))
                implementation("org.jetbrains:kotlin-react-dom:16.6.0-pre.78-kotlin-1.3.41")
                implementation(npm("react-dom", "16.8.3"))
                // implementation(npm("@blueprintjs/core", "3.15.1"))
                // implementation(npm("react-redux", "5.0.7"))
            }
        }
    }
}

val staticFilesBuildDir = "${project.buildDir.path}/static"
val staticFilesSrcDir = "$projectDir/src/main/web"

tasks {
    "compileKotlinJs"(KotlinJsCompile::class)  {
        kotlinOptions.metaInfo = true
        kotlinOptions.outputFile = "${project.buildDir.path}/js/${project.name}.js"
        kotlinOptions.sourceMap = true
        kotlinOptions.moduleKind = "commonjs"
        kotlinOptions.main = "call"
    }

    register<Copy>("copyStatic") {
        dependsOn("assemble")
        from("${project.buildDir.path}/bundle", staticFilesSrcDir)
        into(staticFilesBuildDir)

        val webpack = project.tasks.withType(KotlinWebpack::class).first()
        val bundleFile = webpack.outputPath.name
        val publicPath = "/" // TODO get public path from webpack config

        filesMatching("*.html") {
            expand("bundle" to bundleFile, "publicPath" to publicPath)
        }
    }

    build {
        dependsOn("copyStatic")
    }
}
