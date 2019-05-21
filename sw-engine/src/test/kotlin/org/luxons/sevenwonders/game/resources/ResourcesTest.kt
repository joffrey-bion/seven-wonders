package org.luxons.sevenwonders.game.resources

import org.junit.Test
import org.luxons.sevenwonders.game.api.resources.ResourceType
import org.luxons.sevenwonders.game.api.resources.ResourceType.CLAY
import org.luxons.sevenwonders.game.api.resources.ResourceType.GLASS
import org.luxons.sevenwonders.game.api.resources.ResourceType.LOOM
import org.luxons.sevenwonders.game.api.resources.ResourceType.ORE
import org.luxons.sevenwonders.game.api.resources.ResourceType.PAPYRUS
import org.luxons.sevenwonders.game.api.resources.ResourceType.STONE
import org.luxons.sevenwonders.game.api.resources.ResourceType.WOOD
import java.util.NoSuchElementException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResourcesTest {

    @Test
    fun init_shouldBeEmpty() {
        val resources = emptyResources()
        for (resourceType in ResourceType.values()) {
            assertEquals(0, resources[resourceType])
        }
        assertEquals(0, resources.size)
        assertTrue(resources.isEmpty())
    }

    @Test
    fun add_zero() {
        val resources = mutableResourcesOf()
        resources.add(CLAY, 0)
        assertEquals(0, resources[CLAY])
        assertEquals(0, resources.size)
        assertTrue(resources.isEmpty())
    }

    @Test
    fun add_simple() {
        val resources = mutableResourcesOf()
        resources.add(WOOD, 3)
        assertEquals(3, resources[WOOD])
        assertEquals(3, resources.size)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun add_multipleCallsStacked() {
        val resources = mutableResourcesOf()
        resources.add(ORE, 3)
        resources.add(ORE, 2)
        assertEquals(5, resources[ORE])
        assertEquals(5, resources.size)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun add_interlaced() {
        val resources = mutableResourcesOf()
        resources.add(GLASS, 3)
        resources.add(STONE, 1)
        resources.add(WOOD, 4)
        resources.add(GLASS, 2)
        assertEquals(5, resources[GLASS])
        assertEquals(10, resources.size)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun plus_zero() {
        val resources = resourcesOf(CLAY to 2)
        val resourcesPlusZero = resources + emptyResources()
        val zeroPlusResources = emptyResources() + resources

        assertEquals(2, resourcesPlusZero[CLAY])
        assertEquals(2, resourcesPlusZero.size)
        assertEquals(2, zeroPlusResources[CLAY])
        assertEquals(2, zeroPlusResources.size)
    }

    @Test
    fun plus_sameResource() {
        val resources1 = resourcesOf(WOOD to 1)
        val resources2 = resourcesOf(WOOD to 3)
        val sum = resources1 + resources2

        assertEquals(1, resources1.size)
        assertEquals(3, resources2.size)
        assertEquals(4, sum[WOOD])
        assertEquals(4, sum.size)
    }

    @Test
    fun plus_differentemptyResources() {
        val resources1 = resourcesOf(WOOD to 1)
        val resources2 = resourcesOf(ORE to 3)
        val sum = resources1 + resources2

        assertEquals(1, resources1.size)
        assertEquals(3, resources2.size)
        assertEquals(1, sum[WOOD])
        assertEquals(3, sum[ORE])
        assertEquals(4, sum.size)
    }

    @Test
    fun plus_overlappingemptyResources() {
        val resources1 = resourcesOf(WOOD to 1)
        val resources2 = resourcesOf(WOOD to 2, ORE to 4)
        val sum = resources1 + resources2

        assertEquals(1, resources1.size)
        assertEquals(6, resources2.size)
        assertEquals(3, sum[WOOD])
        assertEquals(4, sum[ORE])
        assertEquals(7, sum.size)
    }

    @Test
    fun remove_some() {
        val resources = mutableResourcesOf(WOOD to 3)
        resources.remove(WOOD, 2)
        assertEquals(1, resources[WOOD])
        assertEquals(1, resources.size)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun remove_all() {
        val resources = mutableResourcesOf(WOOD to 3)
        resources.remove(WOOD, 3)
        assertEquals(0, resources[WOOD])
        assertEquals(0, resources.size)
        assertTrue(resources.isEmpty())
    }

    @Test
    fun remove_tooMany() {
        val resources = mutableResourcesOf(WOOD to 2)

        assertFailsWith<NoSuchElementException> {
            resources.remove(WOOD, 3)
        }
    }

    @Test
    fun addAll_empty() {
        val resources = mutableResourcesOf(STONE to 1, CLAY to 3)

        val emptyResources = emptyResources()

        resources.add(emptyResources)
        assertEquals(1, resources[STONE])
        assertEquals(3, resources[CLAY])
        assertEquals(0, resources[ORE])
        assertEquals(0, resources[GLASS])
        assertEquals(0, resources[LOOM])
        assertEquals(4, resources.size)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun addAll_zeros() {
        val resources = mutableResourcesOf(STONE to 1, CLAY to 3)

        val emptyResources = resourcesOf(STONE to 0, CLAY to 0)

        resources.add(emptyResources)
        assertEquals(1, resources[STONE])
        assertEquals(3, resources[CLAY])
        assertEquals(0, resources[ORE])
        assertEquals(0, resources[GLASS])
        assertEquals(0, resources[LOOM])
        assertEquals(4, resources.size)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun addAll_same() {
        val resources = mutableResourcesOf(STONE to 1, CLAY to 3)
        val resources2 = resourcesOf(STONE to 2, CLAY to 6)

        resources.add(resources2)
        assertEquals(3, resources[STONE])
        assertEquals(9, resources[CLAY])
        assertEquals(0, resources[ORE])
        assertEquals(0, resources[GLASS])
        assertEquals(0, resources[LOOM])
        assertEquals(12, resources.size)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun addAll_overlap() {
        val resources = mutableResourcesOf(STONE to 1, CLAY to 3)
        val resources2 = resourcesOf(CLAY to 6, ORE to 4)

        resources.add(resources2)
        assertEquals(1, resources[STONE])
        assertEquals(9, resources[CLAY])
        assertEquals(4, resources[ORE])
        assertEquals(0, resources[GLASS])
        assertEquals(0, resources[LOOM])
        assertEquals(14, resources.size)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun contains_emptyContainsEmpty() {
        val emptyResources = emptyResources()
        val emptyResources2 = emptyResources()
        assertTrue(emptyResources.containsAll(emptyResources2))
    }

    @Test
    fun contains_singleTypeContainsEmpty() {
        val resources = resourcesOf(STONE to 1)
        val emptyResources = emptyResources()

        assertTrue(resources.containsAll(emptyResources))
    }

    @Test
    fun contains_multipleTypesContainsEmpty() {
        val resources = resourcesOf(STONE to 1, CLAY to 3)
        val emptyResources = emptyResources()

        assertTrue(resources.containsAll(emptyResources))
    }

    @Test
    fun contains_self() {
        val resources = resourcesOf(STONE to 1, CLAY to 3)

        assertTrue(resources.containsAll(resources))
    }

    @Test
    fun contains_allOfEachType() {
        val resources = resourcesOf(STONE to 1, CLAY to 3)
        val resources2 = resourcesOf(STONE to 1, CLAY to 3)

        assertTrue(resources.containsAll(resources2))
    }

    @Test
    fun contains_someOfEachType() {
        val resources = resourcesOf(STONE to 2, CLAY to 4)
        val resources2 = resourcesOf(STONE to 1, CLAY to 3)

        assertTrue(resources.containsAll(resources2))
    }

    @Test
    fun contains_someOfSomeTypes() {
        val resources = resourcesOf(STONE to 2, CLAY to 4)
        val resources2 = resourcesOf(CLAY to 3)

        assertTrue(resources.containsAll(resources2))
    }

    @Test
    fun contains_allOfSomeTypes() {
        val resources = resourcesOf(STONE to 2, CLAY to 4)
        val resources2 = resourcesOf(CLAY to 4)

        assertTrue(resources.containsAll(resources2))
    }

    @Test
    fun minus_empty() {
        val resources = resourcesOf(STONE to 1, CLAY to 3)
        val emptyResources = emptyResources()

        val diff = resources.minus(emptyResources)
        assertEquals(1, diff[STONE])
        assertEquals(3, diff[CLAY])
        assertEquals(0, diff[ORE])
        assertEquals(0, diff[GLASS])
        assertEquals(0, diff[LOOM])
    }

    @Test
    fun minus_self() {
        val resources = resourcesOf(STONE to 1, CLAY to 3)

        val diff = resources.minus(resources)
        assertEquals(0, diff[STONE])
        assertEquals(0, diff[CLAY])
        assertEquals(0, diff[ORE])
        assertEquals(0, diff[GLASS])
        assertEquals(0, diff[LOOM])
    }

    @Test
    fun minus_allOfEachType() {
        val resources = resourcesOf(STONE to 1, CLAY to 3)
        val resources2 = resourcesOf(STONE to 1, CLAY to 3)

        val diff = resources.minus(resources2)
        assertEquals(0, diff[STONE])
        assertEquals(0, diff[CLAY])
        assertEquals(0, diff[ORE])
        assertEquals(0, diff[GLASS])
        assertEquals(0, diff[LOOM])
    }

    @Test
    fun minus_someOfEachType() {
        val resources = resourcesOf(STONE to 2, CLAY to 4)
        val resources2 = resourcesOf(STONE to 1, CLAY to 3)

        val diff = resources.minus(resources2)
        assertEquals(1, diff[STONE])
        assertEquals(1, diff[CLAY])
        assertEquals(0, diff[ORE])
        assertEquals(0, diff[GLASS])
        assertEquals(0, diff[LOOM])
    }

    @Test
    fun minus_someOfSomeTypes() {
        val resources = resourcesOf(STONE to 2, CLAY to 4)
        val resources2 = resourcesOf(CLAY to 3)

        val diff = resources.minus(resources2)
        assertEquals(2, diff[STONE])
        assertEquals(1, diff[CLAY])
        assertEquals(0, diff[ORE])
        assertEquals(0, diff[GLASS])
        assertEquals(0, diff[LOOM])
    }

    @Test
    fun minus_allOfSomeTypes() {
        val resources = resourcesOf(STONE to 2, CLAY to 4)
        val resources2 = resourcesOf(CLAY to 4)

        val diff = resources.minus(resources2)
        assertEquals(2, diff[STONE])
        assertEquals(0, diff[CLAY])
        assertEquals(0, diff[ORE])
        assertEquals(0, diff[GLASS])
        assertEquals(0, diff[LOOM])
    }

    @Test
    fun minus_tooMuchOfExistingType() {
        val resources = resourcesOf(CLAY to 4)
        val resources2 = resourcesOf(CLAY to 5)

        val diff = resources.minus(resources2)
        assertEquals(0, diff[CLAY])
    }

    @Test
    fun minus_someOfAnAbsentType() {
        val resources = emptyResources()
        val resources2 = resourcesOf(LOOM to 5)

        val diff = resources.minus(resources2)
        assertEquals(0, diff[LOOM])
    }

    @Test
    fun minus_someOfATypeWithZero() {
        val resources = resourcesOf(LOOM to 0)
        val resources2 = resourcesOf(LOOM to 5)

        val diff = resources.minus(resources2)
        assertEquals(0, diff[LOOM])
    }

    @Test
    fun isEmpty_noElement() {
        val resources = emptyResources()
        assertTrue(resources.isEmpty())
    }

    @Test
    fun isEmpty_singleZeroElement() {
        val resources = resourcesOf(LOOM to 0)
        assertTrue(resources.isEmpty())
    }

    @Test
    fun isEmpty_multipleZeroElements() {
        val resources = resourcesOf(WOOD to 0, ORE to 0, LOOM to 0)
        assertTrue(resources.isEmpty())
    }

    @Test
    fun isEmpty_singleElementMoreThanZero() {
        val resources = resourcesOf(LOOM to 3)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun isEmpty_mixedZeroAndNonZeroElements() {
        val resources = resourcesOf(WOOD to 0, LOOM to 3)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun isEmpty_mixedZeroAndNonZeroElements_reverseOrder() {
        val resources = resourcesOf(ORE to 3, PAPYRUS to 0)
        assertFalse(resources.isEmpty())
    }

    @Test
    fun equals_trueWhenSame() {
        val resources = emptyResources()
        assertEquals(resources, resources)
    }

    @Test
    fun equals_trueWhenSameContent() {
        val resources1 = mutableResourcesOf()
        val resources2 = mutableResourcesOf()
        assertTrue(resources1 == resources2)
        resources1.add(GLASS, 1)
        resources2.add(GLASS, 1)
        assertTrue(resources1 == resources2)
    }

    @Test
    fun hashCode_sameWhenSameContent() {
        val resources1 = mutableResourcesOf()
        val resources2 = mutableResourcesOf()
        assertEquals(resources1.hashCode(), resources2.hashCode())
        resources1.add(GLASS, 1)
        resources2.add(GLASS, 1)
        assertEquals(resources1.hashCode(), resources2.hashCode())
    }
}
