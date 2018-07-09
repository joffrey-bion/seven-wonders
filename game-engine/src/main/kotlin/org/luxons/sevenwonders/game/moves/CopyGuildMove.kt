package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.effects.SpecialAbility

class CopyGuildMove internal constructor(playerIndex: Int, card: Card, move: PlayerMove) :
    Move(playerIndex, card, move) {

    @Throws(InvalidMoveException::class)
    override fun validate(table: Table, playerHand: List<Card>) {
        val board = table.getBoard(playerIndex)
        if (!board.hasSpecial(SpecialAbility.COPY_GUILD)) {
            throw InvalidMoveException("Player $playerIndex does not have the ability to copy guild cards")
        }
        if (card.color !== Color.PURPLE) {
            throw InvalidMoveException("Player $playerIndex cannot copy card ${card.name} because it is not a guild card")
        }
        val leftNeighbourHasIt = neighbourHasTheCard(table, RelativeBoardPosition.LEFT)
        val rightNeighbourHasIt = neighbourHasTheCard(table, RelativeBoardPosition.RIGHT)
        if (!leftNeighbourHasIt && !rightNeighbourHasIt) {
            throw InvalidMoveException("Player $playerIndex cannot copy card ${card.name} because none of his neighbour has it")
        }
    }

    private fun neighbourHasTheCard(table: Table, position: RelativeBoardPosition): Boolean {
        val neighbourBoard = table.getBoard(playerIndex, position)
        return neighbourBoard.getPlayedCards().contains(card)
    }

    // nothing special to do here
    override fun place(table: Table, discardedCards: MutableList<Card>, settings: Settings) = Unit

    override fun activate(table: Table, discardedCards: List<Card>, settings: Settings) {
        table.getBoard(playerIndex).copiedGuild = card
    }
}
