package org.luxons.sevenwonders.game

import org.junit.Test
import org.luxons.sevenwonders.game.api.Action
import org.luxons.sevenwonders.game.api.Board
import org.luxons.sevenwonders.game.api.PlayedMove
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.PlayerTurnInfo
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.data.GameDefinition
import org.luxons.sevenwonders.game.data.LAST_AGE
import org.luxons.sevenwonders.game.moves.MoveType
import org.luxons.sevenwonders.game.test.testCustomizableSettings
import java.util.HashMap
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GameTest {

    @Test
    fun testFullGame3Players() {
        playGame(nbPlayers = 3)
    }

    @Test
    fun testFullGame5Players() {
        playGame(nbPlayers = 6)
    }

    @Test
    fun testFullGame7Players() {
        playGame(nbPlayers = 7)
    }

    private fun playGame(nbPlayers: Int) {
        val game = createGame(nbPlayers)

        (1..LAST_AGE).forEach { playAge(nbPlayers, game, it) }

        game.computeScore()
    }

    private fun playAge(nbPlayers: Int, game: Game, age: Int) {
        repeat(6) {
            playTurn(nbPlayers, game, age, 7 - it)
        }
    }

    private fun createGame(nbPlayers: Int): Game =
        GameDefinition.load().initGame(0, testCustomizableSettings(), nbPlayers)

    private fun playTurn(nbPlayers: Int, game: Game, ageToCheck: Int, handSize: Int) {
        val turnInfos = game.getCurrentTurnInfo()
        assertEquals(nbPlayers, turnInfos.size)

        val sentMoves = HashMap<Int, PlayerMove>(turnInfos.size)
        for (turnInfo in turnInfos) {
            assertEquals(ageToCheck, turnInfo.currentAge)
            assertEquals(handSize, turnInfo.hand.size)
            val move = getFirstAvailableMove(turnInfo)
            if (move != null) {
                game.prepareMove(turnInfo.playerIndex, move)
                sentMoves[turnInfo.playerIndex] = move
            }
        }
        assertTrue(game.allPlayersPreparedTheirMove())
        val table = game.playTurn()
        checkLastPlayedMoves(sentMoves, table)
    }

    private fun getFirstAvailableMove(turnInfo: PlayerTurnInfo): PlayerMove? = when (turnInfo.action) {
        Action.PLAY, Action.PLAY_2, Action.PLAY_LAST -> createPlayCardMove(turnInfo)
        Action.PICK_NEIGHBOR_GUILD -> createPickGuildMove(turnInfo)
        Action.WAIT -> null
    }

    private fun createPlayCardMove(turnInfo: PlayerTurnInfo): PlayerMove {
        val playableCard = turnInfo.hand.firstOrNull { it.playability.isPlayable }

        return if (playableCard != null) {
            PlayerMove(MoveType.PLAY, playableCard.name, playableCard.playability.cheapestTransactions.first())
        } else {
            PlayerMove(MoveType.DISCARD, turnInfo.hand.first().name)
        }
    }

    private fun createPickGuildMove(turnInfo: PlayerTurnInfo): PlayerMove {
        val neighbourGuilds = turnInfo.neighbourGuildCards
        assertFalse(neighbourGuilds.isEmpty())
        val cardName = neighbourGuilds[0].name
        return PlayerMove(MoveType.COPY_GUILD, cardName)
    }

    private fun checkLastPlayedMoves(sentMoves: Map<Int, PlayerMove>, table: Table) {
        for (move in table.lastPlayedMoves) {
            val sentMove = sentMoves[move.playerIndex]
            assertNotNull(sentMove)
            assertNotNull(move.card)
            assertEquals(sentMove.cardName, move.card.name)
            assertEquals(sentMove.type, move.type)
            assertEquals(sentMove.transactions, move.transactions)

            val board = table.boards[move.playerIndex]
            when (sentMove.type) {
                MoveType.PLAY, MoveType.PLAY_FREE -> checkLastPlayedCard(move, board)
                MoveType.UPGRADE_WONDER -> checkWonderUpgraded(move, board)
                else -> Unit
            }
        }
    }

    private fun checkLastPlayedCard(move: PlayedMove, board: Board) {
        val card = board.playedCards.first { it.name == move.card.name }
        assertTrue(card.playedDuringLastMove)
    }

    private fun checkWonderUpgraded(move: PlayedMove, board: Board) {
        val stages = board.wonder.stages

        val lastBuiltStage = stages.last { it.isBuilt }
        val otherStages = stages - lastBuiltStage

        assertEquals(move.type == MoveType.UPGRADE_WONDER, lastBuiltStage.builtDuringLastMove)
        assertFalse(otherStages.any { it.builtDuringLastMove })
    }
}
