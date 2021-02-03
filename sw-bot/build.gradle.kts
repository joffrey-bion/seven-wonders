plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":sw-client"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")

    implementation("org.slf4j:slf4j-api:1.7.30")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
