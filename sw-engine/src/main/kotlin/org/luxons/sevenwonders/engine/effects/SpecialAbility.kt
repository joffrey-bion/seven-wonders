package org.luxons.sevenwonders.engine.effects

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.boards.Board

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
        override fun computePoints(player: Player): Int {
            val copiedGuild = player.board.copiedGuild
                ?: throw IllegalStateException("The copied Guild has not been chosen, cannot compute points")
            return copiedGuild.effects.sumBy { it.computePoints(player) }
        }
    };

    internal fun apply(board: Board) = board.addSpecial(this)

    internal open fun computePoints(player: Player): Int = 0
}
