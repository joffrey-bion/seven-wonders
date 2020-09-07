package org.luxons.sevenwonders.server

import org.luxons.sevenwonders.model.api.errors.ValidationErrorDTO
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError

fun ObjectError.toDTO() = (this as? FieldError)?.fieldError() ?: objectError()

fun FieldError.fieldError(): ValidationErrorDTO = ValidationErrorDTO(
    path = "$objectName.$field",
    message = "Invalid value for field '$field': $defaultMessage",
    rejectedValue = rejectedValue?.toString(),
)

fun ObjectError.objectError(): ValidationErrorDTO =
    ValidationErrorDTO(objectName, "Invalid value for object '$objectName': $defaultMessage")
