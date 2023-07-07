package org.luxons.sevenwonders.engine.effects

import org.junit.Before
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.test.addCards
import org.luxons.sevenwonders.engine.test.testTable
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.Color
import kotlin.enums.EnumEntries
import kotlin.test.assertEquals

@RunWith(Theories::class)
class BonusPerBoardElementTest {

    private lateinit var table: Table
    private lateinit var player0: Player

    @Before
    fun setUp() {
        table = testTable(4)
        player0 = SimplePlayer(0, table)
    }

    @Theory
    fun computePoints_countsCards(
        boardPosition: RelativeBoardPosition,
        nbCards: Int,
        nbOtherCards: Int,
        points: Int,
        gold: Int,
        color: Color,
    ) {
        val board = table.getBoard(0, boardPosition)
        addCards(board, nbCards, nbOtherCards, color)

        val bonus = BonusPerBoardElement(listOf(boardPosition), BoardElementType.CARD, gold, points, listOf(color))

        assertEquals(nbCards * points, bonus.computePoints(player0))
    }

    @Theory
    fun computePoints_countsDefeatTokens(
        boardPosition: RelativeBoardPosition,
        nbDefeatTokens: Int,
        points: Int,
        gold: Int,
    ) {
        val board = table.getBoard(0, boardPosition)
        repeat(nbDefeatTokens) {
            board.military.defeat()
        }

        val bonus = BonusPerBoardElement(listOf(boardPosition), BoardElementType.DEFEAT_TOKEN, gold, points, listOf())

        assertEquals(nbDefeatTokens * points, bonus.computePoints(player0))
    }

    @Theory
    fun computePoints_countsWonderStages(boardPosition: RelativeBoardPosition, nbStages: Int, points: Int, gold: Int) {
        val board = table.getBoard(0, boardPosition)
        repeat(nbStages) {
            board.wonder.placeCard(CardBack(""))
        }

        val bonus =
            BonusPerBoardElement(listOf(boardPosition), BoardElementType.BUILT_WONDER_STAGES, gold, points, listOf())

        assertEquals(nbStages * points, bonus.computePoints(player0))
    }

    @Theory
    fun apply_countsCards(
        boardPosition: RelativeBoardPosition,
        nbCards: Int,
        nbOtherCards: Int,
        points: Int,
        gold: Int,
        color: Color,
    ) {
        val board = table.getBoard(0, boardPosition)
        addCards(board, nbCards, nbOtherCards, color)

        val bonus = BonusPerBoardElement(listOf(boardPosition), BoardElementType.CARD, gold, points, listOf(color))

        val selfBoard = table.getBoard(0)
        val initialGold = selfBoard.gold
        bonus.applyTo(player0)
        assertEquals(initialGold + nbCards * gold, selfBoard.gold)
    }

    @Theory
    fun apply_countsDefeatTokens(boardPosition: RelativeBoardPosition, nbDefeatTokens: Int, points: Int, gold: Int) {
        val board = table.getBoard(0, boardPosition)
        repeat(nbDefeatTokens) {
            board.military.defeat()
        }

        val bonus = BonusPerBoardElement(listOf(boardPosition), BoardElementType.DEFEAT_TOKEN, gold, points, listOf())

        val selfBoard = table.getBoard(0)
        val initialGold = selfBoard.gold
        bonus.applyTo(player0)
        assertEquals(initialGold + nbDefeatTokens * gold, selfBoard.gold)
    }

    @Theory
    fun apply_countsWonderStages(boardPosition: RelativeBoardPosition, nbStages: Int, points: Int, gold: Int) {
        val board = table.getBoard(0, boardPosition)
        repeat(nbStages) {
            board.wonder.placeCard(CardBack(""))
        }

        val bonus =
            BonusPerBoardElement(listOf(boardPosition), BoardElementType.BUILT_WONDER_STAGES, gold, points, listOf())

        val selfBoard = table.getBoard(0)
        val initialGold = selfBoard.gold
        bonus.applyTo(player0)
        assertEquals(initialGold + nbStages * gold, selfBoard.gold)
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun values(): IntArray = intArrayOf(0, 1, 2, 3)

        @JvmStatic
        @DataPoints
        fun colors(): EnumEntries<Color> = Color.entries

        @JvmStatic
        @DataPoints
        fun positions(): EnumEntries<RelativeBoardPosition> = RelativeBoardPosition.entries
    }
}
