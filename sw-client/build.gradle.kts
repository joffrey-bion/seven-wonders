plugins {
    id("sw.kotlin-multiplatform")
}

kotlin {
    jvm()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.swCommonModel)
                api(libs.krossbow.stomp.kxserialization.json)
                implementation(libs.krossbow.websocket.builtin)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}
