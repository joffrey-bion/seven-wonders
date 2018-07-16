package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.resources.MutableResources
import org.luxons.sevenwonders.game.resources.ResourceType.*
import org.luxons.sevenwonders.game.resources.Resources
import org.luxons.sevenwonders.game.resources.emptyResources
import org.luxons.sevenwonders.game.resources.resourcesOf

class ResourcesSerializerTest {

    private var gson: Gson? = null

    @Before
    fun setUp() {
        gson = GsonBuilder()
            .registerTypeAdapter(Resources::class.java, ResourcesSerializer())
            .registerTypeAdapter(MutableResources::class.java, ResourcesSerializer())
            .create()
    }

    @Test
    fun serialize_null() {
        assertEquals("null", gson!!.toJson(null, Resources::class.java))
    }

    @Test
    fun serialize_emptyResourcesToNull() {
        val resources = emptyResources()
        assertEquals("null", gson!!.toJson(resources))
    }

    @Test
    fun serialize_singleType() {
        val resources = resourcesOf(WOOD)
        assertEquals("\"W\"", gson!!.toJson(resources))
    }

    @Test
    fun serialize_multipleTimesSameType() {
        val resources = resourcesOf(WOOD to 3)
        assertEquals("\"WWW\"", gson!!.toJson(resources))
    }

    @Test
    fun serialize_mixedTypes() {
        val resources = resourcesOf(WOOD, STONE, CLAY)
        assertEquals("\"WSC\"", gson!!.toJson(resources))
    }

    @Test
    fun serialize_mixedTypes_unordered() {
        val resources = resourcesOf(CLAY to 1, WOOD to 2, CLAY to 1, STONE to 1)
        assertEquals("\"CCWWS\"", gson!!.toJson(resources))
    }

    @Test
    fun deserialize_null() {
        assertNull(gson!!.fromJson("null", Resources::class.java))
    }

    @Test
    fun deserialize_emptyList() {
        val resources = emptyResources()
        assertEquals(resources, gson!!.fromJson("\"\"", Resources::class.java))
    }

    @Test
    fun deserialize_singleType() {
        val resources = resourcesOf(WOOD)
        assertEquals(resources, gson!!.fromJson("\"W\"", Resources::class.java))
    }

    @Test
    fun deserialize_multipleTimesSameType() {
        val resources = resourcesOf(WOOD to 3)
        assertEquals(resources, gson!!.fromJson("\"WWW\"", Resources::class.java))
    }

    @Test
    fun deserialize_mixedTypes() {
        val resources = resourcesOf(WOOD, CLAY, STONE)
        assertEquals(resources, gson!!.fromJson("\"WCS\"", Resources::class.java))
    }

    @Test
    fun deserialize_mixedTypes_unordered() {
        val resources = resourcesOf(WOOD to 1, CLAY to 2, STONE to 3)
        assertEquals(resources, gson!!.fromJson("\"SCWCSS\"", Resources::class.java))
    }
}
