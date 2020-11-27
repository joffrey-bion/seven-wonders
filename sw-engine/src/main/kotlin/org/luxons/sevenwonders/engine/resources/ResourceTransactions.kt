package org.luxons.sevenwonders.engine.resources

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.converters.toCountedResourcesList
import org.luxons.sevenwonders.model.resources.*

fun Map<Provider, Resources>.toTransactions(price: Map<Provider, Int>): PricedResourceTransactions = //
    filterValues { !it.isEmpty() } //
        .map { (p, res) -> PricedResourceTransaction(p, res.toCountedResourcesList(), price[p]!!) } //
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
