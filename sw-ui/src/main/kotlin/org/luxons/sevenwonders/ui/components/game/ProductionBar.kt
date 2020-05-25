package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.Align
import kotlinx.css.BorderStyle
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.Position
import kotlinx.css.VerticalAlign
import kotlinx.css.alignItems
import kotlinx.css.background
import kotlinx.css.bottom
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.height
import kotlinx.css.margin
import kotlinx.css.marginLeft
import kotlinx.css.position
import kotlinx.css.properties.borderTop
import kotlinx.css.properties.boxShadow
import kotlinx.css.px
import kotlinx.css.rem
import kotlinx.css.verticalAlign
import kotlinx.css.vw
import kotlinx.css.width
import kotlinx.css.zIndex
import kotlinx.html.DIV
import kotlinx.html.IMG
import kotlinx.html.title
import org.luxons.sevenwonders.model.boards.Production
import org.luxons.sevenwonders.model.resources.CountedResource
import org.luxons.sevenwonders.model.resources.ResourceType
import react.RBuilder
import react.dom.*
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv
import styled.styledImg
import styled.styledSpan

fun RBuilder.productionBar(gold: Int, production: Production) {
    styledDiv {
        css {
            productionBarStyle()
        }
        goldIndicator(gold)
        fixedResources(production.fixedResources)
        alternativeResources(production.alternativeResources)
    }
}

private fun RBuilder.goldIndicator(amount: Int) {
    tokenWithCount(tokenName = "coin", count = amount)
}

private fun RBuilder.fixedResources(resources: List<CountedResource>) {
    styledDiv {
        css {
            margin = "auto"
            display = Display.flex
        }
        resources.forEach {
            tokenWithCount(tokenName = getResourceTokenName(it.type), count = it.count) {
                attrs { key = it.type.toString() }
                css { marginLeft = 1.rem }
            }
        }
    }
}

private fun RBuilder.alternativeResources(resources: Set<Set<ResourceType>>) {
    styledDiv {
        css {
            margin = "auto"
            display = Display.flex
        }
        resources.forEachIndexed { index, res ->
            resourceChoice(types = res) {
                attrs {
                    key = index.toString()
                }
            }
        }
    }
}

private fun RBuilder.resourceChoice(types: Set<ResourceType>, block: StyledDOMBuilder<DIV>.() -> Unit = {}) {
    styledDiv {
        css {
            marginLeft = (1.5).rem
        }
        block()
        for ((i, t) in types.withIndex()) {
            tokenImage(tokenName = getResourceTokenName(t)) {
                attrs { this.key = t.toString() }
            }
            if (i < types.indices.last) {
                styledSpan {
                    css { choiceSeparatorStyle() }
                    +"/"
                }
            }
        }
    }
}

private fun RBuilder.tokenWithCount(tokenName: String, count: Int, block: StyledDOMBuilder<DIV>.() -> Unit = {}) {
    styledDiv {
        block()
        tokenImage(tokenName)
        styledSpan {
            css { tokenCountStyle() }
            + "× $count"
        }
    }
}

private fun RBuilder.tokenImage(tokenName: String, block: StyledDOMBuilder<IMG>.() -> Unit = {}) {
    styledImg(src = getTokenImagePath(tokenName)) {
        css {
            tokenImageStyle()
        }
        attrs {
            this.title = tokenName
            this.alt = tokenName
        }
        block()
    }
}

private fun getTokenImagePath(tokenName: String) = "/images/tokens/$tokenName.png"

private fun getResourceTokenName(resourceType: ResourceType) = "resources/${resourceType.toString().toLowerCase()}"

private fun CSSBuilder.productionBarStyle() {
    alignItems = Align.center
    background = "linear-gradient(#eaeaea, #888 7%)"
    bottom = 0.px
    borderTop(width = 1.px, color = Color("#8b8b8b"), style = BorderStyle.solid)
    boxShadow(blurRadius = 15.px, color = Color("#747474"))
    display = Display.flex
    height = (3.5).rem
    width = 100.vw
    position = Position.fixed
    zIndex = 99
}

private fun CSSBuilder.choiceSeparatorStyle() {
    fontSize = 2.rem
    verticalAlign = VerticalAlign.middle
    margin(all = 5.px)
    color = Color("#c29929")
    declarations["text-shadow"] = "0 0 1px black"
}

private fun CSSBuilder.tokenImageStyle() {
    height = 3.rem
    width = 3.rem
    verticalAlign = VerticalAlign.middle
}

private fun CSSBuilder.tokenCountStyle() {
    fontFamily = "fantasy"
    fontSize = 1.5.rem
    verticalAlign = VerticalAlign.middle
    marginLeft = 0.2.rem
}
