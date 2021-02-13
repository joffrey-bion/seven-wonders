package org.luxons.sevenwonders.engine.moves

import org.luxons.sevenwonders.engine.PlayerContext
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.engine.effects.SpecialAbility
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.model.cards.Color

internal class CopyGuildMove(move: PlayerMove, card: Card, player: PlayerContext) : Move(move, card, player) {

    init {
        val board = player.board
        if (!board.hasSpecial(SpecialAbility.COPY_GUILD)) {
            throw InvalidMoveException(this, "no ability to copy guild cards")
        }
        if (card.color !== Color.PURPLE) {
            throw InvalidMoveException(this, "card '${card.name}' is not a guild card")
        }
        val leftNeighbourHasIt = neighbourHasTheCard(RelativeBoardPosition.LEFT)
        val rightNeighbourHasIt = neighbourHasTheCard(RelativeBoardPosition.RIGHT)
        if (!leftNeighbourHasIt && !rightNeighbourHasIt) {
            throw InvalidMoveException(this, "neighbours don't have card '${card.name}'")
        }
    }

    private fun neighbourHasTheCard(position: RelativeBoardPosition): Boolean =
        playerContext.getBoard(position).getPlayedCards().contains(card)

    // nothing special to do here
    override fun place(discardedCards: MutableList<Card>) = Unit

    override fun activate(discardedCards: List<Card>, settings: Settings) {
        playerContext.board.copiedGuild = card
    }
}
