package org.luxons.sevenwonders.ui.components.lobby

import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.DIV
import org.luxons.sevenwonders.ui.components.GlobalStyles
import react.RBuilder
import react.ReactElement
import react.dom.*
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv
import styled.styledLi
import styled.styledUl

fun <T> RBuilder.radialList(
    items: List<T>,
    centerElement: ReactElement,
    renderItem: (T) -> ReactElement,
    getKey: (T) -> String,
    itemWidth: Int,
    itemHeight: Int,
    options: RadialConfig = RadialConfig(),
    block: StyledDOMBuilder<DIV>.() -> Unit = {},
) {
    val containerWidth = options.diameter + itemWidth
    val containerHeight = options.diameter + itemHeight

    styledDiv {
        css {
            zeroMargins()
            +GlobalStyles.fixedCenter
            width = containerWidth.px
            height = containerHeight.px
        }
        block()
        radialListItems(items, renderItem, getKey, options)
        radialListCenter(centerElement)
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun <T> RBuilder.radialListItems(
    items: List<T>,
    renderItem: (T) -> ReactElement,
    getKey: (T) -> String,
    radialConfig: RadialConfig,
) {
    val offsets = offsetsFromCenter(items.size, radialConfig)
    styledUl {
        css {
            zeroMargins()
            transition(property = "all", duration = 500.ms, timing = Timing.easeInOut)
            zIndex = 1
            width = radialConfig.diameter.px
            height = radialConfig.diameter.px
            absoluteCenter()
        }
        // We ensure a stable order of the DOM elements so that position animations look nice.
        // We still respect the order of the items in the list when placing them along the circle.
        val indexByKey = buildMap<String, Int> {
            items.forEachIndexed { index, item -> put(getKey(item), index) }
        }
        items.sortedBy { getKey(it) }.forEach { item ->
            val key = getKey(item)
            radialListItem(renderItem(item), key, offsets[indexByKey.getValue(key)])
        }
    }
}

private fun RBuilder.radialListItem(item: ReactElement, key: String, offset: CartesianCoords) {
    styledLi {
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
            this.key = key
        }
        child(item)
    }
}

private fun RBuilder.radialListCenter(centerElement: ReactElement?) {
    if (centerElement == null) {
        return
    }
    styledDiv {
        css {
            zIndex = 0
            absoluteCenter()
        }
        child(centerElement)
    }
}

private fun CssBuilder.absoluteCenter() {
    position = Position.absolute
    left = 50.pct
    top = 50.pct
    transform {
        translate((-50).pct, (-50).pct)
    }
}

private fun CssBuilder.zeroMargins() {
    margin(all = 0.px)
    padding(all = 0.px)
}
