package org.luxons.sevenwonders.test.api

import org.luxons.sevenwonders.game.cards.HandRotationDirection
import org.luxons.sevenwonders.game.moves.Move

class ApiTable {

    var nbPlayers: Int = 0

    var boards: List<ApiBoard>? = null

    var currentAge = 0

    var handRotationDirection: HandRotationDirection? = null

    var lastPlayedMoves: List<Move>? = null

    var neighbourGuildCards: List<ApiCard>? = null

    fun increaseCurrentAge() {
        this.currentAge++
    }
}
