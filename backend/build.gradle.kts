plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.21"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.21"
    id("org.springframework.boot") version "2.1.3.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "7.1.0"
}

apply(plugin = "io.spring.dependency-management")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    compile(project(":game-engine"))
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("org.jetbrains.kotlin:kotlin-reflect") // required by Spring 5

    compile("org.springframework.boot:spring-boot-starter-websocket")
    compile("org.springframework.boot:spring-boot-starter-security")
    // required by spring security when using websockets
    compile("org.springframework.security:spring-security-messaging")

    compile("com.fasterxml.jackson.module:jackson-module-kotlin")

    compile("ch.qos.logback:logback-classic:1.1.8")
    compile("org.hildan.livedoc:livedoc-springboot:4.3.2")
    compile("org.hildan.livedoc:livedoc-ui-webjar:4.3.2")

    annotationProcessor("org.hildan.livedoc:livedoc-javadoc-processor:4.3.2")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.hildan.jackstomp:jackstomp:2.0.0")
}

// packages the frontend app within the jar
tasks.jar {
    from("../frontend/build") {
        into("static")
    }
}

// make sure we build the frontend before creating the jar
tasks.jar.get().dependsOn(":frontend:assemble")
