plugins {
    val kotlinVersion = "1.3.31"
    id("kotlin-multiplatform") version kotlinVersion apply false
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion apply false
}

subprojects {
    repositories {
        jcenter()
    }
}
