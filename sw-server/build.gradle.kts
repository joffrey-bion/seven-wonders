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

    val coroutinesVersion = "1.4.2"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion") // for Spring
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-security")
    // required by spring security when using websockets
    implementation("org.springframework.security:spring-security-messaging")

    // logging
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.github.loki4j:loki-logback-appender:1.0.0")

    // monitoring / metrics
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus:1.6.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":sw-client"))
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
