package org.luxons.sevenwonders.ui.utils

import csstype.*
import js.core.*
import react.dom.html.*
import web.cssom.*

/**
 * The cubic-bezier() function defines a Cubic Bezier curve.
 *
 * A Cubic Bezier curve is defined by four points P0, P1, P2, and P3. P0 and P3 are the start and the end of the curve
 * and, in CSS these points are fixed as the coordinates are ratios. P0 is (0, 0) and represents the initial time and
 * the initial state, P3 is (1, 1) and represents the final time and the final state.
 *
 * The x coordinates provided here must be between 0 and 1 (the bezier curve points should be between the start time
 * and end time, giving other values would make the curve go back in the past or further into the future).
 *
 * The y coordinates may be any value: the intermediate states can be below or above the start (0) or end (1) values.
 */
fun cubicBezier(x1: Double, y1: Double, x2: Double, y2: Double) =
    "cubic-bezier($x1, $y1, $x2, $y2)".unsafeCast<AnimationTimingFunction>()

fun Margin(all: AutoLength) = Margin(vertical = all, horizontal = all)

fun Padding(all: Length) = Padding(vertical = all, horizontal = all)

// this should work because NamedColor is ultimately a hex string in JS, not the actual name
fun NamedColor.withAlpha(alpha: Double) = "$this${(alpha * 255).toInt().toString(16)}".unsafeCast<BackgroundColor>()

operator fun FilterFunction.plus(other: FilterFunction) = "$this $other".unsafeCast<FilterFunction>()

fun PropertiesBuilder.ancestorHover(selector: String, block: PropertiesBuilder.() -> Unit) =
    "$selector:hover &".invoke(block)

fun PropertiesBuilder.children(selector: String, block: PropertiesBuilder.() -> Unit) =
    "& > $selector".invoke(block)

fun PropertiesBuilder.descendants(selector: String, block: PropertiesBuilder.() -> Unit) =
    "& $selector".invoke(block)

fun HTMLAttributes<*>.inlineStyles(block: PropertiesBuilder.() -> Unit) {
    style = jso(block)
}
