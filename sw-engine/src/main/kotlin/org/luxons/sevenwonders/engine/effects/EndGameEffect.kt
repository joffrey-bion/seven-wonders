package org.luxons.sevenwonders.engine.effects

import org.luxons.sevenwonders.engine.Player

internal abstract class EndGameEffect : Effect {

    // EndGameEffects don't do anything when applied to the board, they simply give more points in the end
    override fun applyTo(player: Player) = Unit
}
