plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.swClient)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.slf4j.api)
}
