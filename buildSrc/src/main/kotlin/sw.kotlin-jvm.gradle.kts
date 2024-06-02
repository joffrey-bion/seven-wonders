plugins {
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(JdkVersion))
    }
}

kotlin {
    jvmToolchain(JdkVersion)

    compilerOptions {
        setCommonCompilerOptions()
    }
}
