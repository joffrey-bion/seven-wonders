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
    BUILT_WONDER_STAGES {
        @Override
        public int getElementCount(Board board, List<Color> colors) {
            return board.getWonder().getNbBuiltStages();
        }
    },
    DEFEAT_TOKEN {
        @Override
        public int getElementCount(Board board, List<Color> colors) {
            return board.getMilitary().getNbDefeatTokens();
        }
    };

    public abstract int getElementCount(Board board, List<Color> colors);
}
