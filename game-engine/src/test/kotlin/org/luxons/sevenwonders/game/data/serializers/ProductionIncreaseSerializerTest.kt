package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.effects.ProductionIncrease
import org.luxons.sevenwonders.game.resources.MutableResources
import org.luxons.sevenwonders.game.resources.Production
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources

class ProductionIncreaseSerializerTest {

    private var gson: Gson? = null

    @Before
    fun setUp() {
        val resourceTypeList = object : TypeToken<List<ResourceType>>() {

        }.type
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
        assertEquals("null", gson!!.toJson(null, ProductionIncrease::class.java))
    }

    @Test
    fun serialize_emptyProdIncreaseAsNull() {
        val prodIncrease = ProductionIncrease(Production(), false)
        assertEquals("null", gson!!.toJson(prodIncrease, ProductionIncrease::class.java))
    }

    @Test
    fun serialize_singleType() {
        val prodIncrease = create(true, 1, 0, 0)
        assertEquals("\"W\"", gson!!.toJson(prodIncrease, ProductionIncrease::class.java))
    }

    @Test
    fun serialize_mixedTypes() {
        val prodIncrease = create(true, 1, 1, 1)
        assertEquals("\"WSC\"", gson!!.toJson(prodIncrease, ProductionIncrease::class.java))
    }

    @Test
    fun serialize_mixedTypes_notSellable() {
        val prodIncrease = create(false, 1, 1, 1)
        assertEquals("\"(WSC)\"", gson!!.toJson(prodIncrease, ProductionIncrease::class.java))
    }

    @Test
    fun serialize_choice2() {
        val prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY)
        assertEquals("\"W/C\"", gson!!.toJson(prodIncrease, ProductionIncrease::class.java))
    }

    @Test
    fun serialize_choice3() {
        val prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals("\"W/O/C\"", gson!!.toJson(prodIncrease, ProductionIncrease::class.java))
    }

    @Test
    fun serialize_choice3_notSellable() {
        val prodIncrease = createChoice(false, ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals("\"(W/O/C)\"", gson!!.toJson(prodIncrease, ProductionIncrease::class.java))
    }

    @Test
    fun serialize_choice2_unordered() {
        val prodIncrease = createChoice(true, ResourceType.CLAY, ResourceType.WOOD)
        assertEquals("\"W/C\"", gson!!.toJson(prodIncrease, ProductionIncrease::class.java))
    }

    @Test
    fun serialize_choice3_unordered() {
        val prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE)
        assertEquals("\"W/O/C\"", gson!!.toJson(prodIncrease, ProductionIncrease::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun serialize_failIfMultipleChoices() {
        val prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY)
        prodIncrease.production.addChoice(ResourceType.ORE, ResourceType.GLASS)
        gson!!.toJson(prodIncrease, ProductionIncrease::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun serialize_failIfMixedFixedAndChoices() {
        val prodIncrease = create(true, 1, 0, 0)
        prodIncrease.production.addChoice(ResourceType.WOOD, ResourceType.CLAY)
        gson!!.toJson(prodIncrease, ProductionIncrease::class.java)
    }

    @Test
    fun deserialize_nullFromNull() {
        assertNull(gson!!.fromJson("null", ProductionIncrease::class.java))
    }

    @Test
    fun deserialize_emptyList() {
        val prodIncrease = ProductionIncrease(Production(), true)
        assertEquals(prodIncrease, gson!!.fromJson("\"\"", ProductionIncrease::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failOnGarbageString() {
        gson!!.fromJson("\"this is garbage\"", ProductionIncrease::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failOnGarbageStringWithSlashes() {
        gson!!.fromJson("\"this/is/garbage\"", ProductionIncrease::class.java)
    }

    @Test
    fun deserialize_singleType() {
        val prodIncrease = create(true, 1, 0, 0)
        assertEquals(prodIncrease, gson!!.fromJson("\"W\"", ProductionIncrease::class.java))
    }

    @Test
    fun deserialize_multipleTimesSameType_notSellable() {
        val prodIncrease = create(false, 3, 0, 0)
        assertEquals(prodIncrease, gson!!.fromJson("\"(WWW)\"", ProductionIncrease::class.java))
    }

    @Test
    fun deserialize_mixedTypes() {
        val prodIncrease = create(true, 1, 1, 1)
        assertEquals(prodIncrease, gson!!.fromJson("\"WCS\"", ProductionIncrease::class.java))
    }

    @Test
    fun deserialize_choice2() {
        val prodIncrease = createChoice(true, ResourceType.WOOD, ResourceType.CLAY)
        assertEquals(prodIncrease, gson!!.fromJson("\"W/C\"", ProductionIncrease::class.java))
    }

    @Test
    fun deserialize_choice3_notSellable() {
        val prodIncrease = createChoice(false, ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals(prodIncrease, gson!!.fromJson("\"(W/O/C)\"", ProductionIncrease::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failOnMultipleResourcesInChoice() {
        gson!!.fromJson("\"W/SS/C\"", ProductionIncrease::class.java)
    }
}
