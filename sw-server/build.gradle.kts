plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.serialization")
    id("org.springframework.boot") version "2.4.0"
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":sw-common-model"))
    implementation(project(":sw-engine"))
    implementation(project(":sw-bot"))
    implementation(kotlin("reflect")) // required by Spring 5
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-security")
    // required by spring security when using websockets
    implementation("org.springframework.security:spring-security-messaging")

    implementation("ch.qos.logback:logback-classic:1.1.8")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":sw-client"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.processResources {
    // package the frontend app within the jar as static
    val frontendBuildDir = project(":sw-ui").buildDir
    val frontendDist = frontendBuildDir.toPath().resolve("distributions")
    from(frontendDist) {
        include("**/*")
        into("static")
    }
}
// make sure we build the frontend before creating the jar
tasks.processResources.get().dependsOn(":sw-ui:assemble")
