package org.luxons.sevenwonders.engine.data.serializers

import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.junit.Test
import org.luxons.sevenwonders.engine.resources.Resources
import org.luxons.sevenwonders.engine.resources.emptyResources
import org.luxons.sevenwonders.engine.resources.resourcesOf
import org.luxons.sevenwonders.model.resources.ResourceType.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResourcesSerializerTest {

    private val json: Json = Json {
        serializersModule = SerializersModule {
            contextual(Resources::class, ResourcesSerializer)
        }
    }

    @Test
    fun serialize_null() {
        assertEquals("null", json.encodeToString(ResourcesSerializer.nullable, null))
    }

    @Test
    fun serialize_emptyResourcesToNull() {
        val resources = emptyResources()
        assertEquals("null", json.encodeToString(resources))
    }

    @Test
    fun serialize_singleType() {
        val resources = resourcesOf(WOOD)
        assertEquals("\"W\"", json.encodeToString(resources))
    }

    @Test
    fun serialize_multipleTimesSameType() {
        val resources = resourcesOf(WOOD to 3)
        assertEquals("\"WWW\"", json.encodeToString(resources))
    }

    @Test
    fun serialize_mixedTypes() {
        val resources = resourcesOf(WOOD, STONE, CLAY)
        assertEquals("\"WSC\"", json.encodeToString(resources))
    }

    @Test
    fun serialize_mixedTypes_unordered() {
        val resources = resourcesOf(CLAY to 1, WOOD to 2, CLAY to 1, STONE to 1)
        assertEquals("\"CCWWS\"", json.encodeToString(resources))
    }

    @Test
    fun deserialize_null() {
        assertNull(json.decodeFromString<Resources?>("null"))
    }

    @Test
    fun deserialize_emptyList() {
        val resources = emptyResources()
        assertEquals(resources, json.decodeFromString("\"\""))
    }

    @Test
    fun deserialize_singleType() {
        val resources = resourcesOf(WOOD)
        assertEquals(resources, json.decodeFromString("\"W\""))
    }

    @Test
    fun deserialize_multipleTimesSameType() {
        val resources = resourcesOf(WOOD to 3)
        assertEquals(resources, json.decodeFromString("\"WWW\""))
    }

    @Test
    fun deserialize_mixedTypes() {
        val resources = resourcesOf(WOOD, CLAY, STONE)
        assertEquals(resources, json.decodeFromString("\"WCS\""))
    }

    @Test
    fun deserialize_mixedTypes_unordered() {
        val resources = resourcesOf(WOOD to 1, CLAY to 2, STONE to 3)
        assertEquals(resources, json.decodeFromString("\"SCWCSS\""))
    }
}
