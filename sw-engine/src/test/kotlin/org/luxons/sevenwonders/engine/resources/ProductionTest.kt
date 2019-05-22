package org.luxons.sevenwonders.engine.resources

import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.resources.ResourceType.CLAY
import org.luxons.sevenwonders.model.resources.ResourceType.GLASS
import org.luxons.sevenwonders.model.resources.ResourceType.LOOM
import org.luxons.sevenwonders.model.resources.ResourceType.ORE
import org.luxons.sevenwonders.model.resources.ResourceType.PAPYRUS
import org.luxons.sevenwonders.model.resources.ResourceType.STONE
import org.luxons.sevenwonders.model.resources.ResourceType.WOOD
import java.util.EnumSet
import java.util.HashSet
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProductionTest {

    private lateinit var emptyResources: Resources
    private lateinit var resources1Wood: Resources
    private lateinit var resources1Stone: Resources
    private lateinit var resources1Stone1Wood: Resources
    private lateinit var resources2Stones: Resources
    private lateinit var resources2Stones3Clay: Resources

    @Before
    fun init() {
        emptyResources = emptyResources()
        resources1Wood = resourcesOf(WOOD)
        resources1Stone = resourcesOf(STONE)
        resources1Stone1Wood = resourcesOf(STONE to 1, WOOD to 1)
        resources2Stones = resourcesOf(STONE to 2)
        resources2Stones3Clay = resourcesOf(STONE to 2, CLAY to 3)
    }

    @Test
    fun contains_newProductionContainsEmpty() {
        val production = Production()
        assertTrue(production.contains(emptyResources))
    }

    @Test
    fun contains_singleFixedResource_noneAtAll() {
        val production = Production()
        assertFalse(production.contains(resources2Stones))
    }

    @Test
    fun contains_singleFixedResource_notEnough() {
        val production = Production()
        production.addFixedResource(STONE, 1)
        assertFalse(production.contains(resources2Stones))
    }

    @Test
    fun contains_singleFixedResource_justEnough() {
        val production = Production()
        production.addFixedResource(STONE, 2)
        assertTrue(production.contains(resources2Stones))
    }

    @Test
    fun contains_singleFixedResource_moreThanEnough() {
        val production = Production()
        production.addFixedResource(STONE, 3)
        assertTrue(production.contains(resources2Stones))
    }

    @Test
    fun contains_singleFixedResource_moreThanEnough_amongOthers() {
        val production = Production()
        production.addFixedResource(STONE, 3)
        production.addFixedResource(CLAY, 2)
        assertTrue(production.contains(resources2Stones))
    }

    @Test
    fun contains_multipleFixedResources_notEnoughOfOne() {
        val production = Production()
        production.addFixedResource(STONE, 3)
        production.addFixedResource(CLAY, 1)
        assertFalse(production.contains(resources2Stones3Clay))
    }

    @Test
    fun contains_multipleFixedResources_notEnoughOfBoth() {
        val production = Production()
        production.addFixedResource(STONE, 1)
        production.addFixedResource(CLAY, 1)
        assertFalse(production.contains(resources2Stones3Clay))
    }

    @Test
    fun contains_multipleFixedResources_moreThanEnough() {
        val production = Production()
        production.addFixedResource(STONE, 3)
        production.addFixedResource(CLAY, 5)
        assertTrue(production.contains(resources2Stones3Clay))
    }

    @Test
    fun contains_singleChoice_containsEmpty() {
        val production = Production()
        production.addChoice(STONE, CLAY)
        assertTrue(production.contains(emptyResources))
    }

    @Test
    fun contains_singleChoice_enough() {
        val production = Production()
        production.addChoice(STONE, WOOD)
        assertTrue(production.contains(resources1Wood))
        assertTrue(production.contains(resources1Stone))
    }

    @Test
    fun contains_multipleChoices_notBoth() {
        val production = Production()
        production.addChoice(STONE, CLAY)
        production.addChoice(STONE, CLAY)
        production.addChoice(STONE, CLAY)
        assertFalse(production.contains(resources2Stones3Clay))
    }

    @Test
    fun contains_multipleChoices_enough() {
        val production = Production()
        production.addChoice(STONE, ORE)
        production.addChoice(STONE, WOOD)
        assertTrue(production.contains(resources1Stone1Wood))
    }

    @Test
    fun contains_multipleChoices_enoughReverseOrder() {
        val production = Production()
        production.addChoice(STONE, WOOD)
        production.addChoice(STONE, ORE)
        assertTrue(production.contains(resources1Stone1Wood))
    }

    @Test
    fun contains_multipleChoices_moreThanEnough() {
        val production = Production()
        production.addChoice(LOOM, GLASS, PAPYRUS)
        production.addChoice(STONE, ORE)
        production.addChoice(STONE, WOOD)
        assertTrue(production.contains(resources1Stone1Wood))
    }

    @Test
    fun contains_mixedFixedAndChoice_enough() {
        val production = Production()
        production.addFixedResource(WOOD, 1)
        production.addChoice(STONE, WOOD)
        assertTrue(production.contains(resources1Stone1Wood))
    }

    @Test
    fun addAll_empty() {
        val production = Production()
        production.addAll(emptyResources)
        assertTrue(production.contains(emptyResources))
    }

    @Test
    fun addAll_singleResource() {
        val production = Production()
        production.addAll(resources1Stone)
        assertTrue(production.contains(resources1Stone))
    }

    @Test
    fun addAll_multipleResources() {
        val production = Production()
        production.addAll(resources2Stones3Clay)
        assertTrue(production.contains(resources2Stones3Clay))
    }

    @Test
    fun addAll_production_multipleFixedResources() {
        val production = Production()
        production.addAll(resources2Stones3Clay)

        val production2 = Production()
        production2.addAll(production)

        assertTrue(production2.contains(resources2Stones3Clay))
    }

    @Test
    fun addAll_production_multipleChoices() {
        val production = Production()
        production.addChoice(STONE, WOOD)
        production.addChoice(STONE, ORE)

        val production2 = Production()
        production2.addAll(production)
        assertTrue(production.contains(resources1Stone1Wood))
    }

    @Test
    fun addAll_production_mixedFixedResourcesAndChoices() {
        val production = Production()
        production.addFixedResource(WOOD, 1)
        production.addChoice(STONE, WOOD)

        val production2 = Production()
        production2.addAll(production)

        assertTrue(production.contains(resources1Stone1Wood))
    }

    @Test
    fun asChoices_empty() {
        val production = Production()
        assertTrue(production.asChoices().isEmpty())
    }

    @Test
    fun asChoices_onlyChoices() {
        val production = Production()
        production.addChoice(STONE, WOOD)
        production.addChoice(STONE, ORE)
        production.addChoice(CLAY, LOOM, GLASS)
        assertEquals(production.getAlternativeResources(), production.asChoices())
    }

    @Test
    fun asChoices_onlyFixed() {
        val production = Production()
        production.addFixedResource(WOOD, 1)
        production.addFixedResource(CLAY, 2)

        val expected = HashSet<Set<ResourceType>>()
        expected.add(EnumSet.of(WOOD))
        expected.add(EnumSet.of(CLAY))
        expected.add(EnumSet.of(CLAY))

        assertEquals(expected, production.asChoices())
    }

    @Test
    fun asChoices_mixed() {
        val production = Production()
        production.addChoice(STONE, ORE)
        production.addChoice(CLAY, LOOM, GLASS)
        production.addFixedResource(WOOD, 1)
        production.addFixedResource(CLAY, 2)

        val expected = HashSet<Set<ResourceType>>()
        expected.add(EnumSet.of(STONE, ORE))
        expected.add(EnumSet.of(CLAY, LOOM, GLASS))
        expected.add(EnumSet.of(WOOD))
        expected.add(EnumSet.of(CLAY))
        expected.add(EnumSet.of(CLAY))

        assertEquals(expected, production.asChoices())
    }

    @Test
    fun equals_trueWhenSame() {
        val production = Production()
        assertEquals(production, production)
    }

    @Test
    fun equals_trueWhenSameContent() {
        val production1 = Production()
        val production2 = Production()
        assertTrue(production1 == production2)
        production1.addFixedResource(GLASS, 1)
        production2.addFixedResource(GLASS, 1)
        assertTrue(production1 == production2)
        production1.addChoice(ORE, WOOD)
        production2.addChoice(ORE, WOOD)
        assertTrue(production1 == production2)
    }

    @Test
    fun hashCode_sameWhenSameContent() {
        val production1 = Production()
        val production2 = Production()
        assertEquals(production1.hashCode(), production2.hashCode())
        production1.addFixedResource(GLASS, 1)
        production2.addFixedResource(GLASS, 1)
        assertEquals(production1.hashCode(), production2.hashCode())
        production1.addChoice(ORE, WOOD)
        production2.addChoice(ORE, WOOD)
        assertEquals(production1.hashCode(), production2.hashCode())
    }
}
