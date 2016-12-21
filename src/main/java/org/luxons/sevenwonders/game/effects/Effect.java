package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.api.Table;

/**
 * Represents an effect than can be applied to a player's board when playing a card or building his wonder. The effect
 * may affect (or depend on) the adjacent boards. It can have an instantaneous effect on the board, or be postponed to
 * the end of game where point calculations take place.
 */
public interface Effect {

    void apply(Table table, int playerIndex);

    int computePoints(Table table, int playerIndex);
}
