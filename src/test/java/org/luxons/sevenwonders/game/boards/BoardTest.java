package org.luxons.sevenwonders.game.boards;

import java.util.Arrays;
import java.util.Collections;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.test.TestUtils;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class BoardTest {

    @DataPoints("gold")
    public static int[] goldAmounts() {
        return new int[]{-3, -1, 0, 1, 2, 3};
    }

    @DataPoints("nbCards")
    public static int[] nbCards() {
        return new int[] {0, 1, 2};
    }

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @DataPoints
    public static Color[] colors() {
        return Color.values();
    }

    @Theory
    public void initialGold_respectsSettings(@FromDataPoints("gold") int goldAmountInSettings) {
        Settings settings = new Settings();
        settings.setInitialGold(goldAmountInSettings);
        Board board = new Board(TestUtils.createWonder(), null, settings);
        assertEquals(goldAmountInSettings, board.getGold());
    }

    @Theory
    public void initialProduction_containsInitialResource(ResourceType type) {
        Board board = new Board(TestUtils.createWonder(type), null, new Settings());
        Resources resources = TestUtils.createResources(type);
        assertTrue(board.getProduction().contains(resources));
    }

    @Theory
    public void getNbCardsOfColor_properCount_singleColor(ResourceType type, @FromDataPoints("nbCards") int nbCards,
            @FromDataPoints("nbCards") int nbOtherCards, Color color) {
        Board board = new Board(TestUtils.createWonder(type), null, new Settings());
        TestUtils.addCards(board, nbCards, nbOtherCards, color);
        assertEquals(nbCards, board.getNbCardsOfColor(Collections.singletonList(color)));
    }

    @Theory
    public void getNbCardsOfColor_properCount_multiColors(ResourceType type, @FromDataPoints("nbCards") int nbCards1,
            @FromDataPoints("nbCards") int nbCards2, @FromDataPoints("nbCards") int nbOtherCards, Color color1,
            Color color2) {
        Board board = new Board(TestUtils.createWonder(type), null, new Settings());
        TestUtils.addCards(board, nbCards1, color1);
        TestUtils.addCards(board, nbCards2, color2);
        TestUtils.addCards(board, nbOtherCards, TestUtils.getDifferentColorFrom(color1, color2));
        assertEquals(nbCards1 + nbCards2, board.getNbCardsOfColor(Arrays.asList(color1, color2)));
    }
}