package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.api.cards.HandCard
import org.luxons.sevenwonders.game.api.cards.TableCard
import org.luxons.sevenwonders.game.moves.Move

internal fun Card.toTableCard(lastMove: Move? = null): TableCard =
    TableCard(
        name = name,
        color = color,
        requirements = requirements.toApiRequirements(),
        chainParent = chainParent,
        chainChildren = chainChildren,
        image = image,
        back = back,
        playedDuringLastMove = lastMove != null && this.name == lastMove.card.name
    )

internal fun Card.toHandCard(player: Player): HandCard =
    HandCard(
        name = name,
        color = color,
        requirements = requirements.toApiRequirements(),
        chainParent = chainParent,
        chainChildren = chainChildren,
        image = image,
        back = back,
        playability = computePlayabilityBy(player)
    )
