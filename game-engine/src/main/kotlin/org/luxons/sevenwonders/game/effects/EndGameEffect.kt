package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.api.Table

abstract class EndGameEffect : Effect {

    // EndGameEffects don't do anything when applied to the board, they simply give more points in the end
    override fun apply(table: Table, playerIndex: Int) = Unit
}
