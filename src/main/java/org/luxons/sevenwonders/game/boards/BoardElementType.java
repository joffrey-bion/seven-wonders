package org.luxons.sevenwonders.game.boards;

import java.util.List;

import org.luxons.sevenwonders.game.cards.Color;

public enum BoardElementType {
    CARD {
        @Override
        public int getElementCount(Board board, List<Color> colors) {
            return board.getNbCardsOfColor(colors);
        }
    },
    WONDER_LEVEL {
        @Override
        public int getElementCount(Board board, List<Color> colors) {
            return board.getWonderLevel();
        }
    },
    DEFEAT_TOKEN {
        @Override
        public int getElementCount(Board board, List<Color> colors) {
            return board.getNbDefeatTokens();
        }
    };

    public abstract int getElementCount(Board board, List<Color> colors);
}
