package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.*
import kotlinx.html.DIV
import kotlinx.html.IMG
import kotlinx.html.title
import org.luxons.sevenwonders.ui.components.GlobalStyles
import react.RBuilder
import styled.*

enum class TokenCountPosition {
    LEFT, RIGHT, OVER
}

fun RBuilder.goldIndicator(
    amount: Int,
    amountPosition: TokenCountPosition = TokenCountPosition.OVER,
    imgSize: LinearDimension = 3.rem
) {
    tokenWithCount(
        tokenName = "coin",
        title = "$amount gold coins",
        imgSize = imgSize,
        count = amount,
        countPosition = amountPosition
    )
}

fun RBuilder.tokenWithCount(
    tokenName: String,
    count: Int,
    title: String = tokenName,
    imgSize: LinearDimension = 3.rem,
    countPosition: TokenCountPosition = TokenCountPosition.RIGHT,
    brightText: Boolean = false,
    block: StyledDOMBuilder<DIV>.() -> Unit = {}
) {
    styledDiv {
        block()
        val tokenCountSize = imgSize * 0.6
        when (countPosition) {
            TokenCountPosition.RIGHT -> {
                tokenImage(tokenName, title = title, size = imgSize)
                styledSpan {
                    css {
                        tokenCountStyle(tokenCountSize, brightText)
                        marginLeft = 0.2.rem
                    }
                    +"× $count"
                }
            }
            TokenCountPosition.LEFT -> {
                styledSpan {
                    css {
                        tokenCountStyle(tokenCountSize, brightText)
                        marginRight = 0.2.rem
                    }
                    +"$count ×"
                }
                tokenImage(tokenName, title = title, size = imgSize)
            }
            TokenCountPosition.OVER -> {
                styledDiv {
                    css { position = Position.relative }
                    tokenImage(tokenName, title = title, size = imgSize)
                    styledSpan {
                        css {
                            +GlobalStyles.centerInParent
                            tokenCountStyle(tokenCountSize, brightText)
                        }
                        +"$count"
                    }
                }
            }
        }
    }
}

fun RBuilder.tokenImage(
    tokenName: String,
    title: String = tokenName,
    size: LinearDimension = 3.rem,
    block: StyledDOMBuilder<IMG>.() -> Unit = {}
) {
    styledImg(src = getTokenImagePath(tokenName)) {
        css {
            tokenImageStyle(size)
        }
        attrs {
            this.title = title
            this.alt = tokenName
        }
        block()
    }
}

private fun getTokenImagePath(tokenName: String) = "/images/tokens/$tokenName.png"

private fun CSSBuilder.tokenImageStyle(size: LinearDimension) {
    height = size
    width = size
    verticalAlign = VerticalAlign.middle
}

private fun CSSBuilder.tokenCountStyle(size: LinearDimension, brightText: Boolean) {
    fontFamily = "fantasy"
    fontSize = size
    verticalAlign = VerticalAlign.middle
    if (brightText) {
        color = Color.white
    }
    fontWeight = FontWeight.bold
}
