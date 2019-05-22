package org.luxons.sevenwonders.engine.resources

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.converters.toCountedResourcesList
import org.luxons.sevenwonders.model.resources.CountedResource
import org.luxons.sevenwonders.model.resources.Provider
import org.luxons.sevenwonders.model.resources.ResourceTransaction
import org.luxons.sevenwonders.model.resources.ResourceTransactions

fun Map<Provider, Resources>.toTransactions(): ResourceTransactions =
    filterValues { !it.isEmpty() }
        .map { (p, res) -> ResourceTransaction(p, res.toCountedResourcesList()) }
        .toSet()

fun ResourceTransactions.asResources(): Resources = flatMap { it.resources }.asResources()

fun ResourceTransaction.asResources(): Resources = resources.asResources()

fun List<CountedResource>.asResources(): Resources = map { it.asResources() }.merge()

fun CountedResource.asResources(): Resources = resourcesOf(type to count)

internal fun ResourceTransactions.execute(player: Player) = forEach { it.execute(player) }

internal fun ResourceTransaction.execute(player: Player) {
    val board = player.board
    val price = board.tradingRules.computeCost(this)
    board.removeGold(price)
    val providerPosition = provider.boardPosition
    val providerBoard = player.getBoard(providerPosition)
    providerBoard.addGold(price)
}
