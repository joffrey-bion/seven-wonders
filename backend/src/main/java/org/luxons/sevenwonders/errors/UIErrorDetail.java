package org.luxons.sevenwonders.errors;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

class UIErrorDetail {

    private final Object rejectedValue;

    private final String path;

    private final String message;

    UIErrorDetail(FieldError error) {
        rejectedValue = error.getRejectedValue();
        path = error.getObjectName() + '.' + error.getField();
        message = "Invalid value for field '" + error.getField() + "': " + error.getDefaultMessage();
    }

    UIErrorDetail(ObjectError error) {
        rejectedValue = null;
        path = error.getObjectName();
        message = "Invalid value for object '" + error.getObjectName() + "': " + error.getDefaultMessage();
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }
}
