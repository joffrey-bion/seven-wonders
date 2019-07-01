plugins {
    val kotlinVersion = "1.3.40"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    id("org.jlleitschuh.gradle.ktlint") version "7.1.0" apply false
}

subprojects {
    repositories {
        jcenter()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
