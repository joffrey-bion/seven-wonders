package org.luxons.sevenwonders.game.resources

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.EnumSet
import java.util.HashSet

class ProductionTest {

    private var emptyResources: Resources = Resources()
    private var resources1Wood: Resources = Resources()
    private var resources1Stone: Resources = Resources()
    private var resources1Stone1Wood: Resources = Resources()
    private var resources2Stones: Resources = Resources()
    private var resources2Stones3Clay: Resources = Resources()

    @Before
    fun init() {
        emptyResources = Resources()

        resources1Wood = Resources()
        resources1Wood.add(ResourceType.WOOD, 1)

        resources1Stone = Resources()
        resources1Stone.add(ResourceType.STONE, 1)

        resources1Stone1Wood = Resources()
        resources1Stone1Wood.add(ResourceType.STONE, 1)
        resources1Stone1Wood.add(ResourceType.WOOD, 1)

        resources2Stones = Resources()
        resources2Stones.add(ResourceType.STONE, 2)

        resources2Stones3Clay = Resources()
        resources2Stones3Clay.add(ResourceType.STONE, 2)
        resources2Stones3Clay.add(ResourceType.CLAY, 3)
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
        production.addFixedResource(ResourceType.STONE, 1)
        assertFalse(production.contains(resources2Stones))
    }

    @Test
    fun contains_singleFixedResource_justEnough() {
        val production = Production()
        production.addFixedResource(ResourceType.STONE, 2)
        assertTrue(production.contains(resources2Stones))
    }

    @Test
    fun contains_singleFixedResource_moreThanEnough() {
        val production = Production()
        production.addFixedResource(ResourceType.STONE, 3)
        assertTrue(production.contains(resources2Stones))
    }

    @Test
    fun contains_singleFixedResource_moreThanEnough_amongOthers() {
        val production = Production()
        production.addFixedResource(ResourceType.STONE, 3)
        production.addFixedResource(ResourceType.CLAY, 2)
        assertTrue(production.contains(resources2Stones))
    }

    @Test
    fun contains_multipleFixedResources_notEnoughOfOne() {
        val production = Production()
        production.addFixedResource(ResourceType.STONE, 3)
        production.addFixedResource(ResourceType.CLAY, 1)
        assertFalse(production.contains(resources2Stones3Clay))
    }

    @Test
    fun contains_multipleFixedResources_notEnoughOfBoth() {
        val production = Production()
        production.addFixedResource(ResourceType.STONE, 1)
        production.addFixedResource(ResourceType.CLAY, 1)
        assertFalse(production.contains(resources2Stones3Clay))
    }

    @Test
    fun contains_multipleFixedResources_moreThanEnough() {
        val production = Production()
        production.addFixedResource(ResourceType.STONE, 3)
        production.addFixedResource(ResourceType.CLAY, 5)
        assertTrue(production.contains(resources2Stones3Clay))
    }

    @Test
    fun contains_singleChoice_containsEmpty() {
        val production = Production()
        production.addChoice(ResourceType.STONE, ResourceType.CLAY)
        assertTrue(production.contains(emptyResources))
    }

    @Test
    fun contains_singleChoice_enough() {
        val production = Production()
        production.addChoice(ResourceType.STONE, ResourceType.WOOD)
        assertTrue(production.contains(resources1Wood))
        assertTrue(production.contains(resources1Stone))
    }

    @Test
    fun contains_multipleChoices_notBoth() {
        val production = Production()
        production.addChoice(ResourceType.STONE, ResourceType.CLAY)
        production.addChoice(ResourceType.STONE, ResourceType.CLAY)
        production.addChoice(ResourceType.STONE, ResourceType.CLAY)
        assertFalse(production.contains(resources2Stones3Clay))
    }

    @Test
    fun contains_multipleChoices_enough() {
        val production = Production()
        production.addChoice(ResourceType.STONE, ResourceType.ORE)
        production.addChoice(ResourceType.STONE, ResourceType.WOOD)
        assertTrue(production.contains(resources1Stone1Wood))
    }

    @Test
    fun contains_multipleChoices_enoughReverseOrder() {
        val production = Production()
        production.addChoice(ResourceType.STONE, ResourceType.WOOD)
        production.addChoice(ResourceType.STONE, ResourceType.ORE)
        assertTrue(production.contains(resources1Stone1Wood))
    }

    @Test
    fun contains_multipleChoices_moreThanEnough() {
        val production = Production()
        production.addChoice(ResourceType.LOOM, ResourceType.GLASS, ResourceType.PAPYRUS)
        production.addChoice(ResourceType.STONE, ResourceType.ORE)
        production.addChoice(ResourceType.STONE, ResourceType.WOOD)
        assertTrue(production.contains(resources1Stone1Wood))
    }

    @Test
    fun contains_mixedFixedAndChoice_enough() {
        val production = Production()
        production.addFixedResource(ResourceType.WOOD, 1)
        production.addChoice(ResourceType.STONE, ResourceType.WOOD)
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
        production.addChoice(ResourceType.STONE, ResourceType.WOOD)
        production.addChoice(ResourceType.STONE, ResourceType.ORE)

        val production2 = Production()
        production2.addAll(production)
        assertTrue(production.contains(resources1Stone1Wood))
    }

    @Test
    fun addAll_production_mixedFixedResourcesAndChoices() {
        val production = Production()
        production.addFixedResource(ResourceType.WOOD, 1)
        production.addChoice(ResourceType.STONE, ResourceType.WOOD)

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
        production.addChoice(ResourceType.STONE, ResourceType.WOOD)
        production.addChoice(ResourceType.STONE, ResourceType.ORE)
        production.addChoice(ResourceType.CLAY, ResourceType.LOOM, ResourceType.GLASS)
        assertEquals(production.getAlternativeResources(), production.asChoices())
    }

    @Test
    fun asChoices_onlyFixed() {
        val production = Production()
        production.addFixedResource(ResourceType.WOOD, 1)
        production.addFixedResource(ResourceType.CLAY, 2)

        val expected = HashSet<Set<ResourceType>>()
        expected.add(EnumSet.of(ResourceType.WOOD))
        expected.add(EnumSet.of(ResourceType.CLAY))
        expected.add(EnumSet.of(ResourceType.CLAY))

        assertEquals(expected, production.asChoices())
    }

    @Test
    fun asChoices_mixed() {
        val production = Production()
        production.addChoice(ResourceType.STONE, ResourceType.ORE)
        production.addChoice(ResourceType.CLAY, ResourceType.LOOM, ResourceType.GLASS)
        production.addFixedResource(ResourceType.WOOD, 1)
        production.addFixedResource(ResourceType.CLAY, 2)

        val expected = HashSet<Set<ResourceType>>()
        expected.add(EnumSet.of(ResourceType.STONE, ResourceType.ORE))
        expected.add(EnumSet.of(ResourceType.CLAY, ResourceType.LOOM, ResourceType.GLASS))
        expected.add(EnumSet.of(ResourceType.WOOD))
        expected.add(EnumSet.of(ResourceType.CLAY))
        expected.add(EnumSet.of(ResourceType.CLAY))

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
        production1.addFixedResource(ResourceType.GLASS, 1)
        production2.addFixedResource(ResourceType.GLASS, 1)
        assertTrue(production1 == production2)
        production1.addChoice(ResourceType.ORE, ResourceType.WOOD)
        production2.addChoice(ResourceType.ORE, ResourceType.WOOD)
        assertTrue(production1 == production2)
    }

    @Test
    fun hashCode_sameWhenSameContent() {
        val production1 = Production()
        val production2 = Production()
        assertEquals(production1.hashCode().toLong(), production2.hashCode().toLong())
        production1.addFixedResource(ResourceType.GLASS, 1)
        production2.addFixedResource(ResourceType.GLASS, 1)
        assertEquals(production1.hashCode().toLong(), production2.hashCode().toLong())
        production1.addChoice(ResourceType.ORE, ResourceType.WOOD)
        production2.addChoice(ResourceType.ORE, ResourceType.WOOD)
        assertEquals(production1.hashCode().toLong(), production2.hashCode().toLong())
    }
}
