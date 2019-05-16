package org.luxons.sevenwonders.game.resources

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.api.ApiCountedResource
import org.luxons.sevenwonders.game.api.toCountedResourcesList

fun Map<Provider, Resources>.toTransactions(): ResourceTransactions =
    filterValues { !it.isEmpty() }
        .map { (p, res) -> ResourceTransaction(p, res.toCountedResourcesList()) }
        .toSet()

fun ResourceTransactions.asResources(): Resources = flatMap { it.resources }.asResources()

fun ResourceTransaction.asResources(): Resources = resources.asResources()

fun List<ApiCountedResource>.asResources(): Resources = map { it.asResources() }.merge()

fun ApiCountedResource.asResources(): Resources = resourcesOf(type to count)

internal fun ResourceTransactions.execute(player: Player) = forEach { it.execute(player) }

internal fun ResourceTransaction.execute(player: Player) {
    val board = player.board
    val price = board.tradingRules.computeCost(this)
    board.removeGold(price)
    val providerPosition = provider.boardPosition
    val providerBoard = player.getBoard(providerPosition)
    providerBoard.addGold(price)
}
