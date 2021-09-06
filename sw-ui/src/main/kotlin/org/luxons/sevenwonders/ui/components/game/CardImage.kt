package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.IMG
import kotlinx.html.title
import org.luxons.sevenwonders.model.cards.Card
import org.luxons.sevenwonders.model.cards.CardBack
import react.RBuilder
import react.dom.attrs
import styled.StyledDOMBuilder
import styled.css
import styled.styledImg

fun RBuilder.cardImage(
    card: Card,
    faceDown: Boolean = false,
    highlightColor: Color? = null,
    block: StyledDOMBuilder<IMG>.() -> Unit = {},
) {
    if (faceDown) {
        cardBackImage(card.back, highlightColor, block)
        return
    }
    styledImg(src = "/images/cards/${card.image}") {
        css {
            cardImageStyle(highlightColor)
        }
        attrs {
            title = card.name
            alt = "Card ${card.name}"
        }
        block()
    }
}

fun RBuilder.cardBackImage(
    cardBack: CardBack,
    highlightColor: Color? = null,
    block: StyledDOMBuilder<IMG>.() -> Unit = {},
) {
    styledImg(src = "/images/cards/back/${cardBack.image}") {
        css {
            cardImageStyle(highlightColor)
        }
        attrs {
            alt = "Card back (${cardBack.image})"
        }
        block()
    }
}

fun RBuilder.cardPlaceholderImage(block: StyledDOMBuilder<IMG>.() -> Unit = {}) {
    styledImg(src = "/images/cards/back/placeholder.png") {
        css {
            opacity = 0.20
            borderRadius = 5.pct
        }
        attrs {
            alt = "Card placeholder"
        }
        block()
    }
}

private fun CssBuilder.cardImageStyle(highlightColor: Color?) {
    borderRadius = 5.pct
    boxShadow(offsetX = 2.px, offsetY = 2.px, blurRadius = 5.px, color = Color.black)
    highlightStyle(highlightColor)
}

internal fun CssBuilder.highlightStyle(highlightColor: Color?) {
    if (highlightColor != null) {
        boxShadow(
            offsetX = 0.px,
            offsetY = 0.px,
            blurRadius = 1.rem,
            spreadRadius = 0.1.rem,
            color = highlightColor,
        )
    }
}
