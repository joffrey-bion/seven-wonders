package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.api.Table

data class SpecialAbilityActivation(val specialAbility: SpecialAbility) : Effect {

    override fun apply(table: Table, playerIndex: Int) = specialAbility.apply(table.getBoard(playerIndex))

    override fun computePoints(table: Table, playerIndex: Int): Int = specialAbility.computePoints(table, playerIndex)
}
