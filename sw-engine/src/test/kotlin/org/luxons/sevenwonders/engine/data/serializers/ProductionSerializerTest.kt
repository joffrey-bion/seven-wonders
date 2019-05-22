package org.luxons.sevenwonders.engine.data.serializers

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.engine.resources.MutableResources
import org.luxons.sevenwonders.engine.resources.Production
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.engine.resources.Resources
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ProductionSerializerTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        val resourceTypeList = object : TypeToken<List<ResourceType>>() {}.type
        gson = GsonBuilder().registerTypeAdapter(Resources::class.java, ResourcesSerializer())
            .registerTypeAdapter(MutableResources::class.java, ResourcesSerializer())
            .registerTypeAdapter(ResourceType::class.java, ResourceTypeSerializer())
            .registerTypeAdapter(resourceTypeList, ResourceTypesSerializer())
            .registerTypeAdapter(Production::class.java, ProductionSerializer()).create()
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
        assertEquals("null", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_singleType() {
        val prodIncrease = create(1, 0, 0)
        assertEquals("\"W\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_multipleTimesSameType() {
        val prodIncrease = create(3, 0, 0)
        assertEquals("\"WWW\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_mixedTypes() {
        val prodIncrease = create(1, 1, 1)
        assertEquals("\"WSC\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_mixedTypesMultiple() {
        val prodIncrease = create(2, 1, 2)
        assertEquals("\"WWSCC\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_choice2() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY)
        assertEquals("\"W/C\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_choice3() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_choice2_unordered() {
        val prodIncrease = createChoice(ResourceType.CLAY, ResourceType.WOOD)
        assertEquals("\"W/C\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_choice3_unordered() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE)
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_failIfMultipleChoices() {
        val production = createChoice(ResourceType.WOOD, ResourceType.CLAY)
        production.addChoice(ResourceType.ORE, ResourceType.GLASS)
        assertFailsWith<IllegalArgumentException> {
            gson.toJson(production)
        }
    }

    @Test
    fun serialize_failIfMixedFixedAndChoices() {
        val production = create(1, 0, 0)
        production.addChoice(ResourceType.WOOD, ResourceType.CLAY)
        assertFailsWith<IllegalArgumentException> {
            gson.toJson(production)
        }
    }

    @Test
    fun deserialize_nullFromNull() {
        assertNull(gson.fromJson("null", Production::class.java))
    }

    @Test
    fun deserialize_emptyList() {
        val prodIncrease = Production()
        assertEquals(prodIncrease, gson.fromJson("\"\""))
    }

    @Test
    fun deserialize_failOnGarbageString() {
        assertFailsWith<IllegalArgumentException> {
            gson.fromJson<Production>("\"this is garbage\"")
        }
    }

    @Test
    fun deserialize_failOnGarbageStringWithSlashes() {
        assertFailsWith<IllegalArgumentException> {
            gson.fromJson<Production>("\"this/is/garbage\"")
        }
    }

    @Test
    fun deserialize_singleType() {
        val prodIncrease = create(1, 0, 0)
        assertEquals(prodIncrease, gson.fromJson("\"W\""))
    }

    @Test
    fun deserialize_multipleTimesSameType() {
        val prodIncrease = create(3, 0, 0)
        assertEquals(prodIncrease, gson.fromJson("\"WWW\""))
    }

    @Test
    fun deserialize_mixedTypes() {
        val prodIncrease = create(1, 1, 1)
        assertEquals(prodIncrease, gson.fromJson("\"WCS\""))
    }

    @Test
    fun deserialize_mixedTypes_unordered() {
        val prodIncrease = create(1, 3, 2)
        assertEquals(prodIncrease, gson.fromJson("\"SCWCSS\""))
    }

    @Test
    fun deserialize_choice2() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY)
        assertEquals(prodIncrease, gson.fromJson("\"W/C\""))
    }

    @Test
    fun deserialize_choice3() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals(prodIncrease, gson.fromJson("\"W/O/C\""))
    }

    @Test
    fun deserialize_choice2_unordered() {
        val prodIncrease = createChoice(ResourceType.CLAY, ResourceType.WOOD)
        assertEquals(prodIncrease, gson.fromJson("\"W/C\""))
    }

    @Test
    fun deserialize_choice3_unordered() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE)
        assertEquals(prodIncrease, gson.fromJson("\"W/O/C\""))
    }

    @Test
    fun deserialize_failOnMultipleResourcesInChoice() {
        assertFailsWith<IllegalArgumentException> {
            gson.fromJson<Production>("\"W/SS/C\"")
        }
    }
}
