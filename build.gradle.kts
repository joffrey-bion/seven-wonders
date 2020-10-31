import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    val kotlinVersion = "1.4.10"
    kotlin("js") version kotlinVersion apply false
    kotlin("jvm") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

allprojects {
    repositories {
        jcenter()
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        version.set("0.39.0")
        disabledRules.set(setOf("no-wildcard-imports"))
    }

    val compilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += compilerArgs
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile> {
        kotlinOptions.freeCompilerArgs = compilerArgs
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon> {
        kotlinOptions.freeCompilerArgs = compilerArgs
    }

    tasks.withType<AbstractTestTask> {
        testLogging {
            events(TestLogEvent.FAILED, TestLogEvent.STANDARD_ERROR)
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showStackTraces = true
        }
    }
}
