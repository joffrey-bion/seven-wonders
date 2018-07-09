package org.luxons.sevenwonders.game.boards

import org.luxons.sevenwonders.game.cards.Color

enum class BoardElementType {
    CARD {
        override fun getElementCount(board: Board, colors: List<Color>?): Int = board.getNbCardsOfColor(colors!!)
    },
    BUILT_WONDER_STAGES {
        override fun getElementCount(board: Board, colors: List<Color>?): Int = board.wonder.nbBuiltStages
    },
    DEFEAT_TOKEN {
        override fun getElementCount(board: Board, colors: List<Color>?): Int = board.military.nbDefeatTokens
    };

    abstract fun getElementCount(board: Board, colors: List<Color>?): Int
}
