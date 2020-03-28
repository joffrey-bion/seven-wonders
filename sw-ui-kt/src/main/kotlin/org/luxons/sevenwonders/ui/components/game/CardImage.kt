package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.borderRadius
import kotlinx.css.pct
import kotlinx.css.properties.boxShadow
import kotlinx.css.px
import kotlinx.css.rem
import kotlinx.html.IMG
import kotlinx.html.title
import org.luxons.sevenwonders.model.cards.Card
import react.RBuilder
import styled.StyledDOMBuilder
import styled.css
import styled.styledImg

fun RBuilder.cardImage(card: Card, highlightColor: Color? = null, block: StyledDOMBuilder<IMG>.() -> Unit = {}) {
    styledImg(src = "/images/cards/${card.image}") {
        css {
            borderRadius = 5.pct
            boxShadow(offsetX = 2.px, offsetY = 2.px, blurRadius = 5.px, color = Color.black)
            highlightStyle(highlightColor)
        }
        attrs {
            title = card.name
            alt = "Card ${card.name}"
        }
        block()
    }
}

private fun CSSBuilder.highlightStyle(highlightColor: Color?) {
    if (highlightColor != null) {
        boxShadow(
            offsetX = 0.px,
            offsetY = 0.px,
            blurRadius = 1.rem,
            spreadRadius = 0.1.rem,
            color = highlightColor
        )
    }
}
