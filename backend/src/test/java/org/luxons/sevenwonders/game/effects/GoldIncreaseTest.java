package org.luxons.sevenwonders.game.effects;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class GoldIncreaseTest {

    @DataPoints
    public static int[] goldAmounts() {
        return new int[] {-5, -1, 0, 1, 2, 5, 10};
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

    @Theory
    public void computePoints_isAlwaysZero(int gold) {
        GoldIncrease goldIncrease = new GoldIncrease(gold);
        Table table = TestUtils.createTable(5);
        assertEquals(0, goldIncrease.computePoints(table, 0));
    }

    @Theory
    public void equals_falseWhenNull(int gold) {
        GoldIncrease goldIncrease = new GoldIncrease(gold);
        //noinspection ObjectEqualsNull
        assertFalse(goldIncrease.equals(null));
    }

    @Theory
    public void equals_falseWhenDifferentClass(int gold) {
        GoldIncrease goldIncrease = new GoldIncrease(gold);
        MilitaryReinforcements reinforcements = new MilitaryReinforcements(gold);
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(goldIncrease.equals(reinforcements));
    }

    @Theory
    public void equals_trueWhenSame(int gold) {
        GoldIncrease goldIncrease = new GoldIncrease(gold);
        assertEquals(goldIncrease, goldIncrease);
    }

    @Theory
    public void equals_trueWhenSameContent(int gold) {
        GoldIncrease goldIncrease1 = new GoldIncrease(gold);
        GoldIncrease goldIncrease2 = new GoldIncrease(gold);
        assertTrue(goldIncrease1.equals(goldIncrease2));
    }

    @Theory
    public void hashCode_sameWhenSameContent(int gold) {
        GoldIncrease goldIncrease1 = new GoldIncrease(gold);
        GoldIncrease goldIncrease2 = new GoldIncrease(gold);
        assertEquals(goldIncrease1.hashCode(), goldIncrease2.hashCode());
    }
}
