package webpack

external val process: Process

external interface Process {
    val env: dynamic
}

fun isProdEnv(): Boolean = process.env.NODE_ENV == "production"
