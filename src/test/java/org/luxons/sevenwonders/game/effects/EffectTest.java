package org.luxons.sevenwonders.game.effects;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.wonders.Wonder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EffectTest {

    private static final int INITIAL_GOLD = 3;

    private Board board;

    private Board leftBoard;

    private Board rightBoard;

    private Resources resources1Stone1Wood;

    private Resources resources2Stones;

    private Resources resources2Stones3Clay;

    @Before
    public void init() {
        Settings settings = new Settings();
        settings.setInitialGold(INITIAL_GOLD);

        board = new Board(new Wonder("TestWonder", ResourceType.WOOD), settings);
        leftBoard = new Board(new Wonder("TestWonder", ResourceType.STONE), settings);
        rightBoard = new Board(new Wonder("TestWonder", ResourceType.PAPYRUS), settings);

        resources1Stone1Wood = new Resources();
        resources1Stone1Wood.add(ResourceType.STONE, 1);
        resources1Stone1Wood.add(ResourceType.WOOD, 1);

        resources2Stones = new Resources();
        resources2Stones.add(ResourceType.STONE, 2);

        resources2Stones3Clay = new Resources();
        resources2Stones3Clay.add(ResourceType.STONE, 2);
        resources2Stones3Clay.add(ResourceType.CLAY, 3);
    }

    @Test
    public void testInitialBoard() {
        assertEquals(3, board.getGold());
    }

    @Test
    public void testGoldIncrease() {
        GoldIncrease effect = new GoldIncrease(6);
        effect.apply(board, leftBoard, rightBoard);
        assertEquals(INITIAL_GOLD + 6, board.getGold());
        assertEquals(INITIAL_GOLD, leftBoard.getGold());
        assertEquals(INITIAL_GOLD, rightBoard.getGold());
    }

    @Test
    public void testProductionIncrease_standard() {
        ProductionIncrease effect = new ProductionIncrease();
        effect.getProduction().addAll(resources2Stones3Clay);
        effect.apply(board, leftBoard, rightBoard);
        assertTrue(board.getProduction().contains(resources2Stones3Clay));
    }

    @Test
    public void testProductionIncrease_choice() {
        ProductionIncrease effect = new ProductionIncrease();
        effect.getProduction().addChoice(ResourceType.ORE, ResourceType.STONE);
        effect.apply(board, leftBoard, rightBoard);
        assertTrue(board.getProduction().contains(resources1Stone1Wood));
    }
}