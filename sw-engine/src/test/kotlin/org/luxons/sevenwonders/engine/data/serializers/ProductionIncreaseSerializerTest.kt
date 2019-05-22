package org.luxons.sevenwonders.engine.data.serializers

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.engine.effects.ProductionIncrease
import org.luxons.sevenwonders.engine.resources.MutableResources
import org.luxons.sevenwonders.engine.resources.Production
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.engine.resources.Resources
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ProductionIncreaseSerializerTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        val resourceTypeList = object : TypeToken<List<ResourceType>>() {}.type
        gson = GsonBuilder().registerTypeAdapter(Resources::class.java, ResourcesSerializer())
            .registerTypeAdapter(MutableResources::class.java, ResourcesSerializer())
            .registerTypeAdapter(ResourceType::class.java, ResourceTypeSerializer())
            .registerTypeAdapter(resourceTypeList, ResourceTypesSerializer())
            .registerTypeAdapter(Production::class.java, ProductionSerializer())
            .registerTypeAdapter(ProductionIncrease::class.java, ProductionIncreaseSerializer())
            .create()
    }

    private fun create(sellable: Boolean, wood: Int, stone: Int, clay: Int): ProductionIncrease {
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
        return ProductionIncrease(production, sellable)
    }

    private fun createChoice(sellable: Boolean, vararg types: ResourceType): ProductionIncrease {
        val production = Production()
        production.addChoice(*types)
        return ProductionIncrease(production, sellable)
    }

    @Test
    fun serialize_nullAsNull() {
        assertEquals("null", gson.toJson(null, ProductionIncrease::class.java))
    }

    @Test
    fun serialize_emptyProdIncreaseAsNull() {
        val prodIncrease = ProductionIncrease(Production(), false)
        assertEquals("null", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_singleType() {
        val prodIncrease = create(true, 1, 0, 0)
        assertEquals("\"W\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_mixedTypes() {
        val prodIncrease = create(true, 1, 1, 1)
        assertEquals("\"WSC\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_mixedTypes_notSellable() {
        val prodIncrease = create(false, 1, 1, 1)
        assertEquals("\"(WSC)\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_choice2() {
        val prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY)
        assertEquals("\"W/C\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_choice3() {
        val prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_choice3_notSellable() {
        val prodIncrease = createChoice(false, ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals("\"(W/O/C)\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_choice2_unordered() {
        val prodIncrease = createChoice(true, ResourceType.CLAY, ResourceType.WOOD)
        assertEquals("\"W/C\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_choice3_unordered() {
        val prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE)
        assertEquals("\"W/O/C\"", gson.toJson(prodIncrease))
    }

    @Test
    fun serialize_failIfMultipleChoices() {
        val prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY)
        prodIncrease.production.addChoice(ResourceType.ORE, ResourceType.GLASS)
        assertFailsWith<IllegalArgumentException> {
            gson.toJson(prodIncrease)
        }
    }

    @Test
    fun serialize_failIfMixedFixedAndChoices() {
        val prodIncrease = create(true, 1, 0, 0)
        prodIncrease.production.addChoice(ResourceType.WOOD, ResourceType.CLAY)
        assertFailsWith<IllegalArgumentException> {
            gson.toJson(prodIncrease)
        }
    }

    @Test
    fun deserialize_nullFromNull() {
        assertNull(gson.fromJson("null", ProductionIncrease::class.java))
    }

    @Test
    fun deserialize_emptyList() {
        val prodIncrease = ProductionIncrease(Production(), true)
        assertEquals(prodIncrease, gson.fromJson("\"\""))
    }

    @Test
    fun deserialize_failOnGarbageString() {
        assertFailsWith(IllegalArgumentException::class) {
            gson.fromJson<ProductionIncrease>("\"this is garbage\"")
        }
    }

    @Test
    fun deserialize_failOnGarbageStringWithSlashes() {
        assertFailsWith(IllegalArgumentException::class) {
            gson.fromJson<ProductionIncrease>("\"this/is/garbage\"")
        }
    }

    @Test
    fun deserialize_singleType() {
        val prodIncrease = create(true, 1, 0, 0)
        assertEquals(prodIncrease, gson.fromJson("\"W\""))
    }

    @Test
    fun deserialize_multipleTimesSameType_notSellable() {
        val prodIncrease = create(false, 3, 0, 0)
        assertEquals(prodIncrease, gson.fromJson("\"(WWW)\""))
    }

    @Test
    fun deserialize_mixedTypes() {
        val prodIncrease = create(true, 1, 1, 1)
        assertEquals(prodIncrease, gson.fromJson("\"WCS\""))
    }

    @Test
    fun deserialize_choice2() {
        val prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY)
        assertEquals(prodIncrease, gson.fromJson("\"W/C\""))
    }

    @Test
    fun deserialize_choice3_notSellable() {
        val prodIncrease = createChoice(false, ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals(prodIncrease, gson.fromJson("\"(W/O/C)\""))
    }

    @Test
    fun deserialize_failOnMultipleResourcesInChoice() {
        assertFailsWith(IllegalArgumentException::class) {
            gson.fromJson<ProductionIncrease>("\"W/SS/C\"")
        }
    }
}
