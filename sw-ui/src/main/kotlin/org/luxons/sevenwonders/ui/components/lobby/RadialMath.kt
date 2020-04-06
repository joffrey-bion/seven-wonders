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

// Y-axis is pointing down in the browser, so the directions need to be reversed
// (positive angles are now clockwise)
enum class Direction(private val value: Int) {
    CLOCKWISE(1),
    COUNTERCLOCKWISE(-1);

    fun toOrientedDegrees(deg: Int) = value * deg
}

data class RadialConfig(
    val radius: Int = 120,
    val spreadArcDegrees: Int = 360, // full circle
    val firstItemAngleDegrees: Int = 0, // 12 o'clock
    val direction: Direction = Direction.CLOCKWISE
) {
    val diameter: Int = radius * 2
}

private const val DEFAULT_START = -90 // Up, because Y-axis is reversed

fun offsetsFromCenter(nbItems: Int, radialConfig: RadialConfig = RadialConfig()): List<CartesianCoords> {
    val startAngle = DEFAULT_START + radialConfig.direction.toOrientedDegrees(radialConfig.firstItemAngleDegrees)
    val angleStep = radialConfig.spreadArcDegrees / nbItems
    return List(nbItems) { itemCartesianOffsets(startAngle, angleStep, it, radialConfig) }
}

private fun itemCartesianOffsets(startAngle: Int, angleStep: Int, index: Int, config: RadialConfig): CartesianCoords {
    val itemAngle = startAngle + config.direction.toOrientedDegrees(angleStep) * index
    return PolarCoords(config.radius, itemAngle).toCartesian()
}
