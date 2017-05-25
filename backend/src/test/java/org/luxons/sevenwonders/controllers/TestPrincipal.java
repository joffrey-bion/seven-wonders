package org.luxons.sevenwonders.controllers;

import java.security.Principal;

public class TestPrincipal implements Principal {

    private final String name;

    TestPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
