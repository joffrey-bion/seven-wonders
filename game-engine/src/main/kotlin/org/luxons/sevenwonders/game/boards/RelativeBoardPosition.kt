package org.luxons.sevenwonders.game.boards

enum class RelativeBoardPosition {
    LEFT {
        override fun getIndexFrom(playerIndex: Int, nbPlayers: Int): Int {
            return wrapIndex(playerIndex - 1, nbPlayers)
        }
    },
    SELF {
        override fun getIndexFrom(playerIndex: Int, nbPlayers: Int): Int {
            return playerIndex
        }
    },
    RIGHT {
        override fun getIndexFrom(playerIndex: Int, nbPlayers: Int): Int {
            return wrapIndex(playerIndex + 1, nbPlayers)
        }
    };

    abstract fun getIndexFrom(playerIndex: Int, nbPlayers: Int): Int

    internal fun wrapIndex(index: Int, nbPlayers: Int): Int {
        return Math.floorMod(index, nbPlayers)
    }
}
