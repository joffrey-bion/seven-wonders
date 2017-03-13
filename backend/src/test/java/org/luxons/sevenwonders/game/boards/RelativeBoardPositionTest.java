package org.luxons.sevenwonders.game.boards;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class RelativeBoardPositionTest {

    @DataPoints
    public static int[] nbPlayers() {
        return new int[] {1, 2, 3, 5, 7, 9};
    }

    @Theory
    public void getIndexFrom_wrapLeft(int nbPlayers) {
        assumeTrue(nbPlayers >= 2);
        int last = nbPlayers - 1;
        assertEquals(last, RelativeBoardPosition.LEFT.getIndexFrom(0, nbPlayers));
        assertEquals(0, RelativeBoardPosition.SELF.getIndexFrom(0, nbPlayers));
        assertEquals(1, RelativeBoardPosition.RIGHT.getIndexFrom(0, nbPlayers));
    }

    @Theory
    public void getIndexFrom_wrapRight(int nbPlayers) {
        assumeTrue(nbPlayers >= 2);
        int last = nbPlayers - 1;
        assertEquals(last - 1, RelativeBoardPosition.LEFT.getIndexFrom(last, nbPlayers));
        assertEquals(last, RelativeBoardPosition.SELF.getIndexFrom(last, nbPlayers));
        assertEquals(0, RelativeBoardPosition.RIGHT.getIndexFrom(last, nbPlayers));
    }

    @Theory
    public void getIndexFrom_noWrap(int nbPlayers) {
        assumeTrue(nbPlayers >= 3);
        assertEquals(0, RelativeBoardPosition.LEFT.getIndexFrom(1, nbPlayers));
        assertEquals(1, RelativeBoardPosition.SELF.getIndexFrom(1, nbPlayers));
        assertEquals(2, RelativeBoardPosition.RIGHT.getIndexFrom(1, nbPlayers));
    }
}
