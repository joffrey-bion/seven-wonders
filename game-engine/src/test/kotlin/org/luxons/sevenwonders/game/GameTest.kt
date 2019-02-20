package org.luxons.sevenwonders.game

import org.junit.Test
import org.luxons.sevenwonders.game.api.Action
import org.luxons.sevenwonders.game.api.HandCard
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.PlayerTurnInfo
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.data.GameDefinition
import org.luxons.sevenwonders.game.data.LAST_AGE
import org.luxons.sevenwonders.game.moves.MoveType
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.noTransactions
import org.luxons.sevenwonders.game.test.testCustomizableSettings
import java.util.HashMap
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GameTest {

    @Test
    fun testFullGame() {
        val nbPlayers = 5
        val game = createGame(nbPlayers)

        for (age in 1..LAST_AGE) {
            playAge(nbPlayers, game, age)
        }
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

    private fun getFirstAvailableMove(turnInfo: PlayerTurnInfo): PlayerMove? {
        return when (turnInfo.action) {
            Action.PLAY, Action.PLAY_2, Action.PLAY_LAST -> createPlayCardMove(turnInfo)
            Action.PICK_NEIGHBOR_GUILD -> createPickGuildMove(turnInfo)
            Action.WAIT -> null
            else -> null
        }
    }

    private fun createPlayCardMove(turnInfo: PlayerTurnInfo): PlayerMove {
        for (handCard in turnInfo.hand) {
            if (handCard.isPlayable) {
                val transactions = findResourcesToBuyFor(handCard)
                return PlayerMove(MoveType.PLAY, handCard.name, transactions)
            }
        }
        val firstCardInHand = turnInfo.hand[0]
        return PlayerMove(MoveType.DISCARD, firstCardInHand.name)
    }

    private fun findResourcesToBuyFor(handCard: HandCard): ResourceTransactions {
        if (handCard.isFree) {
            return noTransactions()
        }
        return handCard.cheapestTransactions.possibleTransactions.first()
    }

    private fun createPickGuildMove(turnInfo: PlayerTurnInfo): PlayerMove {
        val neighbourGuilds = turnInfo.neighbourGuildCards
        assertNotNull(neighbourGuilds)
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
        }
    }
}
