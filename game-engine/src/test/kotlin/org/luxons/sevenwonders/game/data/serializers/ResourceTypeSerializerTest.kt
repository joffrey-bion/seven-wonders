package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.resources.ResourceType

class ResourceTypeSerializerTest {

    private var gson: Gson? = null

    @Before
    fun setUp() {
        gson = GsonBuilder().registerTypeAdapter(ResourceType::class.java, ResourceTypeSerializer()).create()
    }

    @Test
    fun serialize_useSymbolForEachType() {
        for (type in ResourceType.values()) {
            val expectedJson = "\"" + type.symbol + "\""
            assertEquals(expectedJson, gson!!.toJson(type))
        }
    }

    @Test
    fun deserialize_useSymbolForEachType() {
        for (type in ResourceType.values()) {
            val typeInJson = "\"" + type.symbol + "\""
            assertEquals(type, gson!!.fromJson(typeInJson, ResourceType::class.java))
        }
    }

    @Test
    fun deserialize_nullFromNull() {
        assertNull(gson!!.fromJson("null", ResourceType::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failsOnEmptyString() {
        gson!!.fromJson("\"\"", ResourceType::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failsOnGarbageString() {
        gson!!.fromJson("\"thisisgarbage\"", ResourceType::class.java)
    }
}
