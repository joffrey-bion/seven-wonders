package org.luxons.sevenwonders.ui.components.lobby

import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.DIV
import org.luxons.sevenwonders.ui.components.GlobalStyles
import react.RBuilder
import react.ReactElement
import react.dom.*
import styled.*

typealias ElementBuilder = RBuilder.() -> ReactElement

fun RBuilder.radialList(
    itemBuilders: List<ElementBuilder>,
    centerElementBuilder: ElementBuilder,
    itemWidth: Int,
    itemHeight: Int,
    options: RadialConfig = RadialConfig(),
    block: StyledDOMBuilder<DIV>.() -> Unit = {}
): ReactElement {
    val containerWidth = options.diameter + itemWidth
    val containerHeight = options.diameter + itemHeight

    return styledDiv {
        css {
            zeroMargins()
            +GlobalStyles.fixedCenter
            width = containerWidth.px
            height = containerHeight.px
        }
        block()
        radialListItems(itemBuilders, options)
        radialListCenter(centerElementBuilder)
    }
}

private fun RBuilder.radialListItems(itemBuilders: List<ElementBuilder>, radialConfig: RadialConfig): ReactElement {
    val offsets = offsetsFromCenter(itemBuilders.size, radialConfig)
    return styledUl {
        css {
            zeroMargins()
            transition(property = "all", duration = 500.ms, timing = Timing.easeInOut)
            zIndex = 1
            width = radialConfig.diameter.px
            height = radialConfig.diameter.px
            absoluteCenter()
        }
        itemBuilders.forEachIndexed { i, itemBuilder ->
            radialListItem(itemBuilder, i, offsets[i])
        }
    }
}

private fun RBuilder.radialListItem(itemBuilder: ElementBuilder, i: Int, offset: CartesianCoords): ReactElement {
    return styledLi {
        css {
            display = Display.block
            position = Position.absolute
            top = 50.pct
            left = 50.pct
            zeroMargins()
            listStyleType = ListStyleType.unset
            transition("all", 500.ms, Timing.easeInOut)
            zIndex = 1
            transform {
                translate(offset.x.px, offset.y.px)
                translate((-50).pct, (-50).pct)
            }
        }
        attrs {
            key = "$i"
        }
        itemBuilder()
    }
}

private fun RBuilder.radialListCenter(centerElement: ElementBuilder?): ReactElement? {
    if (centerElement == null) {
        return null
    }
    return styledDiv {
        css {
            zIndex = 0
            absoluteCenter()
        }
        centerElement()
    }
}

private fun CSSBuilder.absoluteCenter() {
    position = Position.absolute
    left = 50.pct
    top = 50.pct
    transform {
        translate((-50).pct, (-50).pct)
    }
}

private fun CSSBuilder.zeroMargins() {
    margin(all = 0.px)
    padding(all = 0.px)
}
