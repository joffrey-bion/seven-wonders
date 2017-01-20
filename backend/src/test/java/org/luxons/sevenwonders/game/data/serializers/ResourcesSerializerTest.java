package org.luxons.sevenwonders.game.data.serializers;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import static org.junit.Assert.*;

public class ResourcesSerializerTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder().registerTypeAdapter(Resources.class, new ResourcesSerializer()).create();
    }

    @Test
    public void serialize_null() {
        assertEquals("null", gson.toJson(null, Resources.class));
    }

    @Test
    public void serialize_emptyResourcesToNull() {
        Resources resources = new Resources();
        assertEquals("null", gson.toJson(resources));
    }

    @Test
    public void serialize_singleType() {
        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 1);
        assertEquals("\"W\"", gson.toJson(resources));
    }

    @Test
    public void serialize_multipleTimesSameType() {
        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 3);
        assertEquals("\"WWW\"", gson.toJson(resources));
    }

    @Test
    public void serialize_mixedTypes() {
        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 1);
        resources.add(ResourceType.STONE, 1);
        resources.add(ResourceType.CLAY, 1);
        assertEquals("\"WSC\"", gson.toJson(resources));
    }

    @Test
    public void serialize_mixedTypes_unordered() {
        Resources resources = new Resources();
        resources.add(ResourceType.CLAY, 1);
        resources.add(ResourceType.WOOD, 2);
        resources.add(ResourceType.CLAY, 1);
        resources.add(ResourceType.STONE, 1);
        assertEquals("\"WWSCC\"", gson.toJson(resources));
    }

    @Test
    public void deserialize_null() {
        assertNull(gson.fromJson("null", Resources.class));
    }

    @Test
    public void deserialize_emptyList() {
        Resources resources = new Resources();
        assertEquals(resources, gson.fromJson("\"\"", Resources.class));
    }

    @Test
    public void deserialize_singleType() {
        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 1);
        assertEquals(resources, gson.fromJson("\"W\"", Resources.class));
    }

    @Test
    public void deserialize_multipleTimesSameType() {
        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 3);
        assertEquals(resources, gson.fromJson("\"WWW\"", Resources.class));
    }

    @Test
    public void deserialize_mixedTypes() {
        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 1);
        resources.add(ResourceType.CLAY, 1);
        resources.add(ResourceType.STONE, 1);
        assertEquals(resources, gson.fromJson("\"WCS\"", Resources.class));
    }

    @Test
    public void deserialize_mixedTypes_unordered() {
        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 1);
        resources.add(ResourceType.CLAY, 2);
        resources.add(ResourceType.STONE, 3);
        assertEquals(resources, gson.fromJson("\"SCWCSS\"", Resources.class));
    }
}