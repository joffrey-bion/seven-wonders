package org.luxons.sevenwonders.game.resources

import org.luxons.sevenwonders.game.api.Table

data class ResourceTransaction(val provider: Provider, val resources: Resources) {

    internal fun execute(table: Table, playerIndex: Int) {
        val board = table.getBoard(playerIndex)
        val price = board.tradingRules.computeCost(this)
        board.removeGold(price)
        val providerPosition = provider.boardPosition
        val providerBoard = table.getBoard(playerIndex, providerPosition)
        providerBoard.addGold(price)
    }
}
