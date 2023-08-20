package org.luxons.sevenwonders.ui.components.lobby

import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.ui.components.*
import react.*
import react.dom.html.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ul
import web.cssom.*
import web.html.*

fun <T> ChildrenBuilder.radialList(
    items: List<T>,
    centerElement: ReactElement<*>,
    renderItem: (T) -> ReactElement<*>,
    getKey: (T) -> String,
    itemWidth: Int,
    itemHeight: Int,
    options: RadialConfig = RadialConfig(),
    block: HTMLAttributes<HTMLDivElement>.() -> Unit = {},
) {
    val containerWidth = options.diameter + itemWidth
    val containerHeight = options.diameter + itemHeight

    div {
        css(GlobalStyles.fixedCenter) {
            zeroMargins()
            width = containerWidth.px
            height = containerHeight.px
        }
        block()
        radialListItems(items, renderItem, getKey, options)
        radialListCenter(centerElement)
    }
}

private fun <T> ChildrenBuilder.radialListItems(
    items: List<T>,
    renderItem: (T) -> ReactElement<*>,
    getKey: (T) -> String,
    radialConfig: RadialConfig,
) {
    val offsets = offsetsFromCenter(items.size, radialConfig)
    ul {
        css {
            zeroMargins()
            transition = Transition(
                property = TransitionProperty.all,
                duration = 500.ms,
                timingFunction = TransitionTimingFunction.easeInOut,
            )
            zIndex = integer(1)
            width = radialConfig.diameter.px
            height = radialConfig.diameter.px
            absoluteCenter()
        }
        // We ensure a stable order of the DOM elements so that position animations look nice.
        // We still respect the order of the items in the list when placing them along the circle.
        val indexByKey = buildMap {
            items.forEachIndexed { index, item -> put(getKey(item), index) }
        }
        items.sortedBy { getKey(it) }.forEach { item ->
            val key = getKey(item)
            radialListItem(renderItem(item), key, offsets[indexByKey.getValue(key)])
        }
    }
}

private fun ChildrenBuilder.radialListItem(item: ReactElement<*>, key: String, offset: CartesianCoords) {
    li {
        css {
            display = Display.block
            position = Position.absolute
            top = 50.pct
            left = 50.pct
            zeroMargins()
            listStyleType = Globals.unset
            transition = Transition(
                property = TransitionProperty.all,
                duration = 500.ms,
                timingFunction = TransitionTimingFunction.easeInOut,
            )
            zIndex = integer(1)
            transform = translate(offset.x.px - 50.pct, offset.y.px - 50.pct)
        }
        this.key = key

        +item
    }
}

private fun ChildrenBuilder.radialListCenter(centerElement: ReactElement<*>?) {
    if (centerElement == null) {
        return
    }
    div {
        css {
            zIndex = integer(0)
            absoluteCenter()
        }
        +centerElement
    }
}

private fun PropertiesBuilder.absoluteCenter() {
    position = Position.absolute
    left = 50.pct
    top = 50.pct
    transform = translate((-50).pct, (-50).pct)
}

private fun PropertiesBuilder.zeroMargins() {
    margin = Margin(vertical = 0.px, horizontal = 0.px)
    padding = Padding(vertical = 0.px, horizontal = 0.px)
}
