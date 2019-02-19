package org.luxons.sevenwonders.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
import org.luxons.sevenwonders.game.resources.Resources
import org.luxons.sevenwonders.game.resources.bestSolution
import org.luxons.sevenwonders.game.resources.noTransactions
import org.luxons.sevenwonders.game.test.testCustomizableSettings
import java.util.HashMap

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
                val resourcesToBuy = findResourcesToBuyFor(handCard, turnInfo)
                return PlayerMove(MoveType.PLAY, handCard.card.name, resourcesToBuy)
            }
        }
        val firstCardInHand = turnInfo.hand[0]
        return PlayerMove(MoveType.DISCARD, firstCardInHand.card.name)
    }

    private fun findResourcesToBuyFor(handCard: HandCard, turnInfo: PlayerTurnInfo): ResourceTransactions {
        if (handCard.isFree) {
            return noTransactions()
        }
        val requiredResources = handCard.card.requirements.resources
        val table = turnInfo.table
        val playerIndex = turnInfo.playerIndex
        // we're supposed to have a best transaction plan because the card is playable
        return bestTransaction(requiredResources, PlayerContext(playerIndex, table, listOf()))
    }

    private fun bestTransaction(resources: Resources, player: Player): ResourceTransactions {
        return bestSolution(resources, player).possibleTransactions.first()
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
            val sentMove = sentMoves[move.playerContext.index]
            assertNotNull(sentMove)
            assertNotNull(move.card)
            assertEquals(sentMove!!.cardName, move.card.name)
        }
    }
}
