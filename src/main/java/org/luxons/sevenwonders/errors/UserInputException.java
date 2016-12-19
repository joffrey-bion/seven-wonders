package org.luxons.sevenwonders.errors;

public class UserInputException extends RuntimeException {

    private final String messageResourceKey;

    public UserInputException(String messageResourceKey) {
        this.messageResourceKey = messageResourceKey;
    }

    public String getMessageResourceKey() {
        return messageResourceKey;
    }
}
