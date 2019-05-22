package org.luxons.sevenwonders.model.boards

enum class RelativeBoardPosition(private val offset: Int) {
    LEFT(-1),
    SELF(0),
    RIGHT(1);

    fun getIndexFrom(playerIndex: Int, nbPlayers: Int): Int = (playerIndex + offset) floorMod nbPlayers
}

fun neighboursPositions() = listOf(
    RelativeBoardPosition.LEFT,
    RelativeBoardPosition.RIGHT
)

private infix fun Int.floorMod(divisor: Int): Int {
    val rem = this % divisor
    return if (rem >= 0) rem else rem + divisor
}
