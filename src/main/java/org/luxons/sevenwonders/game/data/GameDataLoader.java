package org.luxons.sevenwonders.game.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class GameDataLoader {

    private static final String BASE_PACKAGE = GameDataLoader.class.getPackage().getName();

    private static final String BASE_PACKAGE_PATH = '/' + BASE_PACKAGE.replace('.', '/');

    private static final String wondersFile = "wonders.json";

    public static GameData load(Settings settings) {
        GameData data = new GameData();
        data.setWonders(loadWonders());

        int nbPlayers = settings.getNbPlayers();

        DecksDefinition defs = loadDecks();
        data.setCards(1, createCards(defs.getAge1(), nbPlayers));
        data.setCards(2, createCards(defs.getAge2(), nbPlayers));

        List<Card> age3 = createCards(defs.getAge3(), nbPlayers);
        List<Card> guild = createGuildCards(defs.getGuild(), nbPlayers);
        age3.addAll(guild);
        data.setCards(3, age3);
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

    private static List<Card> createCards(List<CardDefinition> defs, int nbPlayers) {
        List<Card> cards = new ArrayList<>();
        for (CardDefinition def : defs) {
            for (int i = 0; i < def.getCountPerNbPlayer().get(nbPlayers); i++) {
                cards.add(def.createCard());
            }
        }
        return cards;
    }

    private static List<Card> createGuildCards(List<CardDefinition> defs, int nbPlayers) {
        List<Card> guild = defs.stream().map(CardDefinition::createCard).collect(Collectors.toList());
        Collections.shuffle(guild);
        return guild.subList(0, nbPlayers + 2);
    }

    private static DecksDefinition loadDecks() {
        return readJsonFile("cards.json", DecksDefinition.class);
    }

    private static <T> T readJsonFile(String filename, Class<T> clazz) {
        InputStream in = GameDataLoader.class.getResourceAsStream(BASE_PACKAGE_PATH + '/' + filename);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = createGson();
        return gson.fromJson(reader, clazz);
    }

    private static Gson createGson() {
        return new GsonBuilder().disableHtmlEscaping()
                                .serializeNulls()
                                .registerTypeAdapter(Resources.class, new ResourcesSerializer())
                                .setPrettyPrinting()
                                .create();
    }
}
