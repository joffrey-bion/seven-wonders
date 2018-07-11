package org.luxons.sevenwonders.game.resources

import org.luxons.sevenwonders.game.Player

data class ResourceTransactions(private val resourcesByProvider: MutableMap<Provider, Resources> = mutableMapOf()) {

    constructor(transactions: Collection<ResourceTransaction>) : this() {
        transactions.forEach { t -> add(t.provider, t.resources) }
    }

    fun add(provider: Provider, resources: Resources) {
        resourcesByProvider.merge(provider, resources) { old, new -> old + new }
    }

    fun remove(provider: Provider, resources: Resources) {
        resourcesByProvider.compute(provider) { _, prevResources ->
            if (prevResources == null) {
                throw IllegalStateException("Cannot remove resources from resource transactions")
            }
            prevResources.minus(resources)
        }
    }

    fun execute(player: Player) {
        asList().forEach { it.execute(player) }
    }

    fun asList(): List<ResourceTransaction> {
        return resourcesByProvider
            .filter { (_, resources) -> !resources.isEmpty }
            .map { (provider, resources) -> ResourceTransaction(provider, resources) }
    }

    fun asResources(): Resources {
        return resourcesByProvider.values.fold(Resources(), Resources::plus)
    }
}
