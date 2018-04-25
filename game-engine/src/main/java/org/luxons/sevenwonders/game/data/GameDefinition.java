package org.luxons.sevenwonders.game.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Decks;
import org.luxons.sevenwonders.game.data.definitions.DecksDefinition;
import org.luxons.sevenwonders.game.data.definitions.WonderDefinition;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class GameDefinition {

    private final GlobalRules rules;

    private final WonderDefinition[] wonders;

    private final DecksDefinition decksDefinition;

    GameDefinition(GlobalRules rules, WonderDefinition[] wonders, DecksDefinition decksDefinition) {
        this.rules = rules;
        this.wonders = wonders;
        this.decksDefinition = decksDefinition;
    }

    public int getMinPlayers() {
        return rules.getMinPlayers();
    }

    public int getMaxPlayers() {
        return rules.getMaxPlayers();
    }

    public Game initGame(long id, CustomizableSettings customSettings, int nbPlayers) {
        Settings settings = new Settings(nbPlayers, customSettings);
        List<Board> boards = assignBoards(settings, nbPlayers);
        Decks decks = decksDefinition.create(settings);
        return new Game(id, settings, nbPlayers, boards, decks);
    }

    private List<Board> assignBoards(Settings settings, int nbPlayers) {
        List<WonderDefinition> randomizedWonders = Arrays.asList(wonders);
        Collections.shuffle(randomizedWonders, settings.getRandom());

        List<Board> boards = new ArrayList<>(nbPlayers);
        for (int i = 0; i < nbPlayers; i++) {
            WonderDefinition def = randomizedWonders.get(i);
            Wonder w = def.create(settings);
            Board b = new Board(w, i, settings);
            boards.add(b);
        }
        return boards;
    }
}
