package org.luxons.sevenwonders.game.data.serializers

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.resources.ResourceType
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResourceTypeSerializerTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = GsonBuilder().registerTypeAdapter(ResourceType::class.java, ResourceTypeSerializer()).create()
    }

    @Test
    fun serialize_useSymbolForEachType() {
        ResourceType.values().forEach { type ->
            val expectedJson = "\"" + type.symbol + "\""
            assertEquals(expectedJson, gson.toJson(type))
        }
    }

    @Test
    fun deserialize_useSymbolForEachType() {
        ResourceType.values().forEach { type ->
            val typeInJson = "\"" + type.symbol + "\""
            assertEquals(type, gson.fromJson(typeInJson))
        }
    }

    @Test
    fun deserialize_nullFromNull() {
        assertNull(gson.fromJson("null", ResourceType::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failsOnEmptyString() {
        gson.fromJson<ResourceType>("\"\"")
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failsOnGarbageString() {
        gson.fromJson<ResourceType>("\"thisisgarbage\"")
    }
}
