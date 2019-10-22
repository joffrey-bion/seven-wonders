plugins {
    val kotlinVersion = "1.3.50"
    kotlin("js") version kotlinVersion apply false
    kotlin("jvm") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    id("org.jlleitschuh.gradle.ktlint") version "7.1.0" apply false
}

subprojects {
    repositories {
        jcenter()
        // keep this as long as the Gradle plugin is used in EAP version
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
