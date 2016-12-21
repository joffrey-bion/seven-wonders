package org.luxons.sevenwonders.game.effects;

import java.util.List;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Color;

public enum BoardElementType {
    CARD {
        @Override
        int getElementCount(Board board, List<Color> colors) {
            return board.getNbCardsOfColor(colors);
        }
    },
    WONDER_LEVEL {
        @Override
        int getElementCount(Board board, List<Color> colors) {
            return board.getWonderLevel();
        }
    },
    DEFEAT_TOKEN {
        @Override
        int getElementCount(Board board, List<Color> colors) {
            return board.getNbDefeatTokens();
        }
    };

    abstract int getElementCount(Board board, List<Color> colors);
}
