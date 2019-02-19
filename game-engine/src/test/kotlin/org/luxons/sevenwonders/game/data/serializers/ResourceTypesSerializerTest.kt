package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.resources.ResourceType
import java.lang.reflect.Type
import java.util.ArrayList

class ResourceTypesSerializerTest {

    private lateinit var gson: Gson
    
    @Before
    fun setUp() {
        gson = GsonBuilder().registerTypeAdapter(createListTypeToken(), ResourceTypesSerializer()).create()
    }

    private fun createListTypeToken(): Type {
        return object : TypeToken<List<ResourceType>>() {}.type
    }

    @Test
    fun serialize_null() {
        assertEquals("null", gson.toJson(null, createListTypeToken()))
    }

    @Test
    fun serialize_emptyList() {
        val types = ArrayList<ResourceType>()
        assertEquals("\"\"", gson.toJson(types, createListTypeToken()))
    }

    @Test
    fun serialize_singleType() {
        val types = ArrayList<ResourceType>()
        types.add(ResourceType.WOOD)
        assertEquals("\"W\"", gson.toJson(types, createListTypeToken()))
    }

    @Test
    fun serialize_multipleTimesSameType() {
        val types = ArrayList<ResourceType>()
        types.add(ResourceType.WOOD)
        types.add(ResourceType.WOOD)
        types.add(ResourceType.WOOD)
        assertEquals("\"WWW\"", gson.toJson(types, createListTypeToken()))
    }

    @Test
    fun serialize_mixedTypes() {
        val types = ArrayList<ResourceType>()
        types.add(ResourceType.WOOD)
        types.add(ResourceType.CLAY)
        types.add(ResourceType.STONE)
        assertEquals("\"WCS\"", gson.toJson(types, createListTypeToken()))
    }

    @Test
    fun deserialize_null() {
        assertNull(gson.fromJson("null", createListTypeToken()))
    }

    @Test
    fun deserialize_emptyList() {
        val types = ArrayList<ResourceType>()
        assertEquals(types, gson.fromJson("\"\"", createListTypeToken()))
    }

    @Test
    fun deserialize_singleType() {
        val types = ArrayList<ResourceType>()
        types.add(ResourceType.WOOD)
        assertEquals(types, gson.fromJson("\"W\"", createListTypeToken()))
    }

    @Test
    fun deserialize_multipleTimesSameType() {
        val types = ArrayList<ResourceType>()
        types.add(ResourceType.WOOD)
        types.add(ResourceType.WOOD)
        types.add(ResourceType.WOOD)
        assertEquals(types, gson.fromJson("\"WWW\"", createListTypeToken()))
    }

    @Test
    fun deserialize_mixedTypes() {
        val types = ArrayList<ResourceType>()
        types.add(ResourceType.WOOD)
        types.add(ResourceType.CLAY)
        types.add(ResourceType.STONE)
        assertEquals(types, gson.fromJson("\"WCS\"", createListTypeToken()))
    }
}
