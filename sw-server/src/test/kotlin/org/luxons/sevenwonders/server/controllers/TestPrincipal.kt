package org.luxons.sevenwonders.server.controllers

import java.security.Principal

internal class TestPrincipal(private val name: String) : Principal {

    override fun getName(): String = name
}
