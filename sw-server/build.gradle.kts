plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "2.3.3.RELEASE"
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":sw-common-model"))
    implementation(project(":sw-engine"))
    implementation(project(":sw-bot"))
    implementation(kotlin("reflect")) // required by Spring 5
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-security")
    // required by spring security when using websockets
    implementation("org.springframework.security:spring-security-messaging")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("ch.qos.logback:logback-classic:1.1.8")
    implementation("org.hildan.livedoc:livedoc-springboot:4.3.2")
    implementation("org.hildan.livedoc:livedoc-ui-webjar:4.3.2")

    annotationProcessor("org.hildan.livedoc:livedoc-javadoc-processor:4.3.2")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":sw-client"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.hildan.jackstomp:jackstomp:2.0.0")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
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
