package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.resources.MutableResources
import org.luxons.sevenwonders.game.resources.Production
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources

class ProductionSerializerTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        val resourceTypeList = object : TypeToken<List<ResourceType>>() {}.type
        gson = GsonBuilder().registerTypeAdapter(Resources::class.java, ResourcesSerializer())
            .registerTypeAdapter(MutableResources::class.java, ResourcesSerializer())
            .registerTypeAdapter(ResourceType::class.java, ResourceTypeSerializer())
            .registerTypeAdapter(resourceTypeList, ResourceTypesSerializer())
            .registerTypeAdapter(Production::class.java, ProductionSerializer())
            .create()
    }

    private fun create(wood: Int, stone: Int, clay: Int): Production {
        val production = Production()
        if (wood > 0) {
            production.addFixedResource(ResourceType.WOOD, wood)
        }
        if (stone > 0) {
            production.addFixedResource(ResourceType.STONE, stone)
        }
        if (clay > 0) {
            production.addFixedResource(ResourceType.CLAY, clay)
        }
        return production
    }

    private fun createChoice(vararg types: ResourceType): Production {
        val production = Production()
        production.addChoice(*types)
        return production
    }

    @Test
    fun serialize_nullAsNull() {
        assertEquals("null", gson.toJson(null, Production::class.java))
    }

    @Test
    fun serialize_emptyProdIncreaseAsNull() {
        val prodIncrease = Production()
        assertEquals("null", gson.toJson(prodIncrease, Production::class.java))
    }

    @Test
    fun serialize_singleType() {
        val prodIncrease = create(1, 0, 0)
        assertEquals("\"W\"", gson.toJson(prodIncrease, Production::class.java))
    }

    @Test
    fun serialize_multipleTimesSameType() {
        val prodIncrease = create(3, 0, 0)
        assertEquals("\"WWW\"", gson.toJson(prodIncrease, Production::class.java))
    }

    @Test
    fun serialize_mixedTypes() {
        val prodIncrease = create(1, 1, 1)
        assertEquals("\"WSC\"", gson.toJson(prodIncrease, Production::class.java))
    }

    @Test
    fun serialize_mixedTypesMultiple() {
        val prodIncrease = create(2, 1, 2)
        assertEquals("\"WWSCC\"", gson.toJson(prodIncrease, Production::class.java))
    }

    @Test
    fun serialize_choice2() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY)
        assertEquals("\"W/C\"", gson.toJson(prodIncrease, Production::class.java))
    }

    @Test
    fun serialize_choice3() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease, Production::class.java))
    }

    @Test
    fun serialize_choice2_unordered() {
        val prodIncrease = createChoice(ResourceType.CLAY, ResourceType.WOOD)
        assertEquals("\"W/C\"", gson.toJson(prodIncrease, Production::class.java))
    }

    @Test
    fun serialize_choice3_unordered() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE)
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease, Production::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun serialize_failIfMultipleChoices() {
        val production = createChoice(ResourceType.WOOD, ResourceType.CLAY)
        production.addChoice(ResourceType.ORE, ResourceType.GLASS)
        gson.toJson(production, Production::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun serialize_failIfMixedFixedAndChoices() {
        val production = create(1, 0, 0)
        production.addChoice(ResourceType.WOOD, ResourceType.CLAY)
        gson.toJson(production, Production::class.java)
    }

    @Test
    fun deserialize_nullFromNull() {
        assertNull(gson.fromJson("null", Production::class.java))
    }

    @Test
    fun deserialize_emptyList() {
        val prodIncrease = Production()
        assertEquals(prodIncrease, gson.fromJson("\"\"", Production::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failOnGarbageString() {
        gson.fromJson("\"this is garbage\"", Production::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failOnGarbageStringWithSlashes() {
        gson.fromJson("\"this/is/garbage\"", Production::class.java)
    }

    @Test
    fun deserialize_singleType() {
        val prodIncrease = create(1, 0, 0)
        assertEquals(prodIncrease, gson.fromJson("\"W\"", Production::class.java))
    }

    @Test
    fun deserialize_multipleTimesSameType() {
        val prodIncrease = create(3, 0, 0)
        assertEquals(prodIncrease, gson.fromJson("\"WWW\"", Production::class.java))
    }

    @Test
    fun deserialize_mixedTypes() {
        val prodIncrease = create(1, 1, 1)
        assertEquals(prodIncrease, gson.fromJson("\"WCS\"", Production::class.java))
    }

    @Test
    fun deserialize_mixedTypes_unordered() {
        val prodIncrease = create(1, 3, 2)
        assertEquals(prodIncrease, gson.fromJson("\"SCWCSS\"", Production::class.java))
    }

    @Test
    fun deserialize_choice2() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY)
        assertEquals(prodIncrease, gson.fromJson("\"W/C\"", Production::class.java))
    }

    @Test
    fun deserialize_choice3() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals(prodIncrease, gson.fromJson("\"W/O/C\"", Production::class.java))
    }

    @Test
    fun deserialize_choice2_unordered() {
        val prodIncrease = createChoice(ResourceType.CLAY, ResourceType.WOOD)
        assertEquals(prodIncrease, gson.fromJson("\"W/C\"", Production::class.java))
    }

    @Test
    fun deserialize_choice3_unordered() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE)
        assertEquals(prodIncrease, gson.fromJson("\"W/O/C\"", Production::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failOnMultipleResourcesInChoice() {
        gson.fromJson("\"W/SS/C\"", Production::class.java)
    }
}
