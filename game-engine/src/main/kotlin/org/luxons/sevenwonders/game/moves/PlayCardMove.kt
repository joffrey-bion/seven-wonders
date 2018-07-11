package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.PlayerContext
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.cards.Card

internal class PlayCardMove(move: PlayerMove, card: Card, player: PlayerContext) :
    CardFromHandMove(move, card, player) {

    init {
        val board = player.board
        if (!card.isChainableOn(board) && !card.requirements.areMetWithHelpBy(board, transactions)) {
            throw InvalidMoveException(this, "requirements not met to play the card ${card.name}")
        }
    }

    override fun place(discardedCards: MutableList<Card>, settings: Settings) = playerContext.board.addCard(card)

    override fun activate(discardedCards: List<Card>, settings: Settings) = card.applyTo(playerContext, transactions)
}
