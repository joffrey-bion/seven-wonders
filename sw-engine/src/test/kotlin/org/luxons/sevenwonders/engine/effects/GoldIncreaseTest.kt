package org.luxons.sevenwonders.engine.effects

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.engine.test.testBoard
import org.luxons.sevenwonders.engine.test.testTable
import org.luxons.sevenwonders.model.resources.ResourceType
import kotlin.test.assertEquals

@RunWith(Theories::class)
class GoldIncreaseTest {

    @Theory
    fun apply_increaseGoldWithRightAmount(initialAmount: Int, goldIncreaseAmount: Int, type: ResourceType) {
        val board = testBoard(type, initialAmount)
        val goldIncrease = GoldIncrease(goldIncreaseAmount)

        goldIncrease.applyTo(board)

        assertEquals(initialAmount + goldIncreaseAmount, board.gold)
    }

    @Theory
    fun computePoints_isAlwaysZero(gold: Int) {
        val goldIncrease = GoldIncrease(gold)
        val player = SimplePlayer(0, testTable(5))
        assertEquals(0, goldIncrease.computePoints(player))
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun goldAmounts(): IntArray = intArrayOf(-5, -1, 0, 1, 2, 5, 10)

        @JvmStatic
        @DataPoints
        fun resourceTypes(): Array<ResourceType> = ResourceType.values()
    }
}
