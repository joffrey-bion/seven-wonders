package org.luxons.sevenwonders.game.data.serializers

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.typeToken
import com.github.salomonbrys.kotson.typedToJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.api.resources.ResourceType
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResourceTypesSerializerTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = GsonBuilder().registerTypeAdapter(typeToken<List<ResourceType>>(), ResourceTypesSerializer()).create()
    }

    @Test
    fun serialize_null() {
        assertEquals("null", gson.toJson(null, typeToken<List<ResourceType>>()))
    }

    @Test
    fun serialize_emptyList() {
        val types = emptyList<ResourceType>()
        assertEquals("\"\"", gson.typedToJson(types))
    }

    @Test
    fun serialize_singleType() {
        val types = listOf(ResourceType.WOOD)
        assertEquals("\"W\"", gson.typedToJson(types))
    }

    @Test
    fun serialize_multipleTimesSameType() {
        val types = List(3) { ResourceType.WOOD }
        assertEquals("\"WWW\"", gson.typedToJson(types))
    }

    @Test
    fun serialize_mixedTypes() {
        val types = listOf(ResourceType.WOOD, ResourceType.CLAY, ResourceType.STONE)
        assertEquals("\"WCS\"", gson.typedToJson(types))
    }

    @Test
    fun deserialize_null() {
        assertNull(gson.fromJson("null", typeToken<List<ResourceType>>()))
    }

    @Test
    fun deserialize_emptyList() {
        val types = emptyList<ResourceType>()
        assertEquals(types, gson.fromJson("\"\""))
    }

    @Test
    fun deserialize_singleType() {
        val types = listOf(ResourceType.WOOD)
        assertEquals(types, gson.fromJson("\"W\""))
    }

    @Test
    fun deserialize_multipleTimesSameType() {
        val types = List(3) { ResourceType.WOOD }
        assertEquals(types, gson.fromJson("\"WWW\""))
    }

    @Test
    fun deserialize_mixedTypes() {
        val types = listOf(ResourceType.WOOD, ResourceType.CLAY, ResourceType.STONE)
        assertEquals(types, gson.fromJson("\"WCS\""))
    }
}
