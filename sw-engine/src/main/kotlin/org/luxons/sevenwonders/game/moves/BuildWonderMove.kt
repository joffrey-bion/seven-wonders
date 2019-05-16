package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.PlayerContext
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.cards.Card

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
