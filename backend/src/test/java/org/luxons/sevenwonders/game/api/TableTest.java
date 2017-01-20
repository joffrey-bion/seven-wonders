package org.luxons.sevenwonders.game.api;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class TableTest {

    @DataPoints
    public static int[] nbPlayers() {
        return new int[] {2, 3, 4, 5, 6, 7, 8};
    }

    @Theory
    public void getBoard_wrapLeft(int nbPlayers) {
        assumeTrue(nbPlayers >= 2);
        Table table = TestUtils.createTable(nbPlayers);
        int last = nbPlayers - 1;
        assertEquals(table.getBoard(last), table.getBoard(0, RelativeBoardPosition.LEFT));
        assertEquals(table.getBoard(0), table.getBoard(0, RelativeBoardPosition.SELF));
        assertEquals(table.getBoard(1), table.getBoard(0, RelativeBoardPosition.RIGHT));
    }

    @Theory
    public void getBoard_wrapRight(int nbPlayers) {
        assumeTrue(nbPlayers >= 2);
        Table table = TestUtils.createTable(nbPlayers);
        int last = nbPlayers - 1;
        assertEquals(table.getBoard(last - 1), table.getBoard(last, RelativeBoardPosition.LEFT));
        assertEquals(table.getBoard(last), table.getBoard(last, RelativeBoardPosition.SELF));
        assertEquals(table.getBoard(0), table.getBoard(last, RelativeBoardPosition.RIGHT));
    }

    @Theory
    public void getBoard_noWrap(int nbPlayers) {
        assumeTrue(nbPlayers >= 3);
        Table table = TestUtils.createTable(nbPlayers);
        assertEquals(table.getBoard(0), table.getBoard(1, RelativeBoardPosition.LEFT));
        assertEquals(table.getBoard(1), table.getBoard(1, RelativeBoardPosition.SELF));
        assertEquals(table.getBoard(2), table.getBoard(1, RelativeBoardPosition.RIGHT));
    }
}