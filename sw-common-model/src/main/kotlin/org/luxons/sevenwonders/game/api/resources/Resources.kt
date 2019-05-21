package org.luxons.sevenwonders.game.api.resources

import org.luxons.sevenwonders.game.api.boards.RelativeBoardPosition

enum class ResourceType(val symbol: Char) {
    WOOD('W'),
    STONE('S'),
    ORE('O'),
    CLAY('C'),
    GLASS('G'),
    PAPYRUS('P'),
    LOOM('L');

    companion object {

        private val typesPerSymbol = values().map { it.symbol to it }.toMap()

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

data class CountedResource(
    val count: Int,
    val type: ResourceType
)

enum class Provider(val boardPosition: RelativeBoardPosition) {
    LEFT_PLAYER(RelativeBoardPosition.LEFT),
    RIGHT_PLAYER(RelativeBoardPosition.RIGHT)
}

data class ResourceTransaction(val provider: Provider, val resources: List<CountedResource>)

typealias ResourceTransactions = Collection<ResourceTransaction>

fun noTransactions(): ResourceTransactions = emptySet()
