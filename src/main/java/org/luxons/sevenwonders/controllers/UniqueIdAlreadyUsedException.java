package org.luxons.sevenwonders.controllers;

public class UniqueIdAlreadyUsedException extends RuntimeException {

    private String id;

    public UniqueIdAlreadyUsedException(String id) {
        super("'" + id + "'");
        this.id = id;
    }

    public String getUsedId() {
        return id;
    }
}
