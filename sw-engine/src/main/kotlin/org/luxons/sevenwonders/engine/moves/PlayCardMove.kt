package org.luxons.sevenwonders.engine.moves

import org.luxons.sevenwonders.engine.PlayerContext
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.Settings

internal class PlayCardMove(move: PlayerMove, card: Card, player: PlayerContext) :
    CardFromHandMove(move, card, player) {

    init {
        if (!card.isPlayableOnBoardWith(player.board, transactions)) {
            throw InvalidMoveException(this, "requirements not met to play the card ${card.name}")
        }
    }

    override fun place(discardedCards: MutableList<Card>, settings: Settings) = playerContext.board.addCard(card)

    override fun activate(discardedCards: List<Card>, settings: Settings) = card.applyTo(playerContext, transactions)
}
