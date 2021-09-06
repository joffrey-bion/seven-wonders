plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.swClient)
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.slf4j.api)

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
