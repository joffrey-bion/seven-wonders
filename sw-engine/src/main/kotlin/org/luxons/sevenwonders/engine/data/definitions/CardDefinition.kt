package org.luxons.sevenwonders.engine.data.definitions

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.engine.cards.Requirements
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.Color

@Serializable
internal class CardDefinition(
    private val name: String,
    private val color: Color,
    private val effect: EffectsDefinition,
    private val requirements: Requirements? = null,
    private val chainParents: List<String>? = null,
    private val chainChildren: List<String>? = null,
    private val countPerNbPlayer: Map<Int, Int>? = null,
    private val image: String,
) {
    fun create(back: CardBack, nbPlayers: Int): List<Card> = List(countPerNbPlayer!![nbPlayers] ?: 0) { create(back) }

    fun create(back: CardBack): Card {
        val reqs = requirements ?: Requirements()
        val parents = chainParents ?: emptyList()
        val children = chainChildren ?: emptyList()
        return Card(name, color, reqs, effect.create(), parents, children, image, back)
    }
}
