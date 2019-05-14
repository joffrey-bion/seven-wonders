plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.31"
    id("org.jlleitschuh.gradle.ktlint") version "7.1.0"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
