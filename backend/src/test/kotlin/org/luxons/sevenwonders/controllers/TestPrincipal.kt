package org.luxons.sevenwonders.controllers

import java.security.Principal

internal class TestPrincipal(private val name: String) : Principal {

    override fun getName(): String {
        return name
    }
}
