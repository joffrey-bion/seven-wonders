package org.luxons.sevenwonders.ui.components.game

import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.model.resources.*
import org.luxons.sevenwonders.ui.components.*
import react.*
import react.dom.html.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.span
import web.html.*

private fun getResourceTokenName(resourceType: ResourceType) = "resources/${resourceType.toString().lowercase()}"

private fun getTokenImagePath(tokenName: String) = "/images/tokens/$tokenName.png"

enum class TokenCountPosition {
    LEFT,
    RIGHT,
    OVER,
}

fun ChildrenBuilder.goldIndicator(
    amount: Int,
    amountPosition: TokenCountPosition = TokenCountPosition.OVER,
    imgSize: Length = 3.rem,
    customCountStyle: PropertiesBuilder.() -> Unit = {},
    block: HTMLAttributes<HTMLDivElement>.() -> Unit = {},
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

fun ChildrenBuilder.resourceImage(
    resourceType: ResourceType,
    title: String = resourceType.toString(),
    size: Length?,
) {
    TokenImage {
        this.tokenName = getResourceTokenName(resourceType)
        this.title = title
        this.size = size
    }
}

fun ChildrenBuilder.tokenWithCount(
    tokenName: String,
    count: Int,
    title: String = tokenName,
    imgSize: Length? = null,
    countPosition: TokenCountPosition = TokenCountPosition.RIGHT,
    brightText: Boolean = false,
    customCountStyle: PropertiesBuilder.() -> Unit = {},
    block: HTMLAttributes<HTMLDivElement>.() -> Unit = {},
) {
    div {
        block()
        val tokenCountSize = if (imgSize != null) 0.6 * imgSize else 1.5.rem
        when (countPosition) {
            TokenCountPosition.RIGHT -> {
                TokenImage {
                    this.tokenName = tokenName
                    this.title = title
                    this.size = imgSize
                }
                span {
                    css {
                        tokenCountStyle(tokenCountSize, brightText, customCountStyle)
                        marginLeft = 0.2.rem
                    }
                    +"× $count"
                }
            }

            TokenCountPosition.LEFT -> {
                span {
                    css {
                        tokenCountStyle(tokenCountSize, brightText, customCountStyle)
                        marginRight = 0.2.rem
                    }
                    +"$count ×"
                }
                TokenImage {
                    this.tokenName = tokenName
                    this.title = title
                    this.size = imgSize
                }
            }

            TokenCountPosition.OVER -> {
                div {
                    css {
                        position = Position.relative
                        // if container becomes large, this one stays small so that children stay on top of each other
                        width = Length.fitContent
                    }
                    TokenImage {
                        this.tokenName = tokenName
                        this.title = title
                        this.size = imgSize
                    }
                    span {
                        css(GlobalStyles.centerInPositionedParent) {
                            tokenCountStyle(tokenCountSize, brightText, customCountStyle)
                        }
                        +"$count"
                    }
                }
            }
        }
    }
}

external interface TokenImageProps : Props {
    var tokenName: String
    var title: String?
    var size: Length?
}

val TokenImage = FC<TokenImageProps> { props ->
    img {
        src = getTokenImagePath(props.tokenName)
        title = props.title ?: props.tokenName
        alt = props.tokenName

        css {
            height = props.size ?: 100.pct
            if (props.size != null) {
                width = props.size
            }
            verticalAlign = VerticalAlign.middle
        }
    }
}

private fun PropertiesBuilder.tokenCountStyle(
    size: Length,
    brightText: Boolean,
    customStyle: PropertiesBuilder.() -> Unit = {},
) {
    fontFamily = string("Acme")
    fontSize = size
    verticalAlign = VerticalAlign.middle
    color = if (brightText) NamedColor.white else NamedColor.black
    customStyle()
}
