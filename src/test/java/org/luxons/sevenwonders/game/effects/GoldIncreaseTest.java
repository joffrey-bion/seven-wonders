package org.luxons.sevenwonders.game.effects;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class GoldIncreaseTest {

    @DataPoints
    public static int[] goldAmounts() {
        return new int[]{-5, -1, 0, 1, 2, 5, 10};
    }

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @Theory
    public void apply_increaseGoldWithRightAmount(int initialAmount, int goldIncreaseAmount, ResourceType type) {
        Board board = TestUtils.createBoard(type, initialAmount);
        GoldIncrease goldIncrease = new GoldIncrease(goldIncreaseAmount);

        goldIncrease.apply(board);

        assertEquals(initialAmount + goldIncreaseAmount, board.getGold());
    }
}