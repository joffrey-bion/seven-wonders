package org.luxons.sevenwonders.game.data.serializers;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.resources.ResourceType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ResourceTypeSerializerTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder().registerTypeAdapter(ResourceType.class, new ResourceTypeSerializer()).create();
    }

    @Test
    public void serialize_useSymbolForEachType() {
        for (ResourceType type : ResourceType.values()) {
            String expectedJson = "\"" + type.getSymbol() + "\"";
            assertEquals(expectedJson, gson.toJson(type));
        }
    }

    @Test
    public void deserialize_useSymbolForEachType() {
        for (ResourceType type : ResourceType.values()) {
            String typeInJson = "\"" + type.getSymbol() + "\"";
            assertEquals(type, gson.fromJson(typeInJson, ResourceType.class));
        }
    }

    @Test
    public void deserialize_nullFromNull() {
        assertNull(gson.fromJson("null", ResourceType.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failsOnEmptyString() {
        gson.fromJson("\"\"", ResourceType.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failsOnGarbageString() {
        gson.fromJson("\"thisisgarbage\"", ResourceType.class);
    }
}
