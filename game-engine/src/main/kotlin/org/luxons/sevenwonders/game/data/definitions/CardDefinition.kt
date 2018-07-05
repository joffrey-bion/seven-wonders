package org.luxons.sevenwonders.game.data.definitions

import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.cards.Requirements

internal class CardDefinition(
    private val name: String,
    private val color: Color,
    private val requirements: Requirements = Requirements(),
    private val effect: EffectsDefinition,
    private val chainParent: String? = null,
    private val chainChildren: List<String> = emptyList(),
    private val image: String,
    private val countPerNbPlayer: Map<Int, Int>
) {
    fun create(back: CardBack, nbPlayers: Int): List<Card> = List(countPerNbPlayer[nbPlayers] ?: 0) { create(back) }

    fun create(back: CardBack): Card =
        Card(name, color, requirements, effect.create(), chainParent, chainChildren, image, back)
}
