package org.luxons.sevenwonders.engine.data.serializers

import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.Json
import org.junit.Test
import org.luxons.sevenwonders.model.resources.ResourceType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ResourceTypeSerializerTest {

    @Test
    fun serialize_useSymbolForEachType() {
        ResourceType.values().forEach { type ->
            val expectedjson = "\"" + type.symbol + "\""
            assertEquals(expectedjson, Json.encodeToString(ResourceTypeSerializer, type))
        }
    }

    @Test
    fun deserialize_useSymbolForEachType() {
        ResourceType.values().forEach { type ->
            val typeInjson = "\"" + type.symbol + "\""
            assertEquals(type, Json.decodeFromString(ResourceTypeSerializer, typeInjson))
        }
    }

    @Test
    fun deserialize_nullFromNull() {
        assertNull(Json.decodeFromString(ResourceTypeSerializer.nullable, "null"))
    }

    @Test
    fun deserialize_failsOnEmptyString() {
        assertFailsWith<IllegalArgumentException> {
            Json.decodeFromString(ResourceTypeSerializer, "\"\"")
        }
    }

    @Test
    fun deserialize_failsOnGarbageString() {
        assertFailsWith<IllegalArgumentException> {
            Json.decodeFromString(ResourceTypeSerializer, "\"thisisgarbage\"")
        }
    }
}
