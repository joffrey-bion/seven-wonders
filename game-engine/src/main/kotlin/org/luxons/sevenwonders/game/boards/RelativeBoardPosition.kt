package org.luxons.sevenwonders.game.boards

enum class RelativeBoardPosition(private val offset: Int) {
    LEFT(-1),
    SELF(0),
    RIGHT(1);

    fun getIndexFrom(playerIndex: Int, nbPlayers: Int): Int = Math.floorMod(playerIndex + offset, nbPlayers)
}

fun neighboursPositions() = listOf(RelativeBoardPosition.LEFT, RelativeBoardPosition.RIGHT)
