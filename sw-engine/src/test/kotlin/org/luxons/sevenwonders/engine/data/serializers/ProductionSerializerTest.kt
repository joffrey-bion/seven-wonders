package org.luxons.sevenwonders.engine.data.serializers

import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import org.luxons.sevenwonders.engine.resources.Production
import org.luxons.sevenwonders.model.resources.ResourceType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ProductionSerializerTest {

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
        assertEquals("null", Json.encodeToString(Production.serializer().nullable, null))
    }

    @Test
    fun serialize_emptyProdIncreaseAsNull() {
        val prodIncrease = Production()
        assertEquals("null", Json.encodeToString(prodIncrease))
    }

    @Test
    fun serialize_singleType() {
        val prodIncrease = create(1, 0, 0)
        assertEquals("\"W\"", Json.encodeToString(prodIncrease))
    }

    @Test
    fun serialize_multipleTimesSameType() {
        val prodIncrease = create(3, 0, 0)
        assertEquals("\"WWW\"", Json.encodeToString(prodIncrease))
    }

    @Test
    fun serialize_mixedTypes() {
        val prodIncrease = create(1, 1, 1)
        assertEquals("\"WSC\"", Json.encodeToString(prodIncrease))
    }

    @Test
    fun serialize_mixedTypesMultiple() {
        val prodIncrease = create(2, 1, 2)
        assertEquals("\"WWSCC\"", Json.encodeToString(prodIncrease))
    }

    @Test
    fun serialize_choice2() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY)
        assertEquals("\"W/C\"", Json.encodeToString(prodIncrease))
    }

    @Test
    fun serialize_choice3() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals("\"W/O/C\"", Json.encodeToString(prodIncrease))
    }

    @Test
    fun serialize_choice2_unordered() {
        val prodIncrease = createChoice(ResourceType.CLAY, ResourceType.WOOD)
        assertEquals("\"W/C\"", Json.encodeToString(prodIncrease))
    }

    @Test
    fun serialize_choice3_unordered() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE)
        assertEquals("\"W/O/C\"", Json.encodeToString(prodIncrease))
    }

    @Test
    fun serialize_failIfMultipleChoices() {
        val production = createChoice(ResourceType.WOOD, ResourceType.CLAY)
        production.addChoice(ResourceType.ORE, ResourceType.GLASS)
        assertFailsWith<IllegalArgumentException> {
            Json.encodeToString(production)
        }
    }

    @Test
    fun serialize_failIfMixedFixedAndChoices() {
        val production = create(1, 0, 0)
        production.addChoice(ResourceType.WOOD, ResourceType.CLAY)
        assertFailsWith<IllegalArgumentException> {
            Json.encodeToString(production)
        }
    }

    @Test
    fun deserialize_nullFromNull() {
        assertNull(Json.decodeFromString<Production?>("null"))
    }

    @Test
    fun deserialize_emptyList() {
        assertEquals(Production(), Json.decodeFromString("\"\""))
    }

    @Test
    fun deserialize_failOnGarbageString() {
        assertFailsWith<IllegalArgumentException> {
            Json.decodeFromString<Production>("\"this is garbage\"")
        }
    }

    @Test
    fun deserialize_failOnGarbageStringWithSlashes() {
        assertFailsWith<IllegalArgumentException> {
            Json.decodeFromString<Production>("\"this/is/garbage\"")
        }
    }

    @Test
    fun deserialize_singleType() {
        val prodIncrease = create(1, 0, 0)
        assertEquals(prodIncrease, Json.decodeFromString("\"W\""))
    }

    @Test
    fun deserialize_multipleTimesSameType() {
        val prodIncrease = create(3, 0, 0)
        assertEquals(prodIncrease, Json.decodeFromString("\"WWW\""))
    }

    @Test
    fun deserialize_mixedTypes() {
        val prodIncrease = create(1, 1, 1)
        assertEquals(prodIncrease, Json.decodeFromString("\"WCS\""))
    }

    @Test
    fun deserialize_mixedTypes_unordered() {
        val prodIncrease = create(1, 3, 2)
        assertEquals(prodIncrease, Json.decodeFromString("\"SCWCSS\""))
    }

    @Test
    fun deserialize_choice2() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY)
        assertEquals(prodIncrease, Json.decodeFromString("\"W/C\""))
    }

    @Test
    fun deserialize_choice3() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.ORE, ResourceType.CLAY)
        assertEquals(prodIncrease, Json.decodeFromString("\"W/O/C\""))
    }

    @Test
    fun deserialize_choice2_unordered() {
        val prodIncrease = createChoice(ResourceType.CLAY, ResourceType.WOOD)
        assertEquals(prodIncrease, Json.decodeFromString("\"W/C\""))
    }

    @Test
    fun deserialize_choice3_unordered() {
        val prodIncrease = createChoice(ResourceType.WOOD, ResourceType.CLAY, ResourceType.ORE)
        assertEquals(prodIncrease, Json.decodeFromString("\"W/O/C\""))
    }

    @Test
    fun deserialize_failOnMultipleResourcesInChoice() {
        assertFailsWith<IllegalArgumentException> {
            Json.decodeFromString<Production>("\"W/SS/C\"")
        }
    }
}
