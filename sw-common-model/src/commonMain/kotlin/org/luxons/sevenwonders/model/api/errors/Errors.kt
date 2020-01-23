package org.luxons.sevenwonders.model.api.errors

import kotlinx.serialization.Serializable

enum class ErrorType {
    VALIDATION, CLIENT, SERVER
}

@Serializable
data class ErrorDTO(
    val code: String,
    val message: String,
    val type: ErrorType,
    val details: List<ValidationErrorDTO> = emptyList()
)

@Serializable
data class ValidationErrorDTO(
    val path: String,
    val message: String,
    val rejectedValue: String? = null
)
