plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":sw-client"))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
