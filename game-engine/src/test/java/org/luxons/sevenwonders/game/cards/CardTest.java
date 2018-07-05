package org.luxons.sevenwonders.game.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.effects.Effect;
import org.luxons.sevenwonders.game.effects.ProductionIncrease;
import org.luxons.sevenwonders.game.resources.ResourceTransactions;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.test.TestUtilsKt;
import org.luxons.sevenwonders.game.wonders.Wonder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CardTest {

    private Table table;

    private Card treeFarmCard;

    @Before
    public void initBoard() {
        Settings settings = new Settings(3);

        List<Board> boards = new ArrayList<>(3);
        boards.add(new Board(new Wonder("TestWonder", ResourceType.WOOD), 0, settings));
        boards.add(new Board(new Wonder("TestWonder", ResourceType.STONE), 1, settings));
        boards.add(new Board(new Wonder("TestWonder", ResourceType.PAPYRUS), 2, settings));
        table = new Table(boards);

        Requirements treeFarmRequirements = new Requirements();
        treeFarmRequirements.setGold(1);

        ProductionIncrease treeFarmEffect = new ProductionIncrease();
        treeFarmEffect.getProduction().addChoice(ResourceType.WOOD, ResourceType.CLAY);

        List<Effect> effects = Collections.singletonList(treeFarmEffect);

        treeFarmCard = new Card("Tree Farm", Color.BROWN, treeFarmRequirements, effects, "", null, null);
    }

    @Test
    public void playCardCostingMoney() {
        table.getBoard(0).setGold(3);
        table.getBoard(1).setGold(3);
        table.getBoard(2).setGold(3);
        treeFarmCard.applyTo(table, 0, new ResourceTransactions());
        assertEquals(2, table.getBoard(0).getGold());
        assertEquals(3, table.getBoard(1).getGold());
        assertEquals(3, table.getBoard(2).getGold());
    }

    @Test
    public void equals_falseWhenNull() {
        Card card = TestUtilsKt.testCard("TestCard");
        //noinspection ObjectEqualsNull
        assertFalse(card.equals(null));
    }

    @Test
    public void equals_falseWhenDifferentClass() {
        Card card = TestUtilsKt.testCard("TestCard");
        Object object = new Object();
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(card.equals(object));
    }

    @Test
    public void equals_trueWhenSame() {
        Card card = TestUtilsKt.testCard("TestCard");
        assertEquals(card, card);
    }

    @Test
    public void equals_trueWhenSameContent() {
        Card card1 = TestUtilsKt.testCard("TestCard");
        Card card2 = TestUtilsKt.testCard("TestCard");
        assertTrue(card1.equals(card2));
    }

    @Test
    public void equals_falseWhenDifferentName() {
        Card card1 = TestUtilsKt.testCard("TestCard1");
        Card card2 = TestUtilsKt.testCard("TestCard2");
        assertFalse(card1.equals(card2));
    }

    @Test
    public void hashCode_sameWhenSameContent() {
        Card card1 = TestUtilsKt.testCard("TestCard");
        Card card2 = TestUtilsKt.testCard("TestCard");
        assertEquals(card1.hashCode(), card2.hashCode());
    }

    @Test
    public void hashCode_differentWhenDifferentName() {
        Card card1 = TestUtilsKt.testCard("TestCard1");
        Card card2 = TestUtilsKt.testCard("TestCard2");
        assertNotEquals(card1.hashCode(), card2.hashCode());
    }
}
