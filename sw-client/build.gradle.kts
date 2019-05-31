plugins {
    kotlin("multiplatform")
    id("org.jlleitschuh.gradle.ktlint")
}

val krossbowVersion = "0.3.1"

kotlin {
    jvm()
    js()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":sw-common-model"))
                implementation(kotlin("stdlib-common"))
                implementation("org.hildan.krossbow:krossbow-client-metadata:$krossbowVersion")
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
                implementation("org.hildan.krossbow:krossbow-client-jvm:$krossbowVersion")
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
                implementation("org.hildan.krossbow:krossbow-client-js:$krossbowVersion")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
