package org.luxons.sevenwonders.errors;

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
