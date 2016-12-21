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

    /**
     * Creates a T object from the given settings. This method mustn't mutate this Definition as it may be called
     * multiple times with different settings.
     *
     * @param settings
     *         the game settings to use to generate a game-specific object from this definition
     *
     * @return the new game-specific object created from this definition
     */
    T create(Settings settings);
}
