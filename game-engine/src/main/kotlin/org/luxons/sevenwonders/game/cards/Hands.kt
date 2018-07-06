package org.luxons.sevenwonders.game.cards

import org.luxons.sevenwonders.game.api.HandCard
import org.luxons.sevenwonders.game.api.Table

class Hands internal constructor(private val hands: List<List<Card>>) {

    val isEmpty: Boolean = this.hands.all(List<Card>::isEmpty)

    operator fun get(playerIndex: Int): List<Card> {
        return hands[playerIndex]
    }

    fun discardHand(playerIndex: Int): Hands {
        val mutatedHands = hands.toMutableList()
        mutatedHands[playerIndex] = emptyList()
        return Hands(mutatedHands)
    }

    fun remove(playerIndex: Int, card: Card): Hands {
        val mutatedHands = hands.toMutableList()
        mutatedHands[playerIndex] = hands[playerIndex] - card
        return Hands(mutatedHands)
    }

    fun createHand(table: Table, playerIndex: Int): List<HandCard> {
        return hands[playerIndex].map { c -> HandCard(c, table, playerIndex) }
    }

    fun rotate(direction: HandRotationDirection): Hands {
        val newHands = when (direction) {
            HandRotationDirection.RIGHT -> hands.takeLast(1) + hands.dropLast(1)
            HandRotationDirection.LEFT -> hands.drop(1) + hands.take(1)
        }
        return Hands(newHands)
    }

    fun maxOneCardRemains(): Boolean = hands.map { it.size }.max() ?: 0 <= 1
}
