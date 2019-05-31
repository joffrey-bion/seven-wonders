package org.luxons.sevenwonders.server

import org.luxons.sevenwonders.model.api.errors.ErrorDTO
import org.luxons.sevenwonders.model.api.errors.ErrorType
import org.slf4j.LoggerFactory
import org.springframework.messaging.converter.MessageConversionException
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.web.bind.annotation.ControllerAdvice

open class ApiMisuseException(message: String) : RuntimeException(message)

@ControllerAdvice
@SendToUser("/queue/errors")
class ExceptionHandler {

    @MessageExceptionHandler
    fun handleValidationError(exception: MethodArgumentNotValidException): ErrorDTO {
        logger.error("Invalid input", exception)
        val validationErrors = exception.bindingResult?.allErrors?.map { it.toDTO() } ?: emptyList()
        return ErrorDTO("INVALID_DATA", "Invalid input data", ErrorType.VALIDATION, validationErrors)
    }

    @MessageExceptionHandler
    fun handleConversionError(exception: MessageConversionException): ErrorDTO {
        logger.error("Error interpreting the message", exception)
        return ErrorDTO("INVALID_MESSAGE_FORMAT", "Invalid input format", ErrorType.VALIDATION)
    }

    @MessageExceptionHandler
    fun handleApiError(exception: ApiMisuseException): ErrorDTO {
        logger.error("Invalid API input", exception)
        return ErrorDTO(exception.javaClass.simpleName, exception.message!!, ErrorType.CLIENT)
    }

    @MessageExceptionHandler
    fun handleUnexpectedInternalError(exception: Throwable): ErrorDTO {
        logger.error("Uncaught exception thrown during message handling", exception)
        return ErrorDTO(exception.javaClass.simpleName, exception.localizedMessage ?: "", ErrorType.SERVER)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExceptionHandler::class.java)
    }
}
