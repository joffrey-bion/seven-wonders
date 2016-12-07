package org.luxons.sevenwonders.game.data;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.cards.Requirements;
import org.luxons.sevenwonders.game.effects.GoldIncrease;
import org.luxons.sevenwonders.game.effects.RawPointsIncrease;
import org.luxons.sevenwonders.game.effects.ScienceProgress;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class GameDataLoader {

    private static final String wondersFile = "wonders.json";

    public static GameData load() {
        GameData data = new GameData();
        data.setWonders(loadWonders());
        for (int age = 1; age <= data.getNbAges(); age++) {
            data.setCards(age, loadCards(age));
        }
        return data;
    }

    private static List<Wonder> loadWonders() {
        List<Wonder> wonders = new ArrayList<>();
        // TODO load actual file
        wonders.add(new Wonder("TestWonder W", ResourceType.WOOD));
        wonders.add(new Wonder("TestWonder S", ResourceType.STONE));
        wonders.add(new Wonder("TestWonder O", ResourceType.ORE));
        wonders.add(new Wonder("TestWonder C", ResourceType.CLAY));
        wonders.add(new Wonder("TestWonder G", ResourceType.GLASS));
        wonders.add(new Wonder("TestWonder L", ResourceType.LOOM));
        wonders.add(new Wonder("TestWonder P", ResourceType.PAPYRUS));
        return wonders;
    }

    private static List<Card> loadCards(int age) {
        List<Card> wonders = new ArrayList<>();
        // TODO load actual file
        wonders.add(new Card("TestCard Yellow", Color.YELLOW, new Requirements(), new GoldIncrease()));
        wonders.add(new Card("TestCard Blue", Color.BLUE, new Requirements(), new RawPointsIncrease()));
        wonders.add(new Card("TestCard Green", Color.GREEN, new Requirements(), new ScienceProgress()));
        return wonders;
    }
}
