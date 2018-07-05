package org.luxons.sevenwonders.game.cards

import org.luxons.sevenwonders.game.api.HandCard
import org.luxons.sevenwonders.game.api.Table

class Hands internal constructor(private val hands: List<List<Card>>) {

    val isEmpty: Boolean = this.hands.all(List<Card>::isEmpty)

    operator fun get(playerIndex: Int): List<Card> {
        return hands[playerIndex]
    }

    fun discard(playerIndex: Int): Hands {
        val newHands = hands.mapIndexed { index, hand -> if (index == playerIndex) emptyList() else hand }
        return Hands(newHands)
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
