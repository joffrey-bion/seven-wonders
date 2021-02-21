package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.*
import kotlinx.html.DIV
import kotlinx.html.IMG
import kotlinx.html.title
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.ui.components.GlobalStyles
import react.RBuilder
import styled.*

private fun getResourceTokenName(resourceType: ResourceType) = "resources/${resourceType.toString().toLowerCase()}"

private fun getTokenImagePath(tokenName: String) = "/images/tokens/$tokenName.png"

enum class TokenCountPosition {
    LEFT,
    RIGHT,
    OVER,
}

fun RBuilder.goldIndicator(
    amount: Int,
    amountPosition: TokenCountPosition = TokenCountPosition.OVER,
    imgSize: LinearDimension = 3.rem,
    customCountStyle: CSSBuilder.() -> Unit = {},
    block: StyledDOMBuilder<DIV>.() -> Unit = {},
) {
    tokenWithCount(
        tokenName = "coin",
        title = "$amount gold coins",
        imgSize = imgSize,
        count = amount,
        countPosition = amountPosition,
        customCountStyle = customCountStyle,
        block = block,
    )
}

fun RBuilder.resourceWithCount(
    resourceType: ResourceType,
    count: Int,
    title: String = resourceType.toString(),
    imgSize: LinearDimension? = null,
    countPosition: TokenCountPosition = TokenCountPosition.RIGHT,
    brightText: Boolean = false,
    customCountStyle: CSSBuilder.() -> Unit = {},
    block: StyledDOMBuilder<DIV>.() -> Unit = {},
) {
    tokenWithCount(
        tokenName = getResourceTokenName(resourceType),
        count = count,
        title = title,
        imgSize = imgSize,
        countPosition = countPosition,
        brightText = brightText,
        customCountStyle = customCountStyle,
        block = block
    )
}

fun RBuilder.resourceImage(
    resourceType: ResourceType,
    title: String = resourceType.toString(),
    size: LinearDimension?,
    block: StyledDOMBuilder<IMG>.() -> Unit = {},
) {
    tokenImage(getResourceTokenName(resourceType), title, size, block)
}

fun RBuilder.tokenWithCount(
    tokenName: String,
    count: Int,
    title: String = tokenName,
    imgSize: LinearDimension? = null,
    countPosition: TokenCountPosition = TokenCountPosition.RIGHT,
    brightText: Boolean = false,
    customCountStyle: CSSBuilder.() -> Unit = {},
    block: StyledDOMBuilder<DIV>.() -> Unit = {},
) {
    styledDiv {
        block()
        val tokenCountSize = if (imgSize != null) imgSize * 0.6 else 1.5.rem
        when (countPosition) {
            TokenCountPosition.RIGHT -> {
                tokenImage(tokenName, title = title, size = imgSize)
                styledSpan {
                    css {
                        tokenCountStyle(tokenCountSize, brightText, customCountStyle)
                        marginLeft = 0.2.rem
                    }
                    +"× $count"
                }
            }
            TokenCountPosition.LEFT -> {
                styledSpan {
                    css {
                        tokenCountStyle(tokenCountSize, brightText, customCountStyle)
                        marginRight = 0.2.rem
                    }
                    +"$count ×"
                }
                tokenImage(tokenName, title = title, size = imgSize)
            }
            TokenCountPosition.OVER -> {
                styledDiv {
                    css {
                        position = Position.relative
                        // if container becomes large, this one stays small so that children stay on top of each other
                        width = LinearDimension.fitContent
                    }
                    tokenImage(tokenName, title = title, size = imgSize)
                    styledSpan {
                        css {
                            +GlobalStyles.centerInPositionedParent
                            tokenCountStyle(tokenCountSize, brightText, customCountStyle)
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
    size: LinearDimension?,
    block: StyledDOMBuilder<IMG>.() -> Unit = {},
) {
    styledImg(src = getTokenImagePath(tokenName)) {
        css {
            height = size ?: 100.pct
            if (size != null) {
                width = size
            }
            verticalAlign = VerticalAlign.middle
        }
        attrs {
            this.title = title
            this.alt = tokenName
        }
        block()
    }
}

private fun CSSBuilder.tokenCountStyle(
    size: LinearDimension,
    brightText: Boolean,
    customStyle: CSSBuilder.() -> Unit = {},
) {
    fontFamily = "Acme"
    fontSize = size
    verticalAlign = VerticalAlign.middle
    color = if (brightText) Color.white else Color.black
    customStyle()
}
