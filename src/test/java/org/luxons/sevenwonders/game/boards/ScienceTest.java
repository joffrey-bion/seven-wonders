package org.luxons.sevenwonders.game.boards;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

@RunWith(Theories.class)
public class ScienceTest {

    @DataPoints
    public static int[][] expectations() {
        // compasses, wheels, tablets, jokers, expected points
        return new int[][]{
                {0, 0, 0, 1, 1},
                {0, 0, 1, 0, 1},
                {0, 0, 0, 2, 4},
                {0, 0, 2, 0, 4},
                {0, 0, 0, 3, 10},
                {1, 1, 1, 0, 10},
                {0, 0, 0, 4, 16},
                {0, 0, 4, 0, 16}};
    }

    @DataPoints
    public static Iterable<Science> dataPoints() {
        List<Science> dataPoints = new ArrayList<>();
        for (int c = 0; c < 5; c++) {
            for (int w = 0; w < 5; w++) {
                for (int t = 0; t < 5; t++) {
                    for (int j = 0; j < 5; j++) {
                        dataPoints.add(science(c, w, t, j));
                    }
                }
            }
        }
        return dataPoints;
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
    public void computePoints_compassesOnly_noJoker(Science science) {
        assumeThat(science.getJokers(), is(0));
        assumeThat(science.getQuantity(ScienceType.WHEEL), is(0));
        assumeThat(science.getQuantity(ScienceType.TABLET), is(0));
        int compasses = science.getQuantity(ScienceType.COMPASS);
        assertEquals(compasses * compasses, science.computePoints());
    }

    @Theory
    public void computePoints_wheelsOnly_noJoker(Science science) {
        assumeThat(science.getJokers(), is(0));
        assumeThat(science.getQuantity(ScienceType.COMPASS), is(0));
        assumeThat(science.getQuantity(ScienceType.TABLET), is(0));
        int wheels = science.getQuantity(ScienceType.WHEEL);
        assertEquals(wheels * wheels, science.computePoints());
    }

    @Theory
    public void computePoints_tabletsOnly_noJoker(Science science) {
        assumeThat(science.getJokers(), is(0));
        assumeThat(science.getQuantity(ScienceType.COMPASS), is(0));
        assumeThat(science.getQuantity(ScienceType.WHEEL), is(0));
        int tablets = science.getQuantity(ScienceType.TABLET);
        assertEquals(tablets * tablets, science.computePoints());
    }

    @Theory
    public void computePoints_allSameNoJoker(Science science) {
        assumeThat(science.getJokers(), is(0));
        int compasses = science.getQuantity(ScienceType.COMPASS);
        int wheels = science.getQuantity(ScienceType.WHEEL);
        int tablets = science.getQuantity(ScienceType.TABLET);
        assumeThat(compasses, is(wheels));
        assumeThat(compasses, is(tablets));
        assertEquals(3 * compasses * compasses + 7 * compasses, science.computePoints());
    }

    @Theory
    public void computePoints_expectation(int[] expectation) {
        Science science = science(expectation[0], expectation[1], expectation[2], expectation[3]);
        assertEquals(expectation[4], science.computePoints());
    }
}