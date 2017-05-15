package org.luxons.sevenwonders.game.effects;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.BoardElementType;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.cards.CardBack;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class BonusPerBoardElementTest {

    @DataPoints
    public static int[] values() {
        return new int[] {0, 1, 2, 3};
    }

    @DataPoints
    public static Color[] colors() {
        return Color.values();
    }

    @DataPoints
    public static RelativeBoardPosition[] positions() {
        return RelativeBoardPosition.values();
    }

    private Table table;

    @Before
    public void setUp() {
        table = TestUtils.createTable(4);
    }

    private static BonusPerBoardElement createBonus(BoardElementType type, int gold, int points, Color... colors) {
        BonusPerBoardElement bonus = new BonusPerBoardElement();
        bonus.setType(type);
        bonus.setGold(gold);
        bonus.setPoints(points);
        bonus.setColors(Arrays.asList(colors));
        return bonus;
    }

    @Theory
    public void computePoints_countsCards(RelativeBoardPosition boardPosition, int nbCards, int nbOtherCards,
            int points, int gold, Color color) {
        Board board = table.getBoard(0, boardPosition);
        TestUtils.addCards(board, nbCards, nbOtherCards, color);

        BonusPerBoardElement bonus = createBonus(BoardElementType.CARD, gold, points, color);
        bonus.setBoards(Collections.singletonList(boardPosition));

        assertEquals(nbCards * points, bonus.computePoints(table, 0));
    }

    @Theory
    public void computePoints_countsDefeatTokens(RelativeBoardPosition boardPosition, int nbDefeatTokens, int points,
            int gold) {
        Board board = table.getBoard(0, boardPosition);
        for (int i = 0; i < nbDefeatTokens; i++) {
            board.getMilitary().defeat();
        }

        BonusPerBoardElement bonus = createBonus(BoardElementType.DEFEAT_TOKEN, gold, points);
        bonus.setBoards(Collections.singletonList(boardPosition));

        assertEquals(nbDefeatTokens * points, bonus.computePoints(table, 0));
    }

    @Theory
    public void computePoints_countsWonderStages(RelativeBoardPosition boardPosition, int nbStages, int points,
            int gold) {
        Board board = table.getBoard(0, boardPosition);
        for (int i = 0; i < nbStages; i++) {
            board.getWonder().buildLevel(new CardBack(""));
        }

        BonusPerBoardElement bonus = createBonus(BoardElementType.BUILT_WONDER_STAGES, gold, points);
        bonus.setBoards(Collections.singletonList(boardPosition));

        assertEquals(nbStages * points, bonus.computePoints(table, 0));
    }

    @Theory
    public void apply_countsCards(RelativeBoardPosition boardPosition, int nbCards, int nbOtherCards, int points,
            int gold, Color color) {
        Board board = table.getBoard(0, boardPosition);
        TestUtils.addCards(board, nbCards, nbOtherCards, color);

        BonusPerBoardElement bonus = createBonus(BoardElementType.CARD, gold, points, color);
        bonus.setBoards(Collections.singletonList(boardPosition));

        Board selfBoard = table.getBoard(0);
        int initialGold = selfBoard.getGold();
        bonus.apply(table, 0);
        assertEquals(initialGold + nbCards * gold, selfBoard.getGold());
    }

    @Theory
    public void apply_countsDefeatTokens(RelativeBoardPosition boardPosition, int nbDefeatTokens, int points,
            int gold) {
        Board board = table.getBoard(0, boardPosition);
        for (int i = 0; i < nbDefeatTokens; i++) {
            board.getMilitary().defeat();
        }

        BonusPerBoardElement bonus = createBonus(BoardElementType.DEFEAT_TOKEN, gold, points);
        bonus.setBoards(Collections.singletonList(boardPosition));

        Board selfBoard = table.getBoard(0);
        int initialGold = selfBoard.getGold();
        bonus.apply(table, 0);
        assertEquals(initialGold + nbDefeatTokens * gold, selfBoard.getGold());
    }

    @Theory
    public void apply_countsWonderStages(RelativeBoardPosition boardPosition, int nbStages, int points, int gold) {
        Board board = table.getBoard(0, boardPosition);
        for (int i = 0; i < nbStages; i++) {
            board.getWonder().buildLevel(new CardBack(""));
        }

        BonusPerBoardElement bonus = createBonus(BoardElementType.BUILT_WONDER_STAGES, gold, points);
        bonus.setBoards(Collections.singletonList(boardPosition));

        Board selfBoard = table.getBoard(0);
        int initialGold = selfBoard.getGold();
        bonus.apply(table, 0);
        assertEquals(initialGold + nbStages * gold, selfBoard.getGold());
    }
}
