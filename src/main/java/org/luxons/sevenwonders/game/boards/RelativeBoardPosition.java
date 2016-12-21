package org.luxons.sevenwonders.game.boards;

public enum RelativeBoardPosition {
    LEFT {
        @Override
        public int getIndexFrom(int playerIndex, int nbPlayers) {
            return wrapIndex(playerIndex - 1, nbPlayers);
        }
    },
    SELF {
        @Override
        public int getIndexFrom(int playerIndex, int nbPlayers) {
            return playerIndex;
        }
    },
    RIGHT {
        @Override
        public int getIndexFrom(int playerIndex, int nbPlayers) {
            return wrapIndex(playerIndex + 1, nbPlayers);
        }
    };

    public abstract int getIndexFrom(int playerIndex, int nbPlayers);

    int wrapIndex(int index, int nbPlayers) {
        return Math.floorMod(index, nbPlayers);
    }
}
