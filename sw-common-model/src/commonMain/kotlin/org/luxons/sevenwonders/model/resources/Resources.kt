package org.luxons.sevenwonders.model.resources

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition

enum class ResourceType(val symbol: Char) {
    WOOD('W'),
    STONE('S'),
    ORE('O'),
    CLAY('C'),
    GLASS('G'),
    PAPYRUS('P'),
    LOOM('L');

    companion object {

        private val typesPerSymbol = values().associateBy { it.symbol }

        fun fromSymbol(symbol: String): ResourceType {
            if (symbol.length != 1) {
                throw IllegalArgumentException("The given symbol must be a valid single-char resource type, got $symbol")
            }
            return fromSymbol(symbol[0])
        }

        fun fromSymbol(symbol: Char?): ResourceType =
            typesPerSymbol[symbol] ?: throw IllegalArgumentException("Unknown resource type symbol '$symbol'")
    }
}

@Serializable
data class CountedResource(
    val count: Int,
    val type: ResourceType,
) {
    override fun toString(): String = "$count $type"
}

enum class Provider(val boardPosition: RelativeBoardPosition) {
    LEFT_PLAYER(RelativeBoardPosition.LEFT),
    RIGHT_PLAYER(RelativeBoardPosition.RIGHT)
}

@Serializable
sealed class ResourceTransaction {
    abstract val provider: Provider
    abstract val resources: List<CountedResource>
}

typealias ResourceTransactions = Set<ResourceTransaction>

@Serializable
data class PricedResourceTransaction(
    override val provider: Provider,
    override val resources: List<CountedResource>,
    val totalPrice: Int,
) : ResourceTransaction() {
    override fun toString(): String = "{$totalPrice coin(s) to $provider for $resources}"
}

typealias PricedResourceTransactions = Set<PricedResourceTransaction>

typealias ResourceTransactionOptions = List<PricedResourceTransactions>

val PricedResourceTransactions.totalPrice: Int
    get() = sumBy { it.totalPrice }

val ResourceTransactionOptions.bestPrice: Int
    get() = minOfOrNull { it.totalPrice } ?: Int.MAX_VALUE

fun noTransactions(): PricedResourceTransactions = emptySet()

fun noTransactionOptions(): ResourceTransactionOptions = emptyList()

fun singleOptionNoTransactionNeeded(): ResourceTransactionOptions = listOf(noTransactions())
