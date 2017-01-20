package org.luxons.sevenwonders.game.effects;

import java.util.List;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.BoardElementType;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.cards.Color;

public class BonusPerBoardElement implements Effect {

    private List<RelativeBoardPosition> boards;

    private int gold;

    private int points;

    private BoardElementType type;

    // only relevant if type=CARD
    private List<Color> colors;

    public List<RelativeBoardPosition> getBoards() {
        return boards;
    }

    public void setBoards(List<RelativeBoardPosition> boards) {
        this.boards = boards;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public BoardElementType getType() {
        return type;
    }

    public void setType(BoardElementType type) {
        this.type = type;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    @Override
    public void apply(Table table, int playerIndex) {
        int goldGain = gold * computeNbOfMatchingElementsIn(table, playerIndex);
        Board board = table.getBoard(playerIndex);
        board.addGold(goldGain);
    }

    @Override
    public int computePoints(Table table, int playerIndex) {
        return points * computeNbOfMatchingElementsIn(table, playerIndex);
    }

    private int computeNbOfMatchingElementsIn(Table table, int playerIndex) {
        return boards.stream()
                     .map(pos -> table.getBoard(playerIndex, pos))
                     .mapToInt(this::computeNbOfMatchingElementsIn)
                     .sum();
    }

    private int computeNbOfMatchingElementsIn(Board board) {
        return type.getElementCount(board, colors);
    }
}
