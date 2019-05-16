package org.luxons.sevenwonders.errors

import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError

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

fun ObjectError.toDTO() = (this as? FieldError)?.fieldError() ?: objectError()

fun FieldError.fieldError(): ValidationErrorDTO =
    ValidationErrorDTO("$objectName.$field", "Invalid value for field '$field': $defaultMessage", rejectedValue)

fun ObjectError.objectError(): ValidationErrorDTO =
    ValidationErrorDTO(objectName, "Invalid value for object '$objectName': $defaultMessage")
