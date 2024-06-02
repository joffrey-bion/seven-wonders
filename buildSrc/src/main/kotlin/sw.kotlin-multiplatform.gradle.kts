import org.jetbrains.kotlin.gradle.*

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(JdkVersion)

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        setCommonCompilerOptions()
    }
}
