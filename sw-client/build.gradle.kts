plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js {
        browser() // necessary for local dependency from JS UI module
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.swCommonModel)
                api(libs.krossbow.stomp.kxserialization.json)
                api(libs.krossbow.websocket.builtin)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}
