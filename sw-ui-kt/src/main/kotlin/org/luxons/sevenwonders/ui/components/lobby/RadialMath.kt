package org.luxons.sevenwonders.ui.components.lobby

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class CartesianCoords(
    val x: Int,
    val y: Int
)

data class PolarCoords(
    val radius: Int,
    val angleDeg: Int
)

private fun Int.toRadians() = (this * PI / 180.0)
private fun Double.project(angleRad: Double, trigFn: (Double) -> Double) = (this * trigFn(angleRad)).roundToInt()
private fun Double.xProjection(angleRad: Double) = project(angleRad, ::cos)
private fun Double.yProjection(angleRad: Double) = project(angleRad, ::sin)

private fun PolarCoords.toCartesian() = CartesianCoords(
    x = radius.toDouble().xProjection(angleDeg.toRadians()),
    y = radius.toDouble().yProjection(angleDeg.toRadians())
)

enum class Direction(private val value: Int) {
    CLOCKWISE(-1),
    COUNTERCLOCKWISE(1);

    fun toOrientedDegrees(deg: Int) = value * deg
}

data class RadialConfig(
    val radius: Int = 120,
    val arcDegrees: Int = 360, // full circle
    val offsetDegrees: Int = 0, // 12 o'clock
    val direction: Direction = Direction.CLOCKWISE
) {
    val diameter: Int = radius * 2
}

private const val DEFAULT_START = 90 // Up

fun offsetsFromCenter(nbItems: Int, radialConfig: RadialConfig = RadialConfig()): List<CartesianCoords> =
        (0 until nbItems).map { itemCartesianOffsets(it, nbItems, radialConfig) }

private fun itemCartesianOffsets(index: Int, nbItems: Int, config: RadialConfig): CartesianCoords {
    val startAngle = DEFAULT_START + config.direction.toOrientedDegrees(config.offsetDegrees)
    val angleStep = config.arcDegrees / nbItems
    val itemAngle = startAngle + config.direction.toOrientedDegrees(angleStep) * index
    return PolarCoords(config.radius, itemAngle).toCartesian()
}
