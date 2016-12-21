package org.luxons.sevenwonders.game.cards;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.effects.Effect;
import org.luxons.sevenwonders.game.effects.ProductionIncrease;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.wonders.Wonder;

import static org.junit.Assert.assertEquals;

public class CardTest {

    private Board board;

    private Board leftBoard;

    private Board rightBoard;

    private Card treeFarmCard;

    @Before
    public void initBoard() {
        Settings settings = new Settings();
        board = new Board(new Wonder("TestWonder", ResourceType.WOOD), settings);
        leftBoard = new Board(new Wonder("TestWonder", ResourceType.STONE), settings);
        rightBoard = new Board(new Wonder("TestWonder", ResourceType.PAPYRUS), settings);

        Requirements treeFarmRequirements = new Requirements();
        treeFarmRequirements.setGold(1);

        ProductionIncrease treeFarmEffect = new ProductionIncrease();
        treeFarmEffect.getProduction().addChoice(ResourceType.WOOD, ResourceType.CLAY);

        List<Effect> effects = Collections.singletonList(treeFarmEffect);

        treeFarmCard = new Card("Tree Farm", Color.BROWN, treeFarmRequirements, effects, "", null, null);
    }

    @Test
    public void testInitialBoard() {
        assertEquals(3, board.getGold());
    }

    @Test
    public void playCardCostingMoney() {
        board.setGold(3);
        treeFarmCard.applyTo(board, leftBoard, rightBoard);
        assertEquals(2, board.getGold());
    }

}
