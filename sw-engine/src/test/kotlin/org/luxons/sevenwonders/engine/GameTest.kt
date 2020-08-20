package org.luxons.sevenwonders.engine

import org.luxons.sevenwonders.engine.data.GameDefinition
import org.luxons.sevenwonders.engine.data.LAST_AGE
import org.luxons.sevenwonders.engine.test.SEED
import org.luxons.sevenwonders.engine.test.testSettings
import org.luxons.sevenwonders.model.Action
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.PlayedMove
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.model.wonders.deal
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class GameTest {

    @Test
    fun testFullGame3Players() = playGame(nbPlayers = 3)

    @Test
    fun testFullGame5Players() = playGame(nbPlayers = 6)

    @Test
    fun testFullGame7Players() = playGame(nbPlayers = 7)

    private fun playGame(nbPlayers: Int) {
        val game = createGame(nbPlayers)

        (1..LAST_AGE).forEach { playAge(nbPlayers, game, it) }

        val turnInfos = game.getCurrentTurnInfo()
        assertEquals(nbPlayers, turnInfos.size)
        turnInfos.forEach {
            assertEquals(Action.WATCH_SCORE, it.action)

            val scoreBoard = it.scoreBoard
            assertNotNull(scoreBoard)
            assertEquals(nbPlayers, scoreBoard.scores.size)
        }
    }

    private fun createGame(nbPlayers: Int): Game = GameDefinition.load().let {
        val wonders = it.allWonders.deal(nbPlayers, random = Random(SEED))
        it.createGame(0, wonders, testSettings())
    }

    private fun playAge(nbPlayers: Int, game: Game, age: Int) {
        do {
            playTurn(nbPlayers, game, age)
        } while (!game.getCurrentTurnInfo().first().isStartOfAge(age + 1))
    }

    private fun PlayerTurnInfo.isStartOfAge(age: Int) = action == Action.WATCH_SCORE || currentAge == age

    private fun playTurn(nbPlayers: Int, game: Game, ageToCheck: Int) {
        val turnInfos = game.getCurrentTurnInfo()
        assertEquals(nbPlayers, turnInfos.size)
        turnInfos.forEach {
            assertEquals(ageToCheck, it.currentAge)
        }

        val moveExpectations = turnInfos.mapNotNull { it.firstAvailableMove() }

        moveExpectations.forEach { game.prepareMove(it.playerIndex, it.moveToSend) }
        assertTrue(game.allPlayersPreparedTheirMove())

        val table = game.playTurn()

        val expectedMoves = moveExpectations.map { it.expectedPlayedMove }
        assertEquals(expectedMoves, table.lastPlayedMoves)
    }

    private fun PlayerTurnInfo.firstAvailableMove(): MoveExpectation? = when (action) {
        Action.PLAY, Action.PLAY_2, Action.PLAY_LAST -> createPlayCardMove(this)
        Action.PLAY_FREE_DISCARDED -> createPlayFreeDiscardedCardMove(this)
        Action.PICK_NEIGHBOR_GUILD -> createPickGuildMove(this)
        Action.WAIT, Action.SAY_READY -> null
        Action.WATCH_SCORE -> fail("should not have WATCH_SCORE action before end of game")
    }

    private fun createPlayCardMove(turnInfo: PlayerTurnInfo): MoveExpectation {
        val wonderBuildability = turnInfo.wonderBuildability
        if (wonderBuildability.isBuildable) {
            val transactions = wonderBuildability.cheapestTransactions.first()
            return planMove(turnInfo, MoveType.UPGRADE_WONDER, turnInfo.hand!!.first(), transactions)
        }
        val playableCard = turnInfo.hand!!.firstOrNull { it.playability.isPlayable }
        return if (playableCard != null) {
            planMove(turnInfo, MoveType.PLAY, playableCard, playableCard.playability.cheapestTransactions.first())
        } else {
            planMove(turnInfo, MoveType.DISCARD, turnInfo.hand!!.first(), noTransactions())
        }
    }

    private fun createPlayFreeDiscardedCardMove(turn: PlayerTurnInfo): MoveExpectation {
        val card = turn.discardedCards?.random() ?: error("No discarded card to play")
        return MoveExpectation(
            turn.playerIndex,
            PlayerMove(MoveType.PLAY_FREE_DISCARDED, card.name, noTransactions()),
            PlayedMove(turn.playerIndex, MoveType.PLAY_FREE_DISCARDED, card.toPlayedCard(), noTransactions())
        )
    }

    private fun planMove(
        turnInfo: PlayerTurnInfo,
        moveType: MoveType,
        card: HandCard,
        transactions: ResourceTransactions
    ): MoveExpectation = MoveExpectation(
        turnInfo.playerIndex,
        PlayerMove(moveType, card.name, transactions),
        PlayedMove(turnInfo.playerIndex, moveType, card.toPlayedCard(), transactions)
    )

    private fun createPickGuildMove(turnInfo: PlayerTurnInfo): MoveExpectation {
        val neighbourGuilds = turnInfo.neighbourGuildCards

        // the game should send action WAIT if no guild cards are available around
        assertFalse(neighbourGuilds.isEmpty())
        val card = neighbourGuilds.first()
        return MoveExpectation(
            turnInfo.playerIndex,
            PlayerMove(MoveType.COPY_GUILD, card.name),
            PlayedMove(turnInfo.playerIndex, MoveType.COPY_GUILD, card.toPlayedCard(), noTransactions())
        )
    }

    data class MoveExpectation(val playerIndex: Int, val moveToSend: PlayerMove, val expectedPlayedMove: PlayedMove)

    private fun HandCard.toPlayedCard(): TableCard =
        TableCard(name, color, requirements, chainParent, chainChildren, image, back, true)
}
