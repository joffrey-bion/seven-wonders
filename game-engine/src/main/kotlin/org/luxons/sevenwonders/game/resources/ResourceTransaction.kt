package org.luxons.sevenwonders.game.resources

import org.luxons.sevenwonders.game.Player

data class ResourceTransaction(val provider: Provider, val resources: Resources) {

    internal fun execute(player: Player) {
        val board = player.board
        val price = board.tradingRules.computeCost(this)
        board.removeGold(price)
        val providerPosition = provider.boardPosition
        val providerBoard = player.getBoard(providerPosition)
        providerBoard.addGold(price)
    }
}
