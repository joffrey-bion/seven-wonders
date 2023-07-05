package org.luxons.sevenwonders.ui.components.game

import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.model.cards.*
import react.*
import react.dom.html.ReactHTML.img
import web.cssom.*
import web.cssom.Color

external interface CardImageProps : PropsWithClassName {
    var card: Card
    var faceDown: Boolean?
    var highlightColor: Color?
}

val CardImage = FC<CardImageProps>("CardImage") { props ->
    if (props.faceDown == true) {
        CardBackImage {
            cardBack = props.card.back
            highlightColor = props.highlightColor
        }
    } else {
        img {
            src = "/images/cards/${props.card.image}"
            title = props.card.name
            alt = "Card ${props.card.name}"

            css(props.className) {
                cardImageStyle(props.highlightColor)
            }
        }
    }
}

external interface CardBackImageProps : PropsWithClassName {
    var cardBack: CardBack
    var highlightColor: Color?
}

val CardBackImage = FC<CardBackImageProps>("CardBackImage") { props ->
    img {
        src = "/images/cards/back/${props.cardBack.image}"
        alt = "Card back (${props.cardBack.image})"
        css(props.className) {
            cardImageStyle(props.highlightColor)
        }
    }
}

val CardPlaceholderImage = FC<PropsWithClassName>("CardPlaceholderImage") { props ->
    img {
        src = "/images/cards/back/placeholder.png"
        alt = "Card placeholder"
        css(props.className) {
            opacity = number(0.20)
            borderRadius = 5.pct
        }
    }
}

private fun PropertiesBuilder.cardImageStyle(highlightColor: Color?) {
    borderRadius = 5.pct
    boxShadow = BoxShadow(offsetX = 2.px, offsetY = 2.px, blurRadius = 5.px, color = NamedColor.black)
    highlightStyle(highlightColor)
}

internal fun PropertiesBuilder.highlightStyle(highlightColor: Color?) {
    if (highlightColor != null) {
        boxShadow = BoxShadow(
            offsetX = 0.px,
            offsetY = 0.px,
            blurRadius = 1.rem,
            spreadRadius = 0.1.rem,
            color = highlightColor,
        )
    }
}
