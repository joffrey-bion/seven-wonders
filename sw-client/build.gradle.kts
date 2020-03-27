plugins {
    kotlin("multiplatform")
    id("org.jlleitschuh.gradle.ktlint")
}

val krossbowVersion = "0.10.3"

kotlin {
    jvm()
    js {
        browser() // necessary for local dependency from JS UI module
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":sw-common-model"))
                implementation(kotlin("stdlib-common"))
                api("org.hildan.krossbow:krossbow-stomp-kxserialization-metadata:$krossbowVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                api("org.hildan.krossbow:krossbow-stomp-kxserialization-jvm:$krossbowVersion")
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
                implementation(kotlin("stdlib-js"))
                api("org.hildan.krossbow:krossbow-stomp-kxserialization-js:$krossbowVersion")
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
