plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":sw-client"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
