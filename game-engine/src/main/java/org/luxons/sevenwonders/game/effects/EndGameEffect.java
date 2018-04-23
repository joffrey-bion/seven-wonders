package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.api.Table;

public abstract class EndGameEffect implements Effect {

    @Override
    public void apply(Table table, int playerIndex) {
        // EndGameEffects don't do anything when applied to the board, they simply give more points in the end
    }
}
