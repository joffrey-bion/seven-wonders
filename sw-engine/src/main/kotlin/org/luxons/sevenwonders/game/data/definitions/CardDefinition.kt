package org.luxons.sevenwonders.game.data.definitions

import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.cards.Requirements

internal class CardDefinition(
    private val name: String,
    private val color: Color,
    private val requirements: Requirements?,
    private val effect: EffectsDefinition,
    private val chainParent: String?,
    private val chainChildren: List<String>?,
    private val image: String,
    private val countPerNbPlayer: Map<Int, Int>
) {
    fun create(back: CardBack, nbPlayers: Int): List<Card> = List(countPerNbPlayer[nbPlayers] ?: 0) { create(back) }

    fun create(back: CardBack): Card {
        val reqs = requirements ?: Requirements()
        val children = chainChildren ?: emptyList()
        return Card(name, color, reqs, effect.create(), chainParent, children, image, back)
    }
}
