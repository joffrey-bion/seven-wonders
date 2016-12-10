package org.luxons.sevenwonders.game.data.definitions;

import org.luxons.sevenwonders.game.Settings;

/**
 * Represents a deserialized JSON definition of some data about the game. It is settings-agnostic. An instance of
 * in-game data can be generated from this, given specific game settings.
 *
 * @param <T>
 *         the type of in-game object that can be generated from this definition
 */
public interface Definition<T> {

    T create(Settings settings);
}
