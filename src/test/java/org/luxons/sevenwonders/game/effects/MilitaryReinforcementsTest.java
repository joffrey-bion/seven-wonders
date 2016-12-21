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
public class MilitaryReinforcementsTest {

    @DataPoints
    public static int[] shieldCounts() {
        return new int[]{0, 1, 2, 3, 5};
    }

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @Theory
    public void apply_increaseGoldWithRightAmount(int initialShields, int additionalShields, ResourceType type) {
        Board board = TestUtils.createBoard(type);
        board.setNbWarSymbols(initialShields);

        MilitaryReinforcements reinforcements = new MilitaryReinforcements(additionalShields);

        reinforcements.apply(board);

        assertEquals(initialShields + additionalShields, board.getNbWarSymbols());
    }

}