package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.resources.Production;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProductionSerializerTest {

    private Gson gson;

    @Before
    public void setUp() {
        Type resourceTypeList = new TypeToken<List<ResourceType>>() {
        }.getType();
        gson = new GsonBuilder().registerTypeAdapter(Resources.class, new ResourcesSerializer())
                                .registerTypeAdapter(ResourceType.class, new ResourceTypeSerializer())
                                .registerTypeAdapter(resourceTypeList, new ResourceTypesSerializer())
                                .registerTypeAdapter(Production.class, new ProductionSerializer())
                                .create();
    }

    private static Production create(int wood, int stone, int clay) {
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
        return production;
    }

    private static Production createChoice(ResourceType... types) {
        Production production = new Production();
        production.addChoice(types);
        return production;
    }

    @Test
    public void serialize_nullAsNull() {
        assertEquals("null", gson.toJson(null, Production.class));
    }

    @Test
    public void serialize_emptyProdIncreaseAsNull() {
        Production prodIncrease = new Production();
        assertEquals("null", gson.toJson(prodIncrease, Production.class));
    }

    @Test
    public void serialize_singleType() {
        Production prodIncrease = create(1, 0, 0);
        assertEquals("\"W\"", gson.toJson(prodIncrease, Production.class));
    }

    @Test
    public void serialize_multipleTimesSameType() {
        Production prodIncrease = create(3, 0, 0);
        assertEquals("\"WWW\"", gson.toJson(prodIncrease, Production.class));
    }

    @Test
    public void serialize_mixedTypes() {
        Production prodIncrease = create(1, 1, 1);
        assertEquals("\"WSC\"", gson.toJson(prodIncrease, Production.class));
    }

    @Test
    public void serialize_mixedTypesMultiple() {
        Production prodIncrease = create(2, 1, 2);
        assertEquals("\"WWSCC\"", gson.toJson(prodIncrease, Production.class));
    }

    @Test
    public void serialize_choice2() {
        Production prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY);
        assertEquals("\"W/C\"", gson.toJson(prodIncrease, Production.class));
    }

    @Test
    public void serialize_choice3() {
        Production prodIncrease = createChoice(ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY);
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease, Production.class));
    }

    @Test
    public void serialize_choice2_unordered() {
        Production prodIncrease = createChoice(ResourceType.CLAY, ResourceType.WOOD);
        assertEquals("\"W/C\"", gson.toJson(prodIncrease, Production.class));
    }

    @Test
    public void serialize_choice3_unordered() {
        Production prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE);
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease, Production.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialize_failIfMultipleChoices() {
        Production production = createChoice(ResourceType.WOOD, ResourceType.CLAY);
        production.addChoice(ResourceType.ORE, ResourceType.GLASS);
        gson.toJson(production, Production.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialize_failIfMixedFixedAndChoices() {
        Production production = create(1, 0, 0);
        production.addChoice(ResourceType.WOOD, ResourceType.CLAY);
        gson.toJson(production, Production.class);
    }

    @Test
    public void deserialize_nullFromNull() {
        assertNull(gson.fromJson("null", Production.class));
    }

    @Test
    public void deserialize_emptyList() {
        Production prodIncrease = new Production();
        assertEquals(prodIncrease, gson.fromJson("\"\"", Production.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failOnGarbageString() {
        gson.fromJson("\"this is garbage\"", Production.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failOnGarbageStringWithSlashes() {
        gson.fromJson("\"this/is/garbage\"", Production.class);
    }

    @Test
    public void deserialize_singleType() {
        Production prodIncrease = create(1, 0, 0);
        assertEquals(prodIncrease, gson.fromJson("\"W\"", Production.class));
    }

    @Test
    public void deserialize_multipleTimesSameType() {
        Production prodIncrease = create(3, 0, 0);
        assertEquals(prodIncrease, gson.fromJson("\"WWW\"", Production.class));
    }

    @Test
    public void deserialize_mixedTypes() {
        Production prodIncrease = create(1, 1, 1);
        assertEquals(prodIncrease, gson.fromJson("\"WCS\"", Production.class));
    }

    @Test
    public void deserialize_mixedTypes_unordered() {
        Production prodIncrease = create(1, 3, 2);
        assertEquals(prodIncrease, gson.fromJson("\"SCWCSS\"", Production.class));
    }

    @Test
    public void deserialize_choice2() {
        Production prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY);
        assertEquals(prodIncrease, gson.fromJson("\"W/C\"", Production.class));
    }

    @Test
    public void deserialize_choice3() {
        Production prodIncrease = createChoice(ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY);
        assertEquals(prodIncrease, gson.fromJson("\"W/O/C\"", Production.class));
    }

    @Test
    public void deserialize_choice2_unordered() {
        Production prodIncrease = createChoice(ResourceType.CLAY, ResourceType.WOOD);
        assertEquals(prodIncrease, gson.fromJson("\"W/C\"", Production.class));
    }

    @Test
    public void deserialize_choice3_unordered() {
        Production prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE);
        assertEquals(prodIncrease, gson.fromJson("\"W/O/C\"", Production.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failOnMultipleResourcesInChoice() {
        gson.fromJson("\"W/SS/C\"", Production.class);
    }
}
