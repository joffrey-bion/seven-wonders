package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.effects.ProductionIncrease;
import org.luxons.sevenwonders.game.resources.Production;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProductionIncreaseSerializerTest {

    private Gson gson;

    @Before
    public void setUp() {
        Type resourceTypeList = new TypeToken<List<ResourceType>>() {
        }.getType();
        gson = new GsonBuilder().registerTypeAdapter(Resources.class, new ResourcesSerializer())
                                .registerTypeAdapter(ResourceType.class, new ResourceTypeSerializer())
                                .registerTypeAdapter(resourceTypeList, new ResourceTypesSerializer())
                                .registerTypeAdapter(Production.class, new ProductionSerializer())
                                .registerTypeAdapter(ProductionIncrease.class, new ProductionIncreaseSerializer())
                                .create();
    }

    private static ProductionIncrease create(boolean sellable, int wood, int stone, int clay) {
        Production production = new Production();
        if (wood > 0) {
            production.addFixedResource(ResourceType.WOOD, wood);
        }
        if (stone > 0) {
            production.addFixedResource(ResourceType.STONE, stone);
        }
        if (clay > 0) {
            production.addFixedResource(ResourceType.CLAY, clay);
        }
        return new ProductionIncrease(production, sellable);
    }

    private static ProductionIncrease createChoice(boolean sellable, ResourceType... types) {
        Production production = new Production();
        production.addChoice(types);
        return new ProductionIncrease(production, sellable);
    }

    @Test
    public void serialize_nullAsNull() {
        assertEquals("null", gson.toJson(null, ProductionIncrease.class));
    }

    @Test
    public void serialize_emptyProdIncreaseAsNull() {
        ProductionIncrease prodIncrease = new ProductionIncrease(new Production(), false);
        assertEquals("null", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_singleType() {
        ProductionIncrease prodIncrease = create(true, 1, 0, 0);
        assertEquals("\"W\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_mixedTypes() {
        ProductionIncrease prodIncrease = create(true, 1, 1, 1);
        assertEquals("\"WSC\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_mixedTypes_notSellable() {
        ProductionIncrease prodIncrease = create(false, 1, 1, 1);
        assertEquals("\"(WSC)\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_choice2() {
        ProductionIncrease prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY);
        assertEquals("\"W/C\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_choice3() {
        ProductionIncrease prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY);
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_choice3_notSellable() {
        ProductionIncrease prodIncrease = createChoice(false, ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY);
        assertEquals("\"(W/O/C)\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_choice2_unordered() {
        ProductionIncrease prodIncrease = createChoice(true, ResourceType.CLAY, ResourceType.WOOD);
        assertEquals("\"W/C\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_choice3_unordered() {
        ProductionIncrease prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE);
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialize_failIfMultipleChoices() {
        ProductionIncrease prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY);
        prodIncrease.getProduction().addChoice(ResourceType.ORE, ResourceType.GLASS);
        gson.toJson(prodIncrease, ProductionIncrease.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialize_failIfMixedFixedAndChoices() {
        ProductionIncrease prodIncrease = create(true, 1, 0, 0);
        prodIncrease.getProduction().addChoice(ResourceType.WOOD, ResourceType.CLAY);
        gson.toJson(prodIncrease, ProductionIncrease.class);
    }

    @Test
    public void deserialize_nullFromNull() {
        assertNull(gson.fromJson("null", ProductionIncrease.class));
    }

    @Test
    public void deserialize_emptyList() {
        ProductionIncrease prodIncrease = new ProductionIncrease(new Production(), true);
        assertEquals(prodIncrease, gson.fromJson("\"\"", ProductionIncrease.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failOnGarbageString() {
        gson.fromJson("\"this is garbage\"", ProductionIncrease.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failOnGarbageStringWithSlashes() {
        gson.fromJson("\"this/is/garbage\"", ProductionIncrease.class);
    }

    @Test
    public void deserialize_singleType() {
        ProductionIncrease prodIncrease = create(true, 1, 0, 0);
        assertEquals(prodIncrease, gson.fromJson("\"W\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_multipleTimesSameType_notSellable() {
        ProductionIncrease prodIncrease = create(false, 3, 0, 0);
        assertEquals(prodIncrease, gson.fromJson("\"(WWW)\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_mixedTypes() {
        ProductionIncrease prodIncrease = create(true, 1, 1, 1);
        assertEquals(prodIncrease, gson.fromJson("\"WCS\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_choice2() {
        ProductionIncrease prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY);
        assertEquals(prodIncrease, gson.fromJson("\"W/C\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_choice3_notSellable() {
        ProductionIncrease prodIncrease = createChoice(false, ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY);
        assertEquals(prodIncrease, gson.fromJson("\"(W/O/C)\"", ProductionIncrease.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failOnMultipleResourcesInChoice() {
        gson.fromJson("\"W/SS/C\"", ProductionIncrease.class);
    }
}
