package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.cards.Card

data class PlayerTurnInfo internal constructor(
    val playerIndex: Int,
    val table: Table,
    val action: Action,
    val hand: List<HandCard>,
    val neighbourGuildCards: List<Card>
) {
    val currentAge: Int = table.currentAge
    val message: String = action.message
}
