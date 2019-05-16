plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

dependencies {
    implementation(project(":sw-common-model"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
