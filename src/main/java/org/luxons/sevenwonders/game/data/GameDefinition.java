package org.luxons.sevenwonders.game.data;

import org.luxons.sevenwonders.game.Decks;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.data.definitions.DecksDefinition;
import org.luxons.sevenwonders.game.data.definitions.Definition;
import org.luxons.sevenwonders.game.data.definitions.WonderDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public GameDefinition(WonderDefinition[] wonders, DecksDefinition decksDefinition) {
        this.wonders = wonders;
        this.decksDefinition = decksDefinition;
    }

    public int getMinPlayers() {
        return MIN_PLAYERS;
    }

    public int getMaxPlayers() {
        return MAX_PLAYERS;
    }

    public Game initGame(Settings settings) {
        List<Board> boards = pickRandomBoards(settings);
        Decks decks = decksDefinition.create(settings);
        return new Game(settings, boards, decks);
    }

    private List<Board> pickRandomBoards(Settings settings) {
        List<WonderDefinition> randomizedWonders = Arrays.asList(wonders);
        Collections.shuffle(randomizedWonders, settings.getRandom());
        return Arrays.stream(wonders).map(def -> def.create(settings)).map(w -> new Board(w, settings)).collect(Collectors.toList());
    }
}
