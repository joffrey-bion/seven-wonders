package org.luxons.sevenwonders.engine

import org.luxons.sevenwonders.engine.data.GameDefinition
import org.luxons.sevenwonders.engine.data.LAST_AGE
import org.luxons.sevenwonders.engine.test.SEED
import org.luxons.sevenwonders.engine.test.testSettings
import org.luxons.sevenwonders.model.*
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.model.wonders.deal
import kotlin.random.Random
import kotlin.test.*

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
            val action = it.action
            assertTrue(action is TurnAction.WatchScore)

            val scoreBoard = action.scoreBoard
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

    private fun PlayerTurnInfo.isStartOfAge(age: Int) = action is TurnAction.WatchScore || currentAge == age

    private fun playTurn(nbPlayers: Int, game: Game, ageToCheck: Int) {
        val turnInfos = game.getCurrentTurnInfo()
        assertEquals(nbPlayers, turnInfos.size)
        turnInfos.forEach {
            assertEquals(ageToCheck, it.currentAge)
        }

        val moveExpectations = turnInfos.mapNotNull { it.firstAvailableMove() }

        moveExpectations.forEach { game.prepareMove(it.playerIndex, it.moveToSend) }
        assertTrue(game.allPlayersPreparedTheirMove())

        game.playTurn()

        val expectedMoves = moveExpectations.map { it.expectedPlayedMove }
        assertEquals(expectedMoves, game.getCurrentTurnInfo()[0].table.lastPlayedMoves)
    }

    private fun PlayerTurnInfo.firstAvailableMove(): MoveExpectation? = when (val a = action) {
        is TurnAction.PlayFromHand -> createPlayCardMove(this, a.hand)
        is TurnAction.PlayFromDiscarded -> createPlayFreeDiscardedCardMove(this, a.discardedCards)
        is TurnAction.PickNeighbourGuild -> createPickGuildMove(this, a.neighbourGuildCards)
        is TurnAction.SayReady,
        is TurnAction.Wait -> null
        is TurnAction.WatchScore -> fail("should not have WATCH_SCORE action before end of game")
    }

    private fun createPlayCardMove(turnInfo: PlayerTurnInfo, hand: List<HandCard>): MoveExpectation {
        val wonderBuildability = turnInfo.wonderBuildability
        if (wonderBuildability.isBuildable) {
            val transactions = wonderBuildability.transactionsOptions.first()
            return planMove(turnInfo, MoveType.UPGRADE_WONDER, hand.first(), transactions)
        }
        val playableCard = hand.firstOrNull { it.playability.isPlayable }
        return if (playableCard != null) {
            planMove(turnInfo, MoveType.PLAY, playableCard, playableCard.playability.transactionOptions.first())
        } else {
            planMove(turnInfo, MoveType.DISCARD, hand.first(), noTransactions())
        }
    }

    private fun createPlayFreeDiscardedCardMove(turn: PlayerTurnInfo, discardedCards: List<HandCard>): MoveExpectation {
        val card = discardedCards.random()
        return MoveExpectation(
            turn.playerIndex,
            PlayerMove(MoveType.PLAY_FREE_DISCARDED, card.name, noTransactions()),
            PlayedMove(turn.playerIndex, MoveType.PLAY_FREE_DISCARDED, card.toPlayedCard(), noTransactions()),
        )
    }

    private fun planMove(
        turnInfo: PlayerTurnInfo,
        moveType: MoveType,
        card: HandCard,
        transactions: ResourceTransactions,
    ): MoveExpectation = MoveExpectation(
        turnInfo.playerIndex,
        PlayerMove(moveType, card.name, transactions),
        PlayedMove(turnInfo.playerIndex, moveType, card.toPlayedCard(), transactions),
    )

    private fun createPickGuildMove(turnInfo: PlayerTurnInfo, neighbourGuilds: List<HandCard>): MoveExpectation {
        // the game should send action WAIT if no guild cards are available around
        assertFalse(neighbourGuilds.isEmpty())
        val card = neighbourGuilds.first()
        return MoveExpectation(
            turnInfo.playerIndex,
            PlayerMove(MoveType.COPY_GUILD, card.name),
            PlayedMove(turnInfo.playerIndex, MoveType.COPY_GUILD, card.toPlayedCard(), noTransactions()),
        )
    }

    data class MoveExpectation(val playerIndex: Int, val moveToSend: PlayerMove, val expectedPlayedMove: PlayedMove)

    private fun HandCard.toPlayedCard(): TableCard =
        TableCard(name, color, requirements, chainParent, chainChildren, image, back, true)
}
