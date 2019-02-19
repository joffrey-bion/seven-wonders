package org.luxons.sevenwonders.game.resources

import org.luxons.sevenwonders.game.Player

typealias ResourceTransactions = Collection<ResourceTransaction>

fun noTransactions(): ResourceTransactions = emptySet()

fun Map<Provider, Resources>.toTransactions(): ResourceTransactions =
    filterValues { !it.isEmpty() }.map { (p, res) -> ResourceTransaction(p, res) }.toSet()

fun ResourceTransactions.asResources(): Resources = map { it.resources }.merge()

internal fun ResourceTransactions.execute(player: Player) = forEach { it.execute(player) }

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
