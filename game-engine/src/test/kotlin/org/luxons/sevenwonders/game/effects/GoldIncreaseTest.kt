package org.luxons.sevenwonders.game.effects

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.test.*

import org.junit.Assert.assertEquals

@RunWith(Theories::class)
class GoldIncreaseTest {

    @Theory
    fun apply_increaseGoldWithRightAmount(initialAmount: Int, goldIncreaseAmount: Int, type: ResourceType) {
        val board = testBoard(type, initialAmount)
        val goldIncrease = GoldIncrease(goldIncreaseAmount)

        goldIncrease.apply(board)

        assertEquals((initialAmount + goldIncreaseAmount).toLong(), board.gold.toLong())
    }

    @Theory
    fun computePoints_isAlwaysZero(gold: Int) {
        val goldIncrease = GoldIncrease(gold)
        val table = testTable(5)
        assertEquals(0, goldIncrease.computePoints(table, 0).toLong())
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun goldAmounts(): IntArray {
            return intArrayOf(-5, -1, 0, 1, 2, 5, 10)
        }

        @JvmStatic
        @DataPoints
        fun resourceTypes(): Array<ResourceType> {
            return ResourceType.values()
        }
    }
}