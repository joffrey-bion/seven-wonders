package org.luxons.sevenwonders.game.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.luxons.sevenwonders.game.cards.Decks;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.data.definitions.DecksDefinition;
import org.luxons.sevenwonders.game.data.definitions.WonderDefinition;
import org.luxons.sevenwonders.game.wonders.Wonder;

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
        List<Board> boards = assignBoards(settings, orderedPlayers);
        Decks decks = decksDefinition.create(settings);
        return new Game(id, settings, orderedPlayers, boards, decks);
    }

    private List<Board> assignBoards(Settings settings, List<Player> orderedPlayers) {
        List<WonderDefinition> randomizedWonders = Arrays.asList(wonders);
        Collections.shuffle(randomizedWonders, settings.getRandom());

        List<Board> boards = new ArrayList<>(orderedPlayers.size());
        for (int i = 0; i < orderedPlayers.size(); i++) {
            Player player = orderedPlayers.get(i);
            WonderDefinition def = randomizedWonders.get(i);
            Wonder w = def.create(settings);
            Board b = new Board(w, player, settings);
            boards.add(b);
        }
        return boards;
    }
}
