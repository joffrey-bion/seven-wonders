plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("org.jlleitschuh.gradle.ktlint")
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":sw-common-model"))
    implementation(project(":sw-engine"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect")) // required by Spring 5

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
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
}

// packages the frontend app within the jar
tasks.bootJar {
    from("../sw-ui/build/processedResources/Js/main") {
        into("static")
    }
}

// make sure we build the frontend before creating the jar
tasks.bootJar.get().dependsOn(":sw-ui:assemble")
