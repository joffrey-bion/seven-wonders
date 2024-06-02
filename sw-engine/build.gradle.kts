plugins {
    id("sw.kotlin-jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(projects.swCommonModel)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(kotlin("test"))
}
