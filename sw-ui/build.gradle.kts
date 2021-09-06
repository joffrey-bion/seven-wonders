import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("js")
}

val kotlinWrappersVersion = libs.versions.kotlin.wrappers.get()

kotlin {
    js {
        browser()
        useCommonJs()
    }
    sourceSets {
        main {
            dependencies {
                implementation(projects.swClient)

                val reactVersion = libs.versions.react.get()
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$reactVersion-$kotlinWrappersVersion")
                implementation(npm("react", reactVersion))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactVersion-$kotlinWrappersVersion")
                implementation(npm("react-dom", reactVersion))

                val reactReduxVersion = libs.versions.reactRedux.get()
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux:$reactReduxVersion-$kotlinWrappersVersion")
                implementation(npm("react-redux", reactReduxVersion))
                implementation(npm("redux", libs.versions.redux.get()))

                val reactRouterDomVersion = libs.versions.reactRouterDom.get()
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:$reactRouterDomVersion-$kotlinWrappersVersion")
                implementation(npm("react-router-dom", reactRouterDomVersion))

                val styledComponentsVersion = libs.versions.styledComponents.get()
                implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:$styledComponentsVersion-$kotlinWrappersVersion")
                implementation(npm("styled-components", styledComponentsVersion))
                implementation(npm("inline-style-prefixer", "6.0.0")) // FIXME is this still needed

                val bpWrapperVersion = libs.versions.blueprintjs.wrapper.get()
                val bpCoreVersion = libs.versions.blueprintjs.core.get()
                val bpIconsVersion = libs.versions.blueprintjs.icons.get()
                implementation("org.hildan.blueprintjs:kotlin-blueprintjs-core:$bpCoreVersion-$bpWrapperVersion")
                implementation("org.hildan.blueprintjs:kotlin-blueprintjs-icons:$bpIconsVersion-$bpWrapperVersion")
                implementation(npm("@blueprintjs/core", bpCoreVersion))
                implementation(npm("@blueprintjs/icons", bpIconsVersion))
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
