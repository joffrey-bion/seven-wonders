package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.properties.boxShadow
import kotlinx.css.px
import kotlinx.css.rem
import kotlinx.html.IMG
import kotlinx.html.title
import org.luxons.sevenwonders.model.cards.TableCard
import react.RBuilder
import styled.StyledDOMBuilder
import styled.css
import styled.styledImg

fun RBuilder.cardImage(card: TableCard, highlightColor: Color? = null, block: StyledDOMBuilder<IMG>.() -> Unit = {}) {
    styledImg(src = "/images/cards/${card.image}") {
        css {
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
