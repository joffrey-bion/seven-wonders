package org.luxons.sevenwonders.game.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.data.definitions.CardDefinition;
import org.luxons.sevenwonders.game.data.definitions.DecksDefinition;
import org.luxons.sevenwonders.game.data.serializers.NumericEffectSerializer;
import org.luxons.sevenwonders.game.data.serializers.ProductionIncreaseSerializer;
import org.luxons.sevenwonders.game.data.serializers.ResourceTypesSerializer;
import org.luxons.sevenwonders.game.data.serializers.ResourcesSerializer;
import org.luxons.sevenwonders.game.data.serializers.ScienceProgressSerializer;
import org.luxons.sevenwonders.game.effects.GoldIncrease;
import org.luxons.sevenwonders.game.effects.MilitaryReinforcements;
import org.luxons.sevenwonders.game.effects.ProductionIncrease;
import org.luxons.sevenwonders.game.effects.RawPointsIncrease;
import org.luxons.sevenwonders.game.effects.ScienceProgress;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class GameDataLoader {

    private static final String BASE_PACKAGE = GameDataLoader.class.getPackage().getName();

    private static final String BASE_PACKAGE_PATH = '/' + BASE_PACKAGE.replace('.', '/');

    private static final String CARDS_FILE = "cards.json";

    private static final String WONDERS_FILE = "wonders.json";

    public static GameData load(Settings settings) {
        GameData data = new GameData();

        List<Wonder> wonders = loadWonders();
        data.setWonders(wonders);

        DecksDefinition decksDefinition = loadDecks();
        data.setDecks(new Decks(decksDefinition, settings));
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

    private static DecksDefinition loadDecks() {
        return readJsonFile(CARDS_FILE, DecksDefinition.class);
    }

    private static <T> T readJsonFile(String filename, Class<T> clazz) {
        InputStream in = GameDataLoader.class.getResourceAsStream(BASE_PACKAGE_PATH + '/' + filename);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = createGson();
        return gson.fromJson(reader, clazz);
    }

    private static Gson createGson() {
        Type resourceTypeList = new TypeToken<List<ResourceType>>() {}.getType();
        return new GsonBuilder().disableHtmlEscaping()
                                .registerTypeAdapter(Resources.class, new ResourcesSerializer())
                                .registerTypeAdapter(resourceTypeList, new ResourceTypesSerializer())
                                .registerTypeAdapter(ProductionIncrease.class, new ProductionIncreaseSerializer())
                                .registerTypeAdapter(MilitaryReinforcements.class, new NumericEffectSerializer())
                                .registerTypeAdapter(RawPointsIncrease.class, new NumericEffectSerializer())
                                .registerTypeAdapter(GoldIncrease.class, new NumericEffectSerializer())
                                .registerTypeAdapter(ScienceProgress.class, new ScienceProgressSerializer())
//                                .setPrettyPrinting()
                                .create();
    }

    public static void main(String[] args) {
        DecksDefinition decksDef = loadDecks();
        Comparator<CardDefinition> comparator =
                Comparator.comparing(CardDefinition::getColor).thenComparing(CardDefinition::getName);
        List<List<CardDefinition>> decks = new ArrayList<>();
        decks.add(decksDef.getAge1());
        decks.add(decksDef.getAge2());
        decks.add(decksDef.getAge3());
        decks.add(decksDef.getGuildCards());

        for (List<CardDefinition> deck : decks) {
            deck.sort(comparator);
            for (CardDefinition cardDefinition : deck) {
                cardDefinition.setImage(computeImageName(cardDefinition.getName()));
            }
        }
        Gson gson = createGson();

        System.out.println(gson.toJson(decksDef));
//        System.out.println(load(5));
    }

    private static String computeImageName(String name) {
        return name.toLowerCase().replaceAll("\\s", "") + ".png";
    }
}
