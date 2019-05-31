package org.luxons.sevenwonders.model.api.errors

enum class ErrorType {
    VALIDATION, CLIENT, SERVER
}

data class ErrorDTO(
    val code: String,
    val message: String,
    val type: ErrorType,
    val details: List<ValidationErrorDTO> = emptyList()
)

data class ValidationErrorDTO(
    val path: String,
    val message: String,
    val rejectedValue: Any? = null
)
