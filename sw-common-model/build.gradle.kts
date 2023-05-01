plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    js {
        browser() // necessary for local dependency from JS UI module
    }
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
