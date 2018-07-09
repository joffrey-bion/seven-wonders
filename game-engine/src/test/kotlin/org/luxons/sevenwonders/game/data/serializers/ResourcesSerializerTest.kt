package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources

class ResourcesSerializerTest {

    private var gson: Gson? = null

    @Before
    fun setUp() {
        gson = GsonBuilder().registerTypeAdapter(Resources::class.java, ResourcesSerializer()).create()
    }

    @Test
    fun serialize_null() {
        assertEquals("null", gson!!.toJson(null, Resources::class.java))
    }

    @Test
    fun serialize_emptyResourcesToNull() {
        val resources = Resources()
        assertEquals("null", gson!!.toJson(resources))
    }

    @Test
    fun serialize_singleType() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 1)
        assertEquals("\"W\"", gson!!.toJson(resources))
    }

    @Test
    fun serialize_multipleTimesSameType() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 3)
        assertEquals("\"WWW\"", gson!!.toJson(resources))
    }

    @Test
    fun serialize_mixedTypes() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 1)
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 1)
        assertEquals("\"WSC\"", gson!!.toJson(resources))
    }

    @Test
    fun serialize_mixedTypes_unordered() {
        val resources = Resources()
        resources.add(ResourceType.CLAY, 1)
        resources.add(ResourceType.WOOD, 2)
        resources.add(ResourceType.CLAY, 1)
        resources.add(ResourceType.STONE, 1)
        assertEquals("\"CCWWS\"", gson!!.toJson(resources))
    }

    @Test
    fun deserialize_null() {
        assertNull(gson!!.fromJson("null", Resources::class.java))
    }

    @Test
    fun deserialize_emptyList() {
        val resources = Resources()
        assertEquals(resources, gson!!.fromJson("\"\"", Resources::class.java))
    }

    @Test
    fun deserialize_singleType() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 1)
        assertEquals(resources, gson!!.fromJson("\"W\"", Resources::class.java))
    }

    @Test
    fun deserialize_multipleTimesSameType() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 3)
        assertEquals(resources, gson!!.fromJson("\"WWW\"", Resources::class.java))
    }

    @Test
    fun deserialize_mixedTypes() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 1)
        resources.add(ResourceType.CLAY, 1)
        resources.add(ResourceType.STONE, 1)
        assertEquals(resources, gson!!.fromJson("\"WCS\"", Resources::class.java))
    }

    @Test
    fun deserialize_mixedTypes_unordered() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 1)
        resources.add(ResourceType.CLAY, 2)
        resources.add(ResourceType.STONE, 3)
        assertEquals(resources, gson!!.fromJson("\"SCWCSS\"", Resources::class.java))
    }
}
