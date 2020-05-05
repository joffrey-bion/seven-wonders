package org.luxons.sevenwonders.engine.boards

import junit.framework.TestCase.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.boards.Board.InsufficientFundsException
import org.luxons.sevenwonders.engine.effects.RawPointsIncrease
import org.luxons.sevenwonders.engine.effects.SpecialAbility
import org.luxons.sevenwonders.engine.effects.SpecialAbilityActivation
import org.luxons.sevenwonders.engine.resources.resourcesOf
import org.luxons.sevenwonders.engine.test.addCards
import org.luxons.sevenwonders.engine.test.getDifferentColorFrom
import org.luxons.sevenwonders.engine.test.playCardWithEffect
import org.luxons.sevenwonders.engine.test.singleBoardPlayer
import org.luxons.sevenwonders.engine.test.testBoard
import org.luxons.sevenwonders.engine.test.testCard
import org.luxons.sevenwonders.engine.test.testSettings
import org.luxons.sevenwonders.engine.test.testWonder
import org.luxons.sevenwonders.model.cards.Color
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.score.ScoreCategory
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(Theories::class)
class BoardTest {

    @Theory
    fun initialGold_respectsSettings(@FromDataPoints("gold") goldAmountInSettings: Int) {
        val settings = testSettings(initialGold = goldAmountInSettings)
        val board = Board(testWonder(), 0, settings)
        assertEquals(goldAmountInSettings, board.gold)
    }

    @Theory
    fun initialProduction_containsInitialResource(type: ResourceType) {
        val board = Board(testWonder(type), 0, testSettings())
        val resources = resourcesOf(type)
        assertTrue(board.production.contains(resources))
        assertTrue(board.publicProduction.contains(resources))
    }

    @Theory
    fun removeGold_successfulWhenNotTooMuch(
        @FromDataPoints("gold") initialGold: Int,
        @FromDataPoints("gold") goldRemoved: Int
    ) {
        assumeTrue(goldRemoved >= 0)
        assumeTrue(initialGold >= goldRemoved)

        val board = Board(testWonder(), 0, testSettings(initialGold = initialGold))
        board.removeGold(goldRemoved)
        assertEquals(initialGold - goldRemoved, board.gold)
    }

    @Theory
    fun removeGold_failsWhenTooMuch(
        @FromDataPoints("gold") initialGold: Int,
        @FromDataPoints("gold") goldRemoved: Int
    ) {
        assumeTrue(goldRemoved >= 0)
        assumeTrue(initialGold < goldRemoved)

        assertFailsWith<InsufficientFundsException> {
            val board = Board(testWonder(), 0, testSettings(initialGold = initialGold))
            board.removeGold(goldRemoved)
        }
    }

    @Theory
    fun getNbCardsOfColor_properCount_singleColor(
        type: ResourceType,
        @FromDataPoints("nbCards") nbCards: Int,
        @FromDataPoints("nbCards") nbOtherCards: Int,
        color: Color
    ) {
        val board = testBoard(initialResource = type)
        addCards(board, nbCards, nbOtherCards, color)
        assertEquals(nbCards, board.getNbCardsOfColor(listOf(color)))
    }

    @Theory
    fun getNbCardsOfColor_properCount_multiColors(
        type: ResourceType,
        @FromDataPoints("nbCards") nbCards1: Int,
        @FromDataPoints("nbCards") nbCards2: Int,
        @FromDataPoints("nbCards") nbOtherCards: Int,
        color1: Color,
        color2: Color
    ) {
        val board = testBoard(initialResource = type)
        addCards(board, nbCards1, color1)
        addCards(board, nbCards2, color2)
        addCards(board, nbOtherCards, getDifferentColorFrom(color1, color2))
        assertEquals(nbCards1 + nbCards2, board.getNbCardsOfColor(listOf(color1, color2)))
    }

    @Test
    fun setCopiedGuild_succeedsOnPurpleCard() {
        val board = testBoard()
        val card = testCard(color = Color.PURPLE)

        board.copiedGuild = card
        assertSame(card, board.copiedGuild)
    }

    @Theory
    fun setCopiedGuild_failsOnNonPurpleCard(color: Color) {
        assumeTrue(color !== Color.PURPLE)
        val board = testBoard()
        val card = testCard(color = color)

        assertFailsWith<IllegalArgumentException> {
            board.copiedGuild = card
        }
    }

    @Theory
    fun hasSpecial(applied: SpecialAbility, tested: SpecialAbility) {
        val board = testBoard()
        val special = SpecialAbilityActivation(applied)

        special.applyTo(singleBoardPlayer(board))

        assertEquals(applied === tested, board.hasSpecial(tested))
    }

    @Test
    fun canPlayFreeCard() {
        val board = testBoard()
        val special = SpecialAbilityActivation(SpecialAbility.ONE_FREE_PER_AGE)

        special.applyTo(singleBoardPlayer(board))

        assertTrue(board.canPlayFreeCard(0))
        assertTrue(board.canPlayFreeCard(1))
        assertTrue(board.canPlayFreeCard(2))

        board.consumeFreeCard(0)

        assertFalse(board.canPlayFreeCard(0))
        assertTrue(board.canPlayFreeCard(1))
        assertTrue(board.canPlayFreeCard(2))

        board.consumeFreeCard(1)

        assertFalse(board.canPlayFreeCard(0))
        assertFalse(board.canPlayFreeCard(1))
        assertTrue(board.canPlayFreeCard(2))

        board.consumeFreeCard(2)

        assertFalse(board.canPlayFreeCard(0))
        assertFalse(board.canPlayFreeCard(1))
        assertFalse(board.canPlayFreeCard(2))
    }

    @Theory
    fun computePoints_gold(@FromDataPoints("gold") gold: Int) {
        assumeTrue(gold >= 0)
        val board = testBoard(initialGold = gold)

        val score = board.computeScore(singleBoardPlayer(board))
        assertEquals(gold / 3, score.pointsByCategory[ScoreCategory.GOLD])
        assertEquals(gold / 3, score.totalPoints)
    }

    @Theory
    fun computePoints_goldAndPoints(@FromDataPoints("gold") gold: Int) {
        assumeTrue(gold >= 0)
        val board = testBoard(initialGold = gold)

        val effect = RawPointsIncrease(5)
        playCardWithEffect(singleBoardPlayer(board), Color.BLUE, effect)

        val score = board.computeScore(singleBoardPlayer(board))
        assertEquals(gold / 3, score.pointsByCategory[ScoreCategory.GOLD])
        assertEquals(5, score.pointsByCategory[ScoreCategory.CIVIL])
        assertEquals(5 + gold / 3, score.totalPoints)
    }

    companion object {

        @JvmStatic
        @DataPoints("gold")
        fun goldAmounts(): IntArray = intArrayOf(-3, -1, 0, 1, 2, 3)

        @JvmStatic
        @DataPoints("nbCards")
        fun nbCards(): IntArray = intArrayOf(0, 1, 2)

        @JvmStatic
        @DataPoints
        fun resourceTypes(): Array<ResourceType> = ResourceType.values()

        @JvmStatic
        @DataPoints
        fun colors(): Array<Color> = Color.values()

        @JvmStatic
        @DataPoints
        fun specialAbilities(): Array<SpecialAbility> = SpecialAbility.values()
    }
}
