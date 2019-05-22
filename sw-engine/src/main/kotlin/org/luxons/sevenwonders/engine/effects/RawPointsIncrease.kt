package org.luxons.sevenwonders.engine.effects

import org.luxons.sevenwonders.engine.Player

internal data class RawPointsIncrease(val points: Int) : EndGameEffect() {

    override fun computePoints(player: Player): Int = points
}
