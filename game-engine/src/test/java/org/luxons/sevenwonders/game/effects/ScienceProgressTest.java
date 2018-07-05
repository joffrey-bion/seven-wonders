package org.luxons.sevenwonders.game.effects;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.Science;
import org.luxons.sevenwonders.game.boards.ScienceType;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class ScienceProgressTest {

    @DataPoints
    public static int[] elementsCount() {
        return new int[] {0, 1, 2};
    }

    @Theory
    public void apply_initContainsAddedScience(int initCompasses, int initWheels, int initTablets, int initJokers,
            int compasses, int wheels, int tablets, int jokers) {
        Board board = TestUtilsKt.testBoard(ResourceType.ORE);
        Science initialScience = TestUtilsKt.createScience(initCompasses, initWheels, initTablets, initJokers);
        board.getScience().addAll(initialScience);

        ScienceProgress effect = TestUtilsKt.createScienceProgress(compasses, wheels, tablets, jokers);
        effect.apply(board);

        assertEquals(initCompasses + compasses, board.getScience().getQuantity(ScienceType.COMPASS));
        assertEquals(initWheels + wheels, board.getScience().getQuantity(ScienceType.WHEEL));
        assertEquals(initTablets + tablets, board.getScience().getQuantity(ScienceType.TABLET));
        assertEquals(initJokers + jokers, board.getScience().getJokers());
    }
}
