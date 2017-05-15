package org.luxons.sevenwonders.controllers.test;

import java.security.Principal;

public class TestUtils {

    public static Principal createPrincipal(String username) {
        return new Principal() {
            @Override
            public String getName() {
                return username;
            }
        };
    }
}
