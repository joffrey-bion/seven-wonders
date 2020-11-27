package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.*
import kotlinx.css.properties.borderTop
import kotlinx.css.properties.boxShadow
import kotlinx.html.DIV
import org.luxons.sevenwonders.model.boards.Production
import org.luxons.sevenwonders.model.resources.CountedResource
import org.luxons.sevenwonders.model.resources.ResourceType
import react.RBuilder
import react.dom.key
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv
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

private fun RBuilder.fixedResources(resources: List<CountedResource>) {
    styledDiv {
        css {
            margin = "auto"
            display = Display.flex
        }
        resources.forEach {
            resourceWithCount(resourceType = it.type, count = it.count, imgSize = 3.rem) {
                attrs { key = it.type.toString() }
                css { marginLeft = 1.rem }
            }
        }
    }
}

private fun RBuilder.alternativeResources(resources: List<Set<ResourceType>>) {
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
            resourceImage(resourceType = t, size = 3.rem) {
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
