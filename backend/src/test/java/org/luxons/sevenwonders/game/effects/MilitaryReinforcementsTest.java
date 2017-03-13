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
public class MilitaryReinforcementsTest {

    @DataPoints
    public static int[] shieldCounts() {
        return new int[] {0, 1, 2, 3, 5};
    }

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @Theory
    public void apply_increaseGoldWithRightAmount(int initialShields, int additionalShields, ResourceType type) {
        Board board = TestUtils.createBoard(type);
        board.getMilitary().addShields(initialShields);

        MilitaryReinforcements reinforcements = new MilitaryReinforcements(additionalShields);
        reinforcements.apply(board);

        assertEquals(initialShields + additionalShields, board.getMilitary().getNbShields());
    }

    @Theory
    public void computePoints_isAlwaysZero(int shields) {
        MilitaryReinforcements reinforcements = new MilitaryReinforcements(shields);
        Table table = TestUtils.createTable(5);
        assertEquals(0, reinforcements.computePoints(table, 0));
    }

    @Theory
    public void equals_falseWhenNull(int shields) {
        MilitaryReinforcements reinforcements = new MilitaryReinforcements(shields);
        //noinspection ObjectEqualsNull
        assertFalse(reinforcements.equals(null));
    }

    @Theory
    public void equals_falseWhenDifferentClass(int shields) {
        MilitaryReinforcements reinforcements = new MilitaryReinforcements(shields);
        GoldIncrease goldIncrease = new GoldIncrease(shields);
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(reinforcements.equals(goldIncrease));
    }

    @Theory
    public void equals_trueWhenSame(int shields) {
        MilitaryReinforcements reinforcements = new MilitaryReinforcements(shields);
        assertEquals(reinforcements, reinforcements);
    }

    @Theory
    public void equals_trueWhenSameContent(int shields) {
        MilitaryReinforcements reinforcements1 = new MilitaryReinforcements(shields);
        MilitaryReinforcements reinforcements2 = new MilitaryReinforcements(shields);
        assertTrue(reinforcements1.equals(reinforcements2));
    }

    @Theory
    public void hashCode_sameWhenSameContent(int shields) {
        MilitaryReinforcements reinforcements1 = new MilitaryReinforcements(shields);
        MilitaryReinforcements reinforcements2 = new MilitaryReinforcements(shields);
        assertEquals(reinforcements1.hashCode(), reinforcements2.hashCode());
    }
}
