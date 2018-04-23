package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Card;

public enum SpecialAbility {

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
        @Override
        public int computePoints(Table table, int playerIndex) {
            Card copiedGuild = table.getBoard(playerIndex).getCopiedGuild();
            if (copiedGuild == null) {
                throw new IllegalStateException("The copied Guild has not been chosen, cannot compute points");
            }
            return copiedGuild.getEffects().stream().mapToInt(e -> e.computePoints(table, playerIndex)).sum();
        }
    };

    protected void apply(Board board) {
        board.addSpecial(this);
    }

    public int computePoints(Table table, int playerIndex) {
        return 0;
    }
}
