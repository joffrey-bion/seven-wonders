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
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.wonders.Wonder;

import static org.junit.Assert.assertEquals;

public class CardTest {

    private Table table;

    private Card treeFarmCard;

    @Before
    public void initBoard() {
        Settings settings = new Settings();

        List<Board> boards = new ArrayList<>(3);
        boards.add(new Board(new Wonder("TestWonder", ResourceType.WOOD), null, settings));
        boards.add(new Board(new Wonder("TestWonder", ResourceType.STONE), null, settings));
        boards.add(new Board(new Wonder("TestWonder", ResourceType.PAPYRUS), null, settings));
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
        treeFarmCard.applyTo(table, 0);
        assertEquals(2, table.getBoard(0).getGold());
        assertEquals(3, table.getBoard(1).getGold());
        assertEquals(3, table.getBoard(2).getGold());
    }
}
