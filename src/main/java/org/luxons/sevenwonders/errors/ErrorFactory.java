package org.luxons.sevenwonders.errors;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

@Component
public class ErrorFactory {

    private static final String ERROR_CODE_VALIDATION = "VALIDATION_ERROR";

    private static final String ERROR_MSG_VALIDATION = "Input invalid";

    private final MessageSource messageSource;

    @Autowired
    public ErrorFactory(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public UIError createError(Throwable exception) {
        if (exception instanceof UserInputException) {
            return createUserError((UserInputException)exception);
        } else if (exception instanceof MethodArgumentNotValidException) {
            return createValidationError((MethodArgumentNotValidException)exception);
        } else {
            return createInternalError(exception);
        }
    }

    private UIError createUserError(UserInputException exception) {
        String messageKey = exception.getMessageResourceKey();
        String message = messageSource.getMessage(messageKey, null, messageKey, Locale.US);
        return new UIError(messageKey, message, ErrorType.USER);
    }

    private UIError createInternalError(Throwable exception) {
        return new UIError(exception.getClass().getSimpleName(), exception.getMessage(), ErrorType.INTERNAL);
    }

    private UIError createValidationError(MethodArgumentNotValidException exception) {
        List<ObjectError> errors = exception.getBindingResult().getAllErrors();
        UIError uiError = new UIError(ERROR_CODE_VALIDATION, ERROR_MSG_VALIDATION, ErrorType.VALIDATION);
        uiError.setValidationErrors(errors);
        return uiError;
    }
}
