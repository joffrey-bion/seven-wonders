package com.palantir.blueprintjs.org.luxons.sevenwonders.ui.utils

import kotlinx.html.SPAN
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.ReactElement
import react.dom.*

/**
 * Creates a ReactElement without appending it (so that is can be passed around).
 */
fun createElement(block: RBuilder.() -> ReactElement): ReactElement {
    return RDOMBuilder { SPAN(attributesMapOf("class", null), it) }
        .apply { block() }
        .create()
}
