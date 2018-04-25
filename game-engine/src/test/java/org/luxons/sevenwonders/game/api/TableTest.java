package org.luxons.sevenwonders.game.api;

import java.util.List;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

    @Theory
    public void getNeighbourGuildCards(int nbPlayers) {
        assumeTrue(nbPlayers >= 4);
        Table table = TestUtils.createTable(nbPlayers);
        List<Card> guildCards = TestUtils.createGuildCards(4);
        table.getBoard(0).getPlayedCards().add(guildCards.get(0));
        table.getBoard(0).getPlayedCards().add(guildCards.get(1));
        table.getBoard(1).getPlayedCards().add(guildCards.get(2));
        table.getBoard(2).getPlayedCards().add(guildCards.get(3));

        List<Card> neightbourCards0 = table.getNeighbourGuildCards(0);
        assertEquals(1, neightbourCards0.size());
        assertFalse(neightbourCards0.contains(guildCards.get(0)));
        assertFalse(neightbourCards0.contains(guildCards.get(1)));
        assertTrue(neightbourCards0.contains(guildCards.get(2)));
        assertFalse(neightbourCards0.contains(guildCards.get(3)));

        List<Card> neightbourCards1 = table.getNeighbourGuildCards(1);
        assertEquals(3, neightbourCards1.size());
        assertTrue(neightbourCards1.contains(guildCards.get(0)));
        assertTrue(neightbourCards1.contains(guildCards.get(1)));
        assertFalse(neightbourCards1.contains(guildCards.get(2)));
        assertTrue(neightbourCards1.contains(guildCards.get(3)));

        List<Card> neightbourCards2 = table.getNeighbourGuildCards(2);
        assertEquals(1, neightbourCards2.size());
        assertFalse(neightbourCards2.contains(guildCards.get(0)));
        assertFalse(neightbourCards2.contains(guildCards.get(1)));
        assertTrue(neightbourCards2.contains(guildCards.get(2)));
        assertFalse(neightbourCards2.contains(guildCards.get(3)));
    }
}
