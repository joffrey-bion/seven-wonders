package org.luxons.sevenwonders.ui.components.lobby

import kotlinx.css.CSSBuilder
import kotlinx.css.Display
import kotlinx.css.ListStyleType
import kotlinx.css.Position
import kotlinx.css.display
import kotlinx.css.height
import kotlinx.css.left
import kotlinx.css.listStyleType
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.properties.Timing
import kotlinx.css.properties.ms
import kotlinx.css.properties.transform
import kotlinx.css.properties.transition
import kotlinx.css.properties.translate
import kotlinx.css.px
import kotlinx.css.top
import kotlinx.css.width
import kotlinx.css.zIndex
import react.RBuilder
import react.ReactElement
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledLi
import styled.styledUl

typealias ElementBuilder = RBuilder.() -> ReactElement

fun RBuilder.radialList(
    itemBuilders: List<ElementBuilder>,
    centerElementBuilder: ElementBuilder,
    itemWidth: Int,
    itemHeight: Int,
    options: RadialConfig = RadialConfig()
): ReactElement {
    val containerWidth = options.diameter + itemWidth
    val containerHeight = options.diameter + itemHeight

    return styledDiv {
        css {
            zeroMargins()
            position = Position.relative
            width = containerWidth.px
            height = containerHeight.px
        }
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
    margin = "0"
    padding = "0"
}
