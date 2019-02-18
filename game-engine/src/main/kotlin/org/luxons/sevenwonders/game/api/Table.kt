package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition
import org.luxons.sevenwonders.game.boards.neighboursPositions
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.cards.HandRotationDirection
import org.luxons.sevenwonders.game.data.Age
import org.luxons.sevenwonders.game.moves.Move

/**
 * The table contains what is visible by all the players in the game: the boards and their played cards, and the
 * players' information.
 */
class Table internal constructor(val boards: List<Board>) {

    val nbPlayers: Int = boards.size

    var currentAge: Age = 0
        private set

    val handRotationDirection: HandRotationDirection
        get() = HandRotationDirection.forAge(currentAge)

    var lastPlayedMoves: List<Move> = emptyList()
        internal set

    internal fun getBoard(playerIndex: Int): Board = boards[playerIndex]

    internal fun getBoard(playerIndex: Int, position: RelativeBoardPosition): Board =
        boards[position.getIndexFrom(playerIndex, nbPlayers)]

    internal fun increaseCurrentAge() {
        this.currentAge++
    }

    internal fun resolveMilitaryConflicts() {
        for (i in 0 until nbPlayers) {
            val board1 = getBoard(i)
            val board2 = getBoard(i, RelativeBoardPosition.RIGHT)
            resolveConflict(board1, board2)
        }
    }

    private fun resolveConflict(board1: Board, board2: Board) {
        val shields1 = board1.military.nbShields
        val shields2 = board2.military.nbShields
        if (shields1 < shields2) {
            board1.military.defeat()
            board2.military.victory(currentAge)
        } else if (shields1 > shields2) {
            board1.military.victory(currentAge)
            board2.military.defeat()
        }
    }

    internal fun getNeighbourGuildCards(playerIndex: Int): List<Card> = neighboursPositions()
        .flatMap { getBoard(playerIndex, it).getPlayedCards() }
        .filter { it.color == Color.PURPLE }
}
