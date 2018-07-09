package org.luxons.sevenwonders.game.boards

import junit.framework.TestCase.assertEquals
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board.InsufficientFundsException
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.effects.RawPointsIncrease
import org.luxons.sevenwonders.game.effects.SpecialAbility
import org.luxons.sevenwonders.game.effects.SpecialAbilityActivation
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.scoring.ScoreCategory
import org.luxons.sevenwonders.game.test.addCards
import org.luxons.sevenwonders.game.test.createResources
import org.luxons.sevenwonders.game.test.getDifferentColorFrom
import org.luxons.sevenwonders.game.test.playCardWithEffect
import org.luxons.sevenwonders.game.test.testBoard
import org.luxons.sevenwonders.game.test.testCard
import org.luxons.sevenwonders.game.test.testCustomizableSettings
import org.luxons.sevenwonders.game.test.testWonder

@RunWith(Theories::class)
class BoardTest {

    @JvmField
    @Rule
    var thrown: ExpectedException = ExpectedException.none()

    @Theory
    fun initialGold_respectsSettings(@FromDataPoints("gold") goldAmountInSettings: Int) {
        val customSettings = testCustomizableSettings(goldAmountInSettings)
        val settings = Settings(5, customSettings)
        val board = Board(testWonder(), 0, settings)
        assertEquals(goldAmountInSettings, board.gold)
    }

    @Theory
    fun initialProduction_containsInitialResource(type: ResourceType) {
        val board = Board(testWonder(type), 0, Settings(5))
        val resources = createResources(type)
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
        val board = Board(testWonder(), 0, Settings(5))
        board.gold = initialGold
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
        thrown.expect(InsufficientFundsException::class.java)
        val board = Board(testWonder(), 0, Settings(5))
        board.gold = initialGold
        board.removeGold(goldRemoved)
    }

    @Theory
    fun getNbCardsOfColor_properCount_singleColor(
        type: ResourceType, @FromDataPoints("nbCards") nbCards: Int,
        @FromDataPoints("nbCards") nbOtherCards: Int, color: Color
    ) {
        val board = testBoard(type)
        addCards(board, nbCards, nbOtherCards, color)
        assertEquals(nbCards, board.getNbCardsOfColor(listOf(color)))
    }

    @Theory
    fun getNbCardsOfColor_properCount_multiColors(
        type: ResourceType, @FromDataPoints("nbCards") nbCards1: Int,
        @FromDataPoints("nbCards") nbCards2: Int,
        @FromDataPoints("nbCards") nbOtherCards: Int, color1: Color,
        color2: Color
    ) {
        val board = testBoard(type)
        addCards(board, nbCards1, color1)
        addCards(board, nbCards2, color2)
        addCards(board, nbOtherCards, getDifferentColorFrom(color1, color2))
        assertEquals(nbCards1 + nbCards2, board.getNbCardsOfColor(listOf(color1, color2)))
    }

    @Test
    fun setCopiedGuild_succeedsOnPurpleCard() {
        val board = testBoard(ResourceType.CLAY)
        val card = testCard(Color.PURPLE)

        board.copiedGuild = card
        assertSame(card, board.copiedGuild)
    }

    @Theory
    fun setCopiedGuild_failsOnNonPurpleCard(color: Color) {
        assumeTrue(color !== Color.PURPLE)
        val board = testBoard(ResourceType.CLAY)
        val card = testCard(color)

        thrown.expect(IllegalArgumentException::class.java)
        board.copiedGuild = card
    }

    @Theory
    fun hasSpecial(applied: SpecialAbility, tested: SpecialAbility) {
        val board = testBoard(ResourceType.CLAY)
        val table = Table(listOf(board))
        val special = SpecialAbilityActivation(applied)

        special.apply(table, 0)

        assertEquals(applied === tested, board.hasSpecial(tested))
    }

    @Test
    fun canPlayFreeCard() {
        val board = testBoard(ResourceType.CLAY)
        val table = Table(listOf(board))
        val special = SpecialAbilityActivation(SpecialAbility.ONE_FREE_PER_AGE)

        special.apply(table, 0)

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
        val board = testBoard(ResourceType.WOOD)
        val table = Table(listOf(board))
        board.gold = gold

        val score = board.computePoints(table)
        assertEquals(gold / 3, score.getPoints(ScoreCategory.GOLD))
        assertEquals(gold / 3, score.totalPoints)
    }

    @Theory
    fun computePoints_(@FromDataPoints("gold") gold: Int) {
        assumeTrue(gold >= 0)
        val board = testBoard(ResourceType.WOOD)
        val table = Table(listOf(board))
        board.gold = gold

        val effect = RawPointsIncrease(5)
        playCardWithEffect(table, 0, Color.BLUE, effect)

        val score = board.computePoints(table)
        assertEquals(gold / 3, score.getPoints(ScoreCategory.GOLD))
        assertEquals(5, score.getPoints(ScoreCategory.CIVIL))
        assertEquals(5 + gold / 3, score.totalPoints)
    }

    companion object {

        @JvmStatic
        @DataPoints("gold")
        fun goldAmounts(): IntArray {
            return intArrayOf(-3, -1, 0, 1, 2, 3)
        }

        @JvmStatic
        @DataPoints("nbCards")
        fun nbCards(): IntArray {
            return intArrayOf(0, 1, 2)
        }

        @JvmStatic
        @DataPoints
        fun resourceTypes(): Array<ResourceType> {
            return ResourceType.values()
        }

        @JvmStatic
        @DataPoints
        fun colors(): Array<Color> {
            return Color.values()
        }

        @JvmStatic
        @DataPoints
        fun specialAbilities(): Array<SpecialAbility> {
            return SpecialAbility.values()
        }
    }
}
