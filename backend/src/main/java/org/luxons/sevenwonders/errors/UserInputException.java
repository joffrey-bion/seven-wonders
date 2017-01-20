package org.luxons.sevenwonders.errors;

public class UserInputException extends RuntimeException {

    private final String messageResourceKey;

    private final Object[] params;

    public UserInputException(String messageResourceKey, Object... params) {
        this.messageResourceKey = messageResourceKey;
        this.params = params;
    }

    String getMessageResourceKey() {
        return messageResourceKey;
    }

    Object[] getParams() {
        return params;
    }
}
