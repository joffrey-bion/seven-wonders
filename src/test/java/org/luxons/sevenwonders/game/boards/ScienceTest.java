package org.luxons.sevenwonders.game.boards;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class ScienceTest {

    @DataPoints
    public static int[][] quantitiesWithExpectedPoints() {
        // compasses, wheels, tablets, jokers, expected points
        return new int[][]{
                {0, 0, 0, 1, 1},
                {0, 0, 1, 0, 1},
                {0, 0, 0, 2, 4},
                {0, 0, 1, 1, 4},
                {0, 0, 2, 0, 4},
                {0, 0, 0, 3, 10},
                {0, 0, 1, 2, 10},
                {0, 1, 1, 1, 10},
                {1, 1, 1, 0, 10},
                {0, 0, 0, 4, 16},
                {0, 0, 1, 3, 16},
                {0, 0, 2, 2, 16},
                {0, 0, 3, 1, 16},
                {0, 0, 4, 0, 16}};
    }

    @DataPoints
    public static int[] quantitiesDataPoints() {
        return new int[] {0, 1, 3, 5, 8};
    }

    private static Science science(int compasses, int wheels, int tablets, int jokers) {
        Science science = new Science();
        science.add(ScienceType.COMPASS, compasses);
        science.add(ScienceType.WHEEL, wheels);
        science.add(ScienceType.TABLET, tablets);
        science.addJoker(jokers);
        return science;
    }

    @Test
    public void addAll_empty() {
        Science initial = science(3, 4, 5, 1);
        Science empty = new Science();
        initial.addAll(empty);
        assertEquals(3, initial.getQuantity(ScienceType.COMPASS));
        assertEquals(4, initial.getQuantity(ScienceType.WHEEL));
        assertEquals(5, initial.getQuantity(ScienceType.TABLET));
        assertEquals(1, initial.getJokers());
    }

    @Test
    public void addAll_noJoker() {
        Science initial = science(3, 4, 5, 1);
        Science other = science(1, 2, 3, 0);
        initial.addAll(other);
        assertEquals(4, initial.getQuantity(ScienceType.COMPASS));
        assertEquals(6, initial.getQuantity(ScienceType.WHEEL));
        assertEquals(8, initial.getQuantity(ScienceType.TABLET));
        assertEquals(1, initial.getJokers());
    }

    @Test
    public void addAll_withJokers() {
        Science initial = science(3, 4, 5, 1);
        Science other = science(0, 0, 0, 3);
        initial.addAll(other);
        assertEquals(3, initial.getQuantity(ScienceType.COMPASS));
        assertEquals(4, initial.getQuantity(ScienceType.WHEEL));
        assertEquals(5, initial.getQuantity(ScienceType.TABLET));
        assertEquals(4, initial.getJokers());
    }

    @Test
    public void addAll_mixed() {
        Science initial = science(3, 4, 5, 1);
        Science other = science(1, 2, 3, 4);
        initial.addAll(other);
        assertEquals(4, initial.getQuantity(ScienceType.COMPASS));
        assertEquals(6, initial.getQuantity(ScienceType.WHEEL));
        assertEquals(8, initial.getQuantity(ScienceType.TABLET));
        assertEquals(5, initial.getJokers());
    }

    @Theory
    public void computePoints_compassesOnly_noJoker(int compasses) {
        Science science = science(compasses, 0, 0, 0);
        assertEquals(compasses * compasses, science.computePoints());
    }

    @Theory
    public void computePoints_wheelsOnly_noJoker(int wheels) {
        Science science = science(0, wheels, 0, 0);
        assertEquals(wheels * wheels, science.computePoints());
    }

    @Theory
    public void computePoints_tabletsOnly_noJoker(int tablets) {
        Science science = science(0, 0, tablets, 0);
        assertEquals(tablets * tablets, science.computePoints());
    }

    @Theory
    public void computePoints_allSameNoJoker(int eachSymbol) {
        Science science = science(eachSymbol, eachSymbol, eachSymbol, 0);
        assertEquals(3 * eachSymbol * eachSymbol + 7 * eachSymbol, science.computePoints());
    }

    @Theory
    public void computePoints_expectation(int[] expectation) {
        Science science = science(expectation[0], expectation[1], expectation[2], expectation[3]);
        assertEquals(expectation[4], science.computePoints());
    }
}