package org.luxons.sevenwonders.engine.moves

import org.luxons.sevenwonders.engine.PlayerContext
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.Settings

internal class BuildWonderMove(move: PlayerMove, card: Card, player: PlayerContext) :
    CardFromHandMove(move, card, player) {

    private val wonder = player.board.wonder

    init {
        if (!wonder.isNextStageBuildable(playerContext.board, transactions)) {
            throw InvalidMoveException(this, "all levels are already built, or the given resources are insufficient")
        }
    }

    override fun place(discardedCards: MutableList<Card>, settings: Settings) = wonder.placeCard(card.back)

    override fun activate(discardedCards: List<Card>, settings: Settings) =
        wonder.activateLastBuiltStage(playerContext, transactions)
}
