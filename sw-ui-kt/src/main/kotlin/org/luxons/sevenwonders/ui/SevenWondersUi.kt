package org.luxons.sevenwonders.ui

import org.luxons.sevenwonders.ui.redux.configureStore
import org.w3c.dom.Element
import react.RBuilder
import react.dom.*
import react.redux.provider
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    window.onload = {
        val rootElement = document.getElementById("root")
        if (rootElement != null) {
            initializeAndRender(rootElement)
        } else {
            console.error("Element with ID 'root' was not found, cannot bootstrap react app")
        }
    }
}

private fun initializeAndRender(rootElement: Element) {
    val store = configureStore()
    render(rootElement) {
        provider(store) {
            app()
        }
    }
}

fun RBuilder.app() = div {
    h1 {
        +"Seven Wonders"
    }
    p {
        +"Great app!"
    }
}
