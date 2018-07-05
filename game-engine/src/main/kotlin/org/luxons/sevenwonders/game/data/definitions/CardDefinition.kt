package org.luxons.sevenwonders.game.data.definitions

import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.cards.Requirements

internal class CardDefinition(
    private val name: String,
    private val color: Color,
    private val requirements: Requirements? = null,
    private val effect: EffectsDefinition,
    private val chainParent: String? = null,
    private val chainChildren: List<String> = emptyList(),
    private val image: String? = null,
    private val countPerNbPlayer: Map<Int, Int>
) {
    fun create(nbPlayers: Int): List<Card> = List( countPerNbPlayer[nbPlayers]!!) { create() }

    fun create(): Card = Card(name, color, requirements, effect.create(), chainParent, chainChildren, image)
}
