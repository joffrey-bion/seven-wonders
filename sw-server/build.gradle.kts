plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.serialization")
    id("org.springframework.boot") version "2.4.0"
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(projects.swCommonModel)
    implementation(projects.swEngine)
    implementation(projects.swBot)
    implementation(kotlin("reflect")) // required by Spring 5

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor) // for Spring
    implementation(libs.kotlinx.serialization.json)

    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-security")
    // required by spring security when using websockets
    implementation("org.springframework.security:spring-security-messaging")

    // logging
    implementation(libs.logback.classic)
    implementation(libs.loki.logback.appender)

    // monitoring / metrics
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(libs.micrometer.registry.prometheus)

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation(projects.swClient)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.processResources {
    // make sure we build the frontend before creating the jar
    dependsOn(":sw-ui:assemble")
    // package the frontend app within the jar as static
    val frontendBuildDir = project(":sw-ui").buildDir
    val frontendDist = frontendBuildDir.toPath().resolve("distributions")
    from(frontendDist) {
        include("**/*")
        into("static")
    }
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
    }
}
