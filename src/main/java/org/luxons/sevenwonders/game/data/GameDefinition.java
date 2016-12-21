package org.luxons.sevenwonders.game.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.Decks;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.data.definitions.DecksDefinition;
import org.luxons.sevenwonders.game.data.definitions.WonderDefinition;

public class GameDefinition {

    /**
     * This value is heavily dependent on the JSON data. Any change must be carefully thought through.
     */
    private static final int MIN_PLAYERS = 3;

    /**
     * This value is heavily dependent on the JSON data. Any change must be carefully thought through.
     */
    private static final int MAX_PLAYERS = 7;

    private WonderDefinition[] wonders;

    private DecksDefinition decksDefinition;

    GameDefinition(WonderDefinition[] wonders, DecksDefinition decksDefinition) {
        this.wonders = wonders;
        this.decksDefinition = decksDefinition;
    }

    public int getMinPlayers() {
        return MIN_PLAYERS;
    }

    public int getMaxPlayers() {
        return MAX_PLAYERS;
    }

    public Game initGame(long id, Settings settings, List<Player> orderedPlayers) {
        List<Board> boards = pickRandomBoards(settings);
        Decks decks = decksDefinition.create(settings);
        return new Game(id, settings, orderedPlayers, boards, decks);
    }

    private List<Board> pickRandomBoards(Settings settings) {
        List<WonderDefinition> randomizedWonders = Arrays.asList(wonders);
        Collections.shuffle(randomizedWonders, settings.getRandom());
        return Arrays.stream(wonders)
                     .map(def -> def.create(settings))
                     .map(w -> new Board(w, settings))
                     .limit(settings.getNbPlayers())
                     .collect(Collectors.toList());
    }
}
