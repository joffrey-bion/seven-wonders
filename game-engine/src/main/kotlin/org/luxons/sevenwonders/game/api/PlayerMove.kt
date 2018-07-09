package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.moves.MoveType
import org.luxons.sevenwonders.game.resources.ResourceTransaction

data class PlayerMove(
    val type: MoveType,
    val cardName: String,
    val transactions: Collection<ResourceTransaction> = emptyList()
)
