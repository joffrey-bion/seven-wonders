package org.luxons.sevenwonders.game.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.luxons.sevenwonders.game.data.definitions.DecksDefinition;
import org.luxons.sevenwonders.game.data.definitions.GameDefinition;
import org.luxons.sevenwonders.game.data.definitions.WonderDefinition;
import org.luxons.sevenwonders.game.data.serializers.NumericEffectSerializer;
import org.luxons.sevenwonders.game.data.serializers.ProductionIncreaseSerializer;
import org.luxons.sevenwonders.game.data.serializers.ResourceTypeSerializer;
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
import org.springframework.stereotype.Component;

@Component
public class GameDefinitionLoader {

    private static final String BASE_PACKAGE = GameDefinitionLoader.class.getPackage().getName();

    private static final String BASE_PACKAGE_PATH = '/' + BASE_PACKAGE.replace('.', '/');

    private static final String CARDS_FILE = "cards.json";

    private static final String WONDERS_FILE = "wonders.json";

    private final GameDefinition gameDefinition;

    public GameDefinitionLoader() {
        gameDefinition = new GameDefinition(loadWonders(), loadDecks());
    }

    public GameDefinition getGameDefinition() {
        return gameDefinition;
    }

    public static GameDefinition load() {
        return new GameDefinition(loadWonders(), loadDecks());
    }

    private static WonderDefinition[] loadWonders() {
        return readJsonFile(WONDERS_FILE, WonderDefinition[].class);
    }

    private static DecksDefinition loadDecks() {
        return readJsonFile(CARDS_FILE, DecksDefinition.class);
    }

    private static <T> T readJsonFile(String filename, Class<T> clazz) {
        InputStream in = GameDefinitionLoader.class.getResourceAsStream(BASE_PACKAGE_PATH + '/' + filename);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = createGson();
        return gson.fromJson(reader, clazz);
    }

    private static Gson createGson() {
        Type resourceTypeList = new TypeToken<List<ResourceType>>() {}.getType();
        return new GsonBuilder().disableHtmlEscaping()
                                .registerTypeAdapter(Resources.class, new ResourcesSerializer())
                                .registerTypeAdapter(ResourceType.class, new ResourceTypeSerializer())
                                .registerTypeAdapter(resourceTypeList, new ResourceTypesSerializer())
                                .registerTypeAdapter(ProductionIncrease.class, new ProductionIncreaseSerializer())
                                .registerTypeAdapter(MilitaryReinforcements.class, new NumericEffectSerializer())
                                .registerTypeAdapter(RawPointsIncrease.class, new NumericEffectSerializer())
                                .registerTypeAdapter(GoldIncrease.class, new NumericEffectSerializer())
                                .registerTypeAdapter(ScienceProgress.class, new ScienceProgressSerializer())
                                .create();
    }
}
