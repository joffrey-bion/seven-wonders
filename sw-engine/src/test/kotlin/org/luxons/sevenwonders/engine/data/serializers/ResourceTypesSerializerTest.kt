package org.luxons.sevenwonders.engine.data.serializers

import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.Json
import org.junit.Test
import org.luxons.sevenwonders.model.resources.ResourceType
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResourceTypesSerializerTest {

    @Test
    fun serialize_null() {
        assertEquals("null", Json.encodeToString(ResourceTypesSerializer.nullable, null))
    }

    @Test
    fun serialize_emptyList() {
        val types = emptyList<ResourceType>()
        assertEquals("\"\"", Json.encodeToString(ResourceTypesSerializer, types))
    }

    @Test
    fun serialize_singleType() {
        val types = listOf(ResourceType.WOOD)
        assertEquals("\"W\"", Json.encodeToString(ResourceTypesSerializer, types))
    }

    @Test
    fun serialize_multipleTimesSameType() {
        val types = List(3) { ResourceType.WOOD }
        assertEquals("\"WWW\"", Json.encodeToString(ResourceTypesSerializer, types))
    }

    @Test
    fun serialize_mixedTypes() {
        val types = listOf(ResourceType.WOOD, ResourceType.CLAY, ResourceType.STONE)
        assertEquals("\"WCS\"", Json.encodeToString(ResourceTypesSerializer, types))
    }

    @Test
    fun deserialize_null() {
        assertNull(Json.decodeFromString(ResourceTypesSerializer.nullable, "null"))
    }

    @Test
    fun deserialize_emptyList() {
        val types = emptyList<ResourceType>()
        assertEquals(types, Json.decodeFromString(ResourceTypesSerializer, "\"\""))
    }

    @Test
    fun deserialize_singleType() {
        val types = listOf(ResourceType.WOOD)
        assertEquals(types, Json.decodeFromString(ResourceTypesSerializer, "\"W\""))
    }

    @Test
    fun deserialize_multipleTimesSameType() {
        val types = List(3) { ResourceType.WOOD }
        assertEquals(types, Json.decodeFromString(ResourceTypesSerializer, "\"WWW\""))
    }

    @Test
    fun deserialize_mixedTypes() {
        val types = listOf(ResourceType.WOOD, ResourceType.CLAY, ResourceType.STONE)
        assertEquals(types, Json.decodeFromString(ResourceTypesSerializer, "\"WCS\""))
    }
}
