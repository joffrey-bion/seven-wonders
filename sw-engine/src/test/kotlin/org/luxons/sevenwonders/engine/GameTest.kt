package org.luxons.sevenwonders.engine

import org.luxons.sevenwonders.model.Action
import org.luxons.sevenwonders.model.PlayedMove
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.engine.data.GameDefinition
import org.luxons.sevenwonders.engine.data.LAST_AGE
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.engine.test.testCustomizableSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
        turnInfos.forEach {
            assertEquals(ageToCheck, it.currentAge)
            assertEquals(handSize, it.hand.size)
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
        Action.PICK_NEIGHBOR_GUILD -> createPickGuildMove(this)
        Action.WAIT -> null
    }

    private fun createPlayCardMove(turnInfo: PlayerTurnInfo): MoveExpectation {
        val wonderBuildability = turnInfo.wonderBuildability
        if (wonderBuildability.isBuildable) {
            val transactions = wonderBuildability.cheapestTransactions.first()
            return planMove(turnInfo, MoveType.UPGRADE_WONDER, turnInfo.hand.first(), transactions)
        }
        val playableCard = turnInfo.hand.firstOrNull { it.playability.isPlayable }
        return if (playableCard != null) {
            planMove(turnInfo, MoveType.PLAY, playableCard, playableCard.playability.cheapestTransactions.first())
        } else {
            planMove(turnInfo, MoveType.DISCARD, turnInfo.hand.first(),
                noTransactions()
            )
        }
    }

    private fun createPickGuildMove(turnInfo: PlayerTurnInfo): MoveExpectation {
        val neighbourGuilds = turnInfo.neighbourGuildCards

        // the game should send action WAIT if no guild cards are available around
        assertFalse(neighbourGuilds.isEmpty())
        return MoveExpectation(
            turnInfo.playerIndex,
            PlayerMove(
                MoveType.COPY_GUILD,
                neighbourGuilds.first().name
            ),
            PlayedMove(
                turnInfo.playerIndex,
                MoveType.COPY_GUILD,
                neighbourGuilds.first(),
                noTransactions()
            )
        )
    }

    data class MoveExpectation(val playerIndex: Int, val moveToSend: PlayerMove, val expectedPlayedMove: PlayedMove)

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

    private fun HandCard.toPlayedCard(): TableCard =
        TableCard(
            name, color, requirements, chainParent, chainChildren, image, back, true
        )
}
