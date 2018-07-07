package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.cards.Card

enum class SpecialAbility {

    /**
     * The player can play the last card of each age instead of discarding it. This card can be played by paying its
     * cost, discarded to gain 3 coins or used in the construction of his or her Wonder.
     */
    PLAY_LAST_CARD,

    /**
     * Once per age, a player can construct a building from his or her hand for free.
     */
    ONE_FREE_PER_AGE,

    /**
     * The player can look at all cards discarded since the beginning of the game, pick one and build it for free.
     */
    PLAY_DISCARDED,

    /**
     * The player can, at the end of the game, "copy" a Guild of his or her choice (purple card), built by one of his or
     * her two neighboring cities.
     */
    COPY_GUILD {
        override fun computePoints(table: Table, playerIndex: Int): Int {
            val copiedGuild = table.getBoard(playerIndex).copiedGuild
                    ?: throw IllegalStateException("The copied Guild has not been chosen, cannot compute points")
            return copiedGuild.effects.stream().mapToInt { e -> e.computePoints(table, playerIndex) }.sum()
        }
    };

    fun apply(board: Board) = board.addSpecial(this)

    open fun computePoints(table: Table, playerIndex: Int): Int = 0
}
