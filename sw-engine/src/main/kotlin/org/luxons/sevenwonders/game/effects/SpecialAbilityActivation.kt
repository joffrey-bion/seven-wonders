package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.Player

internal data class SpecialAbilityActivation(val specialAbility: SpecialAbility) : Effect {

    override fun applyTo(player: Player) = specialAbility.apply(player.board)

    override fun computePoints(player: Player): Int = specialAbility.computePoints(player)
}
