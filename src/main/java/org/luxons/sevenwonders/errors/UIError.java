package org.luxons.sevenwonders.errors;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class UIError {

    private final String code;

    private final String message;

    private final ErrorType type;

    private List<UIErrorDetail> details = new ArrayList<>();

    UIError(String code, String message, ErrorType type) {
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

    public List<UIErrorDetail> getDetails() {
        return details;
    }

    void addDetails(List<ObjectError> objectErrors) {
        for (ObjectError objectError : objectErrors) {
            this.details.add(convertError(objectError));
        }
    }

    private UIErrorDetail convertError(ObjectError objectError) {
        if (objectError instanceof FieldError) {
            return new UIErrorDetail((FieldError)objectError);
        } else {
            return new UIErrorDetail(objectError);
        }
    }
}
