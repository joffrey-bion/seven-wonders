package com.palantir.blueprintjs.org.luxons.sevenwonders.ui.utils

import kotlinx.html.SPAN
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.ReactElement
import react.dom.*

fun createElement(block: RBuilder.() -> ReactElement): ReactElement {
    return RDOMBuilder { SPAN(attributesMapOf("class", null), it) }
        .apply { block() }
        .create()
}
