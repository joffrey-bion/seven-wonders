package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.borderRadius
import kotlinx.css.pct
import kotlinx.css.properties.*
import kotlinx.css.px
import kotlinx.css.rem
import kotlinx.html.IMG
import kotlinx.html.title
import org.luxons.sevenwonders.model.cards.Card
import react.RBuilder
import styled.StyledDOMBuilder
import styled.css
import styled.styledImg

fun RBuilder.cardImage(
    card: Card,
    faceDown: Boolean = false,
    highlightColor: Color? = null,
    block: StyledDOMBuilder<IMG>.() -> Unit = {}
) {
    styledImg(src = card.imageSrc(faceDown)) {
        css {
            cardImageStyle(highlightColor)
        }
        attrs {
            title = card.name
            alt = if (faceDown) "Card back (${card.back.image})" else "Card ${card.name}"
        }
        block()
    }
}

private fun Card.imageSrc(faceDown: Boolean): String = if (faceDown) {
    "/images/cards/back/${back.image}"
} else {
    "/images/cards/$image"
}

private fun CSSBuilder.cardImageStyle(highlightColor: Color?) {
    borderRadius = 5.pct
    boxShadow(offsetX = 2.px, offsetY = 2.px, blurRadius = 5.px, color = Color.black)
    highlightStyle(highlightColor)
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
