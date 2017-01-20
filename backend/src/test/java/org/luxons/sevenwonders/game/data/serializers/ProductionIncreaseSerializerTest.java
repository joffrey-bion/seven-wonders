package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.effects.ProductionIncrease;
import org.luxons.sevenwonders.game.resources.Production;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import static org.junit.Assert.*;

public class ProductionIncreaseSerializerTest {

    private Gson gson;

    @Before
    public void setUp() {
        Type resourceTypeList = new TypeToken<List<ResourceType>>() {
        }.getType();
        gson = new GsonBuilder().registerTypeAdapter(Resources.class, new ResourcesSerializer())
                                .registerTypeAdapter(ResourceType.class, new ResourceTypeSerializer())
                                .registerTypeAdapter(resourceTypeList, new ResourceTypesSerializer())
                                .registerTypeAdapter(ProductionIncrease.class, new ProductionIncreaseSerializer())
                                .create();
    }

    private static ProductionIncrease create(int wood, int stone, int clay) {
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
        ProductionIncrease prodIncrease = new ProductionIncrease();
        prodIncrease.setProduction(production);
        return prodIncrease;
    }

    private static ProductionIncrease createChoice(ResourceType... types) {
        Production production = new Production();
        production.addChoice(types);
        ProductionIncrease prodIncrease = new ProductionIncrease();
        prodIncrease.setProduction(production);
        return prodIncrease;
    }

    @Test
    public void serialize_nullAsNull() {
        assertEquals("null", gson.toJson(null, ProductionIncrease.class));
    }

    @Test
    public void serialize_emptyProdIncreaseAsNull() {
        ProductionIncrease prodIncrease = new ProductionIncrease();
        assertEquals("null", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_singleType() {
        ProductionIncrease prodIncrease = create(1, 0, 0);
        assertEquals("\"W\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_multipleTimesSameType() {
        ProductionIncrease prodIncrease = create(3, 0, 0);
        assertEquals("\"WWW\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_mixedTypes() {
        ProductionIncrease prodIncrease = create(1, 1, 1);
        assertEquals("\"WSC\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_mixedTypesMultiple() {
        ProductionIncrease prodIncrease = create(2, 1, 2);
        assertEquals("\"WWSCC\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_choice2() {
        ProductionIncrease prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY);
        assertEquals("\"W/C\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_choice3() {
        ProductionIncrease prodIncrease = createChoice(ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY);
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_choice2_unordered() {
        ProductionIncrease prodIncrease = createChoice(ResourceType.CLAY, ResourceType.WOOD);
        assertEquals("\"W/C\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test
    public void serialize_choice3_unordered() {
        ProductionIncrease prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE);
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease, ProductionIncrease.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialize_failIfMultipleChoices() {
        ProductionIncrease prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY);
        prodIncrease.getProduction().addChoice(ResourceType.ORE, ResourceType.GLASS);
        gson.toJson(prodIncrease, ProductionIncrease.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialize_failIfMixedFixedAndChoices() {
        ProductionIncrease prodIncrease = create(1, 0, 0);
        prodIncrease.getProduction().addChoice(ResourceType.WOOD, ResourceType.CLAY);
        gson.toJson(prodIncrease, ProductionIncrease.class);
    }

    @Test
    public void deserialize_nullFromNull() {
        assertNull(gson.fromJson("null", ProductionIncrease.class));
    }

    @Test
    public void deserialize_emptyList() {
        ProductionIncrease prodIncrease = new ProductionIncrease();
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
        ProductionIncrease prodIncrease = create(1, 0, 0);
        assertEquals(prodIncrease, gson.fromJson("\"W\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_multipleTimesSameType() {
        ProductionIncrease prodIncrease = create(3, 0, 0);
        assertEquals(prodIncrease, gson.fromJson("\"WWW\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_mixedTypes() {
        ProductionIncrease prodIncrease = create(1, 1, 1);
        assertEquals(prodIncrease, gson.fromJson("\"WCS\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_mixedTypes_unordered() {
        ProductionIncrease prodIncrease = create(1, 3, 2);
        assertEquals(prodIncrease, gson.fromJson("\"SCWCSS\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_choice2() {
        ProductionIncrease prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY);
        assertEquals(prodIncrease, gson.fromJson("\"W/C\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_choice3() {
        ProductionIncrease prodIncrease = createChoice(ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY);
        assertEquals(prodIncrease, gson.fromJson("\"W/O/C\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_choice2_unordered() {
        ProductionIncrease prodIncrease = createChoice(ResourceType.CLAY, ResourceType.WOOD);
        assertEquals(prodIncrease, gson.fromJson("\"W/C\"", ProductionIncrease.class));
    }

    @Test
    public void deserialize_choice3_unordered() {
        ProductionIncrease prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE);
        assertEquals(prodIncrease, gson.fromJson("\"W/O/C\"", ProductionIncrease.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failOnMultipleResourcesInChoice() {
        gson.fromJson("\"W/SS/C\"", ProductionIncrease.class);
    }
}