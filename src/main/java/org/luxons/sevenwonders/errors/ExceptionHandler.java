package org.luxons.sevenwonders.errors;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    private static final String ERROR_CODE_VALIDATION = "VALIDATION_ERROR";

    private static final String ERROR_MSG_VALIDATION = "Input invalid";

    private final MessageSource messageSource;

    @Autowired
    public ExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    private UIError handleValidationError(MethodArgumentNotValidException exception) {
        logger.error("Invalid input", exception);
        List<ObjectError> errors = exception.getBindingResult().getAllErrors();
        UIError uiError = new UIError(ERROR_CODE_VALIDATION, ERROR_MSG_VALIDATION, ErrorType.VALIDATION);
        uiError.setValidationErrors(errors);
        return uiError;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    private UIError handleGenericUserError(UserInputException exception) {
        logger.error("Incorrect user input: " + exception.getMessage());
        String messageKey = exception.getMessageResourceKey();
        String message = messageSource.getMessage(messageKey, exception.getParams(), messageKey, Locale.US);
        return new UIError(messageKey, message, ErrorType.USER);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    private UIError handleApiError(ApiMisuseException exception) {
        logger.error("Invalid API input", exception);
        return new UIError(exception.getClass().getSimpleName(), exception.getMessage(), ErrorType.INTERNAL);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    private UIError handleUnexpectedInternalError(Throwable exception) {
        logger.error("Uncaught exception thrown during message handling", exception);
        return new UIError(exception.getClass().getSimpleName(), exception.getMessage(), ErrorType.INTERNAL);
    }
}
