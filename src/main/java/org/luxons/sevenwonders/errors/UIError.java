package org.luxons.sevenwonders.errors;

import java.util.List;

import org.springframework.validation.ObjectError;

public class UIError {

    private final String code;

    private final String message;

    private final ErrorType type;

    private List<ObjectError> validationErrors;

    public UIError(String code, String message, ErrorType type) {
        this.code = code;
        this.message = message;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ErrorType getType() {
        return type;
    }

    public List<ObjectError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ObjectError> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
