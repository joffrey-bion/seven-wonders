package org.luxons.sevenwonders.game.boards;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.boards.Board.InsufficientFundsException;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.test.TestUtils;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.*;

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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Theory
    public void initialGold_respectsSettings(@FromDataPoints("gold") int goldAmountInSettings) {
        CustomizableSettings customSettings = new CustomizableSettings();
        customSettings.setInitialGold(goldAmountInSettings);
        Settings settings = new Settings(5, customSettings);
        Board board = new Board(TestUtils.createWonder(), null, settings);
        assertEquals(goldAmountInSettings, board.getGold());
    }

    @Theory
    public void initialProduction_containsInitialResource(ResourceType type) {
        Board board = new Board(TestUtils.createWonder(type), null, new Settings(5));
        Resources resources = TestUtils.createResources(type);
        assertTrue(board.getProduction().contains(resources));
        assertTrue(board.getPublicProduction().contains(resources));
    }

    @Theory
    public void removeGold_successfulWhenNotTooMuch(@FromDataPoints("gold") int initialGold,
            @FromDataPoints("gold") int goldRemoved) {
        assumeTrue(goldRemoved >= 0);
        assumeTrue(initialGold >= goldRemoved);
        Board board = new Board(TestUtils.createWonder(), null, new Settings(5));
        board.setGold(initialGold);
        board.removeGold(goldRemoved);
        assertEquals(initialGold - goldRemoved, board.getGold());
    }

    @Theory
    public void removeGold_failsWhenTooMuch(@FromDataPoints("gold") int initialGold,
            @FromDataPoints("gold") int goldRemoved) {
        assumeTrue(goldRemoved >= 0);
        assumeTrue(initialGold < goldRemoved);
        thrown.expect(InsufficientFundsException.class);
        Board board = new Board(TestUtils.createWonder(), null, new Settings(5));
        board.setGold(initialGold);
        board.removeGold(goldRemoved);
    }

    @Theory
    public void getNbCardsOfColor_properCount_singleColor(ResourceType type, @FromDataPoints("nbCards") int nbCards,
            @FromDataPoints("nbCards") int nbOtherCards, Color color) {
        Board board = new Board(TestUtils.createWonder(type), null, new Settings(5));
        TestUtils.addCards(board, nbCards, nbOtherCards, color);
        assertEquals(nbCards, board.getNbCardsOfColor(Collections.singletonList(color)));
    }

    @Theory
    public void getNbCardsOfColor_properCount_multiColors(ResourceType type, @FromDataPoints("nbCards") int nbCards1,
            @FromDataPoints("nbCards") int nbCards2, @FromDataPoints("nbCards") int nbOtherCards, Color color1,
            Color color2) {
        Board board = new Board(TestUtils.createWonder(type), null, new Settings(5));
        TestUtils.addCards(board, nbCards1, color1);
        TestUtils.addCards(board, nbCards2, color2);
        TestUtils.addCards(board, nbOtherCards, TestUtils.getDifferentColorFrom(color1, color2));
        assertEquals(nbCards1 + nbCards2, board.getNbCardsOfColor(Arrays.asList(color1, color2)));
    }
}