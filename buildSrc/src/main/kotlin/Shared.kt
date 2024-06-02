import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.*

/**
 * The major JDK version shared between all toolchain settings.
 */
const val JdkVersion = 17

/**
 * The compiler options shared by all Kotlin compilations in the project.
 */
fun KotlinCommonCompilerOptions.setCommonCompilerOptions() {
    progressiveMode = true
    allWarningsAsErrors = true
}
