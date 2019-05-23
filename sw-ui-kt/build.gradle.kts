import org.jetbrains.kotlin.gradle.frontend.util.frontendExtension
import org.jetbrains.kotlin.gradle.frontend.webpack.WebPackExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    id("kotlin2js")
    id("org.jetbrains.kotlin.frontend") //version "0.0.45"
}

repositories {
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlin-js-wrappers")
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":sw-common-model"))

    implementation("org.jetbrains:kotlin-react-dom:16.6.0-pre.67-kotlin-1.3.11")
}

val staticFilesBuildDir = "${project.buildDir.path}/static"
val staticFilesSrcDir = "$projectDir/src/main/web"

tasks {
    "compileKotlin2Js"(Kotlin2JsCompile::class)  {
        kotlinOptions.metaInfo = true
        kotlinOptions.outputFile = "${project.buildDir.path}/js/${project.name}.js"
        kotlinOptions.sourceMap = true
        kotlinOptions.moduleKind = "commonjs"
        kotlinOptions.main = "call"
    }

    register<Copy>("copyStatic") {
        dependsOn("bundle")
        from("${project.buildDir.path}/bundle", staticFilesSrcDir)
        into(staticFilesBuildDir)

        val webpack = project.frontendExtension.bundles().first { it is WebPackExtension } as WebPackExtension
        val bundleName = webpack.bundleName
        val publicPath = webpack.publicPath
        filesMatching("*.html") {
            expand("bundle" to "$bundleName.bundle.js", "publicPath" to publicPath)
        }
    }

    build {
        dependsOn("copyStatic")
    }
}

kotlinFrontend {

    sourceMaps = true
    
    webpack {
        bundleName = "seven-wonders-ui"
        contentPath = file(staticFilesBuildDir)
    }
    
    npm {
//        dependency("@blueprintjs/core", "3.15.1")
        dependency("react", "16.8.3")
        dependency("react-dom", "16.8.3")
        dependency("react-redux", "5.0.7")
    }
}

fun org.jetbrains.kotlin.gradle.frontend.KotlinFrontendExtension.webpack(
    configure: org.jetbrains.kotlin.gradle.frontend.webpack.WebPackExtension.() -> Unit
) {
    bundle("webpack", delegateClosureOf(configure))
}
