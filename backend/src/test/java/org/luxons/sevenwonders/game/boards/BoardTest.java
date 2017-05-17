package org.luxons.sevenwonders.game.boards;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board.InsufficientFundsException;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.effects.Effect;
import org.luxons.sevenwonders.game.effects.RawPointsIncrease;
import org.luxons.sevenwonders.game.effects.SpecialAbility;
import org.luxons.sevenwonders.game.effects.SpecialAbilityActivation;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.scoring.PlayerScore;
import org.luxons.sevenwonders.game.scoring.ScoreCategory;
import org.luxons.sevenwonders.game.test.TestUtils;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class BoardTest {

    @DataPoints("gold")
    public static int[] goldAmounts() {
        return new int[]{-3, -1, 0, 1, 2, 3};
    }

    @DataPoints("nbCards")
    public static int[] nbCards() {
        return new int[]{0, 1, 2};
    }

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @DataPoints
    public static Color[] colors() {
        return Color.values();
    }

    @DataPoints
    public static SpecialAbility[] specialAbilities() {
        return SpecialAbility.values();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Theory
    public void initialGold_respectsSettings(@FromDataPoints("gold") int goldAmountInSettings) {
        CustomizableSettings customSettings = TestUtils.createCustomizableSettings();
        customSettings.setInitialGold(goldAmountInSettings);
        Settings settings = new Settings(5, customSettings);
        Board board = new Board(TestUtils.createWonder(), 0, settings);
        assertEquals(goldAmountInSettings, board.getGold());
    }

    @Theory
    public void initialProduction_containsInitialResource(ResourceType type) {
        Board board = new Board(TestUtils.createWonder(type), 0, new Settings(5));
        Resources resources = TestUtils.createResources(type);
        assertTrue(board.getProduction().contains(resources));
        assertTrue(board.getPublicProduction().contains(resources));
    }

    @Theory
    public void removeGold_successfulWhenNotTooMuch(@FromDataPoints("gold") int initialGold,
                                                    @FromDataPoints("gold") int goldRemoved) {
        assumeTrue(goldRemoved >= 0);
        assumeTrue(initialGold >= goldRemoved);
        Board board = new Board(TestUtils.createWonder(), 0, new Settings(5));
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
        Board board = new Board(TestUtils.createWonder(), 0, new Settings(5));
        board.setGold(initialGold);
        board.removeGold(goldRemoved);
    }

    @Theory
    public void getNbCardsOfColor_properCount_singleColor(ResourceType type, @FromDataPoints("nbCards") int nbCards,
                                                          @FromDataPoints("nbCards") int nbOtherCards, Color color) {
        Board board = TestUtils.createBoard(type);
        TestUtils.addCards(board, nbCards, nbOtherCards, color);
        assertEquals(nbCards, board.getNbCardsOfColor(Collections.singletonList(color)));
    }

    @Theory
    public void getNbCardsOfColor_properCount_multiColors(ResourceType type, @FromDataPoints("nbCards") int nbCards1,
                                                          @FromDataPoints("nbCards") int nbCards2,
                                                          @FromDataPoints("nbCards") int nbOtherCards, Color color1,
                                                          Color color2) {
        Board board = TestUtils.createBoard(type);
        TestUtils.addCards(board, nbCards1, color1);
        TestUtils.addCards(board, nbCards2, color2);
        TestUtils.addCards(board, nbOtherCards, TestUtils.getDifferentColorFrom(color1, color2));
        assertEquals(nbCards1 + nbCards2, board.getNbCardsOfColor(Arrays.asList(color1, color2)));
    }

    @Test
    public void setCopiedGuild_succeedsOnPurpleCard() {
        Board board = TestUtils.createBoard(ResourceType.CLAY);
        Card card = TestUtils.createCard(Color.PURPLE);

        board.setCopiedGuild(card);
        assertSame(card, board.getCopiedGuild());
    }

    @Theory
    public void setCopiedGuild_failsOnNonPurpleCard(Color color) {
        assumeTrue(color != Color.PURPLE);
        Board board = TestUtils.createBoard(ResourceType.CLAY);
        Card card = TestUtils.createCard(color);

        thrown.expect(IllegalArgumentException.class);
        board.setCopiedGuild(card);
    }

    @Theory
    public void hasSpecial(SpecialAbility applied, SpecialAbility tested) {
        Board board = TestUtils.createBoard(ResourceType.CLAY);
        Table table = new Table(Collections.singletonList(board));
        SpecialAbilityActivation special = new SpecialAbilityActivation(applied);

        special.apply(table, 0);

        assertEquals(applied == tested, board.hasSpecial(tested));
    }

    @Test
    public void canPlayFreeCard() {
        Board board = TestUtils.createBoard(ResourceType.CLAY);
        Table table = new Table(Collections.singletonList(board));
        SpecialAbilityActivation special = new SpecialAbilityActivation(SpecialAbility.ONE_FREE_PER_AGE);

        special.apply(table, 0);

        assertTrue(board.canPlayFreeCard(0));
        assertTrue(board.canPlayFreeCard(1));
        assertTrue(board.canPlayFreeCard(2));

        board.consumeFreeCard(0);

        assertFalse(board.canPlayFreeCard(0));
        assertTrue(board.canPlayFreeCard(1));
        assertTrue(board.canPlayFreeCard(2));

        board.consumeFreeCard(1);

        assertFalse(board.canPlayFreeCard(0));
        assertFalse(board.canPlayFreeCard(1));
        assertTrue(board.canPlayFreeCard(2));

        board.consumeFreeCard(2);

        assertFalse(board.canPlayFreeCard(0));
        assertFalse(board.canPlayFreeCard(1));
        assertFalse(board.canPlayFreeCard(2));
    }

    @Theory
    public void computePoints_gold(@FromDataPoints("gold") int gold) {
        assumeTrue(gold >= 0);
        Board board = TestUtils.createBoard(ResourceType.WOOD);
        Table table = new Table(Collections.singletonList(board));
        board.setGold(gold);

        PlayerScore score = board.computePoints(table);
        assertEquals(gold / 3, (int) score.getPoints(ScoreCategory.GOLD));
        assertEquals(gold / 3, score.getTotalPoints());
    }

    @Theory
    public void computePoints_(@FromDataPoints("gold") int gold) {
        assumeTrue(gold >= 0);
        Board board = TestUtils.createBoard(ResourceType.WOOD);
        Table table = new Table(Collections.singletonList(board));
        board.setGold(gold);

        Effect effect = new RawPointsIncrease(5);
        TestUtils.playCardWithEffect(table, 0, Color.BLUE, effect);

        PlayerScore score = board.computePoints(table);
        assertEquals(gold / 3, (int) score.getPoints(ScoreCategory.GOLD));
        assertEquals(5, (int) score.getPoints(ScoreCategory.CIVIL));
        assertEquals(5 + gold / 3, score.getTotalPoints());
    }
}
