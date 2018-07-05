package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.cards.HandRotationDirection
import org.luxons.sevenwonders.game.moves.Move
import org.luxons.sevenwonders.game.resources.Provider

/**
 * The table contains what is visible by all the players in the game: the boards and their played cards, and the
 * players' information.
 */
class Table(val boards: List<Board>) {

    val nbPlayers: Int = boards.size

    var currentAge = 0
        private set

    val handRotationDirection: HandRotationDirection
        get() = HandRotationDirection.forAge(currentAge)

    var lastPlayedMoves: List<Move> = emptyList()

    fun getBoard(playerIndex: Int): Board {
        return boards[playerIndex]
    }

    fun getBoard(playerIndex: Int, position: RelativeBoardPosition): Board {
        return boards[position.getIndexFrom(playerIndex, nbPlayers)]
    }

    fun increaseCurrentAge() {
        this.currentAge++
    }

    fun resolveMilitaryConflicts() {
        for (i in 0 until nbPlayers) {
            val board1 = getBoard(i)
            val board2 = getBoard((i + 1) % nbPlayers)
            resolveConflict(board1, board2, currentAge)
        }
    }

    private fun resolveConflict(board1: Board, board2: Board, age: Int) {
        val shields1 = board1.military.nbShields
        val shields2 = board2.military.nbShields
        if (shields1 < shields2) {
            board1.military.defeat()
            board2.military.victory(age)
        } else if (shields1 > shields2) {
            board1.military.victory(age)
            board2.military.defeat()
        }
    }

    fun getNeighbourGuildCards(playerIndex: Int): List<Card> {
        return getNeighbourBoards(playerIndex).flatMap(Board::getPlayedCards).filter { c -> c.color == Color.PURPLE }
    }

    private fun getNeighbourBoards(playerIndex: Int): List<Board> {
        return Provider.values().map { getBoard(playerIndex, it.boardPosition) }
    }
}
