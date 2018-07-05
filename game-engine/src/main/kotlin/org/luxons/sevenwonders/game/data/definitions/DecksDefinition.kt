package org.luxons.sevenwonders.game.data.definitions

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Decks

internal class DecksDefinition(
    private val age1: List<CardDefinition>,
    private val age2: List<CardDefinition>,
    private val age3: List<CardDefinition>,
    private val age1Back: String,
    private val age2Back: String,
    private val age3Back: String,
    private val guildCards: List<CardDefinition>
) {
    fun prepareDecks(settings: Settings): Decks {
        val cardsPerAge = mapOf(
            1 to prepareStandardDeck(age1, settings, age1Back),
            2 to prepareStandardDeck(age2, settings, age2Back),
            3 to prepareAge3Deck(settings)
        )
        return Decks(cardsPerAge)
    }

    private fun prepareStandardDeck(defs: List<CardDefinition>, settings: Settings, backImage: String): List<Card> {
        val back = CardBack(backImage)
        val cards = createDeck(defs, settings, back).toMutableList()
        cards.shuffle(settings.random)
        return cards
    }

    private fun prepareAge3Deck(settings: Settings): List<Card> {
        val back = CardBack(age3Back)
        val age3deck = createDeck(age3, settings, back).toMutableList()
        age3deck.addAll(createGuildCards(settings, back))
        age3deck.shuffle(settings.random)
        return age3deck
    }

    private fun createDeck(defs: List<CardDefinition>, settings: Settings, back: CardBack): List<Card> {
        return defs.flatMap { it.create(back, settings.nbPlayers) }
    }

    private fun createGuildCards(settings: Settings, back: CardBack): List<Card> {
        val guild = guildCards.map { it.create(back) }.toMutableList()
        guild.shuffle(settings.random)
        return guild.subList(0, settings.nbPlayers + 2)
    }
}
