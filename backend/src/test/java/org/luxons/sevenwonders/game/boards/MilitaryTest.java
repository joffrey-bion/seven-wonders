package org.luxons.sevenwonders.game.boards;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.boards.Military.UnknownAgeException;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class MilitaryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @DataPoints("points")
    public static int[] points() {
        return new int[] {0, 1, 3, 5};
    }

    @DataPoints("ages")
    public static int[] ages() {
        return new int[] {1, 2, 3};
    }

    private static Military createMilitary(int age, int nbPointsPerVictory, int nbPointsPerDefeat) {
        Map<Integer, Integer> wonPointsPerAge = new HashMap<>();
        wonPointsPerAge.put(age, nbPointsPerVictory);
        return new Military(nbPointsPerDefeat, wonPointsPerAge);
    }

    @Theory
    public void victory_addsCorrectPoints(@FromDataPoints("ages") int age,
            @FromDataPoints("points") int nbPointsPerVictory) {
        Military military = createMilitary(age, nbPointsPerVictory, 0);
        int initialPoints = military.getTotalPoints();

        military.victory(age);
        assertEquals(initialPoints + nbPointsPerVictory, military.getTotalPoints());
    }

    @Theory
    public void victory_failsIfUnknownAge(@FromDataPoints("points") int nbPointsPerVictory) {
        Military military = createMilitary(0, nbPointsPerVictory, 0);
        thrown.expect(UnknownAgeException.class);
        military.victory(1);
    }

    @Theory
    public void defeat_removesCorrectPoints(@FromDataPoints("points") int nbPointsLostPerDefeat) {
        Military military = createMilitary(0, 0, nbPointsLostPerDefeat);
        int initialPoints = military.getTotalPoints();

        military.defeat();
        assertEquals(initialPoints - nbPointsLostPerDefeat, military.getTotalPoints());
    }
}
