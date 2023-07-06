import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    val kotlinVersion = "1.9.0"
    kotlin("js") version kotlinVersion apply false
    kotlin("jvm") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { // JVM only
        kotlinOptions.jvmTarget = "17"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>> {
        kotlinOptions.allWarningsAsErrors = true
    }

    tasks.withType<AbstractTestTask> {
        testLogging {
            events(TestLogEvent.FAILED, TestLogEvent.STANDARD_ERROR)
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showStackTraces = true
        }
    }
}
