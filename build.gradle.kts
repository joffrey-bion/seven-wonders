plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.spring) apply false
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
}
