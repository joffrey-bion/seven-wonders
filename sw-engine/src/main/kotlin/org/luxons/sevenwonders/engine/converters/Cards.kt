package org.luxons.sevenwonders.engine.converters

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.engine.moves.Move
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.cards.TableCard

internal fun Card.toTableCard(lastMove: Move? = null): TableCard = TableCard(
    name = name,
    color = color,
    requirements = requirements.toApiRequirements(),
    chainParent = chainParent,
    chainChildren = chainChildren,
    image = image,
    back = back,
    playedDuringLastMove = lastMove != null && this.name == lastMove.card.name,
)

internal fun Card.toHandCard(player: Player, forceSpecialFree: Boolean): HandCard = HandCard(
    name = name,
    color = color,
    requirements = requirements.toApiRequirements(),
    chainParent = chainParent,
    chainChildren = chainChildren,
    image = image,
    back = back,
    playability = computePlayabilityBy(player, forceSpecialFree),
)
