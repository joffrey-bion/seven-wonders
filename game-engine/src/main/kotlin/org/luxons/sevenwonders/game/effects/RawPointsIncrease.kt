package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.api.Table

data class RawPointsIncrease(val points: Int) : EndGameEffect() {

    override fun computePoints(table: Table, playerIndex: Int): Int = points
}
