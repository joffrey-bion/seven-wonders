package org.luxons.sevenwonders.game.effects;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class RawPointsIncreaseTest {

    @DataPoints
    public static int[] points() {
        return new int[] {0, 1, 2, 3, 5};
    }

    @Theory
    public void computePoints_equalsNbOfPoints(int points) {
        RawPointsIncrease rawPointsIncrease = new RawPointsIncrease(points);
        Table table = TestUtils.createTable(5);
        assertEquals(points, rawPointsIncrease.computePoints(table, 0));
    }

    @Theory
    public void equals_falseWhenNull(int points) {
        RawPointsIncrease rawPointsIncrease = new RawPointsIncrease(points);
        //noinspection ObjectEqualsNull
        assertFalse(rawPointsIncrease.equals(null));
    }

    @Theory
    public void equals_falseWhenDifferentClass(int points) {
        RawPointsIncrease rawPointsIncrease = new RawPointsIncrease(points);
        GoldIncrease goldIncrease = new GoldIncrease(points);
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(rawPointsIncrease.equals(goldIncrease));
    }

    @Theory
    public void equals_trueWhenSame(int points) {
        RawPointsIncrease rawPointsIncrease = new RawPointsIncrease(points);
        assertEquals(rawPointsIncrease, rawPointsIncrease);
    }

    @Theory
    public void equals_trueWhenSameContent(int points) {
        RawPointsIncrease rawPointsIncrease1 = new RawPointsIncrease(points);
        RawPointsIncrease rawPointsIncrease2 = new RawPointsIncrease(points);
        assertTrue(rawPointsIncrease1.equals(rawPointsIncrease2));
    }

    @Theory
    public void hashCode_sameWhenSameContent(int points) {
        RawPointsIncrease rawPointsIncrease1 = new RawPointsIncrease(points);
        RawPointsIncrease rawPointsIncrease2 = new RawPointsIncrease(points);
        assertEquals(rawPointsIncrease1.hashCode(), rawPointsIncrease2.hashCode());
    }
}
