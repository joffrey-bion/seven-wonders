package org.luxons.sevenwonders.game.data.definitions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.GameData;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class GameDefinition implements Definition<GameData> {

    /**
     * This value is heavily dependent on the JSON data. Any change must be carefully thought through.
     */
    private static final int MIN_PLAYERS = 3;

    /**
     * This value is heavily dependent on the JSON data. Any change must be carefully thought through.
     */
    private static final int MAX_PLAYERS = 7;

    private WonderDefinition[] wonders;

    private DecksDefinition decks;

    public GameDefinition(WonderDefinition[] wonders, DecksDefinition decks) {
        this.wonders = wonders;
        this.decks = decks;
    }

    @Override
    public GameData create(Settings settings) {
        GameData data = new GameData(MIN_PLAYERS, MAX_PLAYERS);
        data.setWonders(createWonders(settings));
        data.setDecks(decks.create(settings));
        return data;
    }

    private List<Wonder> createWonders(Settings settings) {
        return Arrays.stream(wonders).map(def -> def.create(settings)).collect(Collectors.toList());
    }
}
