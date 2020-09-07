package org.luxons.sevenwonders.engine.cards

internal fun List<Card>.deal(nbPlayers: Int): Hands {
    val hands: Map<Int, List<Card>> = this.withIndex() //
        .groupBy { (index, _) -> index % nbPlayers } //
        .mapValues { it.value.map { (_, cards) -> cards } }

    val allHands = List(nbPlayers) { i -> hands[i] ?: emptyList() }
    return Hands(allHands)
}

internal class Decks(private val cardsPerAge: Map<Int, List<Card>>) {

    fun getCard(age: Int, cardName: String): Card =
        getDeck(age).firstOrNull { c -> c.name == cardName } ?: throw CardNotFoundException(cardName)

    fun deal(age: Int, nbPlayers: Int): Hands {
        val deck = getDeck(age)
        validateNbCards(deck, nbPlayers)
        return deck.deal(nbPlayers)
    }

    private fun getDeck(age: Int): List<Card> {
        return cardsPerAge[age] ?: throw IllegalArgumentException("No deck found for age $age")
    }

    private fun validateNbCards(deck: List<Card>, nbPlayers: Int) {
        if (nbPlayers == 0) {
            throw IllegalArgumentException("Cannot deal cards between 0 players")
        }
        if (deck.size % nbPlayers != 0) {
            throw IllegalArgumentException("Cannot deal ${deck.size} cards evenly between $nbPlayers players")
        }
    }

    class CardNotFoundException(message: String) : RuntimeException(message)
}
