package org.luxons.sevenwonders.game.resources

import org.luxons.sevenwonders.game.api.ApiCountedResource

typealias ResourceTransactions = Collection<ResourceTransaction>

data class ResourceTransaction(val provider: Provider, val resources: List<ApiCountedResource>)

fun noTransactions(): ResourceTransactions = emptySet()
