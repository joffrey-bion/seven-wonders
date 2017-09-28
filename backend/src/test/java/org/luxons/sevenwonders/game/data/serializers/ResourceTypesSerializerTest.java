package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.resources.ResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ResourceTypesSerializerTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder().registerTypeAdapter(createListTypeToken(), new ResourceTypesSerializer()).create();
    }

    private static Type createListTypeToken() {
        return new TypeToken<List<ResourceType>>() {
        }.getType();
    }

    @Test
    public void serialize_null() {
        assertEquals("null", gson.toJson(null, createListTypeToken()));
    }

    @Test
    public void serialize_emptyList() {
        List<ResourceType> types = new ArrayList<>();
        assertEquals("\"\"", gson.toJson(types, createListTypeToken()));
    }

    @Test
    public void serialize_singleType() {
        List<ResourceType> types = new ArrayList<>();
        types.add(ResourceType.WOOD);
        assertEquals("\"W\"", gson.toJson(types, createListTypeToken()));
    }

    @Test
    public void serialize_multipleTimesSameType() {
        List<ResourceType> types = new ArrayList<>();
        types.add(ResourceType.WOOD);
        types.add(ResourceType.WOOD);
        types.add(ResourceType.WOOD);
        assertEquals("\"WWW\"", gson.toJson(types, createListTypeToken()));
    }

    @Test
    public void serialize_mixedTypes() {
        List<ResourceType> types = new ArrayList<>();
        types.add(ResourceType.WOOD);
        types.add(ResourceType.CLAY);
        types.add(ResourceType.STONE);
        assertEquals("\"WCS\"", gson.toJson(types, createListTypeToken()));
    }

    @Test
    public void deserialize_null() {
        assertNull(gson.fromJson("null", createListTypeToken()));
    }

    @Test
    public void deserialize_emptyList() {
        List<ResourceType> types = new ArrayList<>();
        assertEquals(types, gson.fromJson("\"\"", createListTypeToken()));
    }

    @Test
    public void deserialize_singleType() {
        List<ResourceType> types = new ArrayList<>();
        types.add(ResourceType.WOOD);
        assertEquals(types, gson.fromJson("\"W\"", createListTypeToken()));
    }

    @Test
    public void deserialize_multipleTimesSameType() {
        List<ResourceType> types = new ArrayList<>();
        types.add(ResourceType.WOOD);
        types.add(ResourceType.WOOD);
        types.add(ResourceType.WOOD);
        assertEquals(types, gson.fromJson("\"WWW\"", createListTypeToken()));
    }

    @Test
    public void deserialize_mixedTypes() {
        List<ResourceType> types = new ArrayList<>();
        types.add(ResourceType.WOOD);
        types.add(ResourceType.CLAY);
        types.add(ResourceType.STONE);
        assertEquals(types, gson.fromJson("\"WCS\"", createListTypeToken()));
    }
}
