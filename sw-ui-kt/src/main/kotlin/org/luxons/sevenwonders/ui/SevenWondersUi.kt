package org.luxons.sevenwonders.ui

import react.RBuilder
import react.dom.*
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    window.onload = {
        render(document.getElementById("root")!!) {
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
