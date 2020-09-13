plugins {
    kotlin("multiplatform")
}

val krossbowVersion = "0.41.0"

kotlin {
    jvm()
    js {
        browser() // necessary for local dependency from JS UI module
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":sw-common-model"))
                api("org.hildan.krossbow:krossbow-stomp-kxserialization:$krossbowVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("text-encoding", "0.7.0")) // required by krossbow, because required by kotlinx-io
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
