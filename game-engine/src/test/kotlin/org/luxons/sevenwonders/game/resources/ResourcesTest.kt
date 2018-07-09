package org.luxons.sevenwonders.game.resources

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.util.NoSuchElementException

class ResourcesTest {

    @JvmField
    @Rule
    var thrown = ExpectedException.none()

    @Test
    fun init_shouldBeEmpty() {
        val resources = Resources()
        for (resourceType in ResourceType.values()) {
            assertEquals(0, resources.getQuantity(resourceType).toLong())
        }
        assertEquals(0, resources.size().toLong())
        assertTrue(resources.isEmpty)
    }

    @Test
    fun add_zero() {
        val resources = Resources()
        resources.add(ResourceType.CLAY, 0)
        assertEquals(0, resources.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(0, resources.size().toLong())
        assertTrue(resources.isEmpty)
    }

    @Test
    fun add_simple() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 3)
        assertEquals(3, resources.getQuantity(ResourceType.WOOD).toLong())
        assertEquals(3, resources.size().toLong())
        assertFalse(resources.isEmpty)
    }

    @Test
    fun add_multipleCallsStacked() {
        val resources = Resources()
        resources.add(ResourceType.ORE, 3)
        resources.add(ResourceType.ORE, 2)
        assertEquals(5, resources.getQuantity(ResourceType.ORE).toLong())
        assertEquals(5, resources.size().toLong())
        assertFalse(resources.isEmpty)
    }

    @Test
    fun add_interlaced() {
        val resources = Resources()
        resources.add(ResourceType.GLASS, 3)
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.WOOD, 4)
        resources.add(ResourceType.GLASS, 2)
        assertEquals(5, resources.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(10, resources.size().toLong())
        assertFalse(resources.isEmpty)
    }

    @Test
    fun remove_some() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 3)
        resources.remove(ResourceType.WOOD, 2)
        assertEquals(1, resources.getQuantity(ResourceType.WOOD).toLong())
        assertEquals(1, resources.size().toLong())
        assertFalse(resources.isEmpty)
    }

    @Test
    fun remove_all() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 3)
        resources.remove(ResourceType.WOOD, 3)
        assertEquals(0, resources.getQuantity(ResourceType.WOOD).toLong())
        assertEquals(0, resources.size().toLong())
        assertTrue(resources.isEmpty)
    }

    @Test
    fun remove_tooMany() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 2)

        thrown.expect(NoSuchElementException::class.java)
        resources.remove(ResourceType.WOOD, 3)
    }

    @Test
    fun addAll_empty() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 3)

        val emptyResources = Resources()

        resources.addAll(emptyResources)
        assertEquals(1, resources.getQuantity(ResourceType.STONE).toLong())
        assertEquals(3, resources.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.ORE).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.LOOM).toLong())
        assertEquals(4, resources.size().toLong())
        assertFalse(resources.isEmpty)
    }

    @Test
    fun addAll_zeros() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 3)

        val emptyResources = Resources()
        emptyResources.add(ResourceType.STONE, 0)
        emptyResources.add(ResourceType.CLAY, 0)

        resources.addAll(emptyResources)
        assertEquals(1, resources.getQuantity(ResourceType.STONE).toLong())
        assertEquals(3, resources.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.ORE).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.LOOM).toLong())
        assertEquals(4, resources.size().toLong())
        assertFalse(resources.isEmpty)
    }

    @Test
    fun addAll_same() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 3)

        val resources2 = Resources()
        resources.add(ResourceType.STONE, 2)
        resources.add(ResourceType.CLAY, 6)

        resources.addAll(resources2)
        assertEquals(3, resources.getQuantity(ResourceType.STONE).toLong())
        assertEquals(9, resources.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.ORE).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.LOOM).toLong())
        assertEquals(12, resources.size().toLong())
        assertFalse(resources.isEmpty)
    }

    @Test
    fun addAll_overlap() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 3)

        val resources2 = Resources()
        resources.add(ResourceType.CLAY, 6)
        resources.add(ResourceType.ORE, 4)

        resources.addAll(resources2)
        assertEquals(1, resources.getQuantity(ResourceType.STONE).toLong())
        assertEquals(9, resources.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(4, resources.getQuantity(ResourceType.ORE).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(0, resources.getQuantity(ResourceType.LOOM).toLong())
        assertEquals(14, resources.size().toLong())
        assertFalse(resources.isEmpty)
    }

    @Test
    fun contains_emptyContainsEmpty() {
        val emptyResources = Resources()
        val emptyResources2 = Resources()
        assertTrue(emptyResources.contains(emptyResources2))
    }

    @Test
    fun contains_singleTypeContainsEmpty() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)

        val emptyResources = Resources()

        assertTrue(resources.contains(emptyResources))
    }

    @Test
    fun contains_multipleTypesContainsEmpty() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 3)

        val emptyResources = Resources()

        assertTrue(resources.contains(emptyResources))
    }

    @Test
    fun contains_self() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 3)

        assertTrue(resources.contains(resources))
    }

    @Test
    fun contains_allOfEachType() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 3)

        val resources2 = Resources()
        resources2.add(ResourceType.STONE, 1)
        resources2.add(ResourceType.CLAY, 3)

        assertTrue(resources.contains(resources2))
    }

    @Test
    fun contains_someOfEachType() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 2)
        resources.add(ResourceType.CLAY, 4)

        val resources2 = Resources()
        resources2.add(ResourceType.STONE, 1)
        resources2.add(ResourceType.CLAY, 3)

        assertTrue(resources.contains(resources2))
    }

    @Test
    fun contains_someOfSomeTypes() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 2)
        resources.add(ResourceType.CLAY, 4)

        val resources2 = Resources()
        resources2.add(ResourceType.CLAY, 3)

        assertTrue(resources.contains(resources2))
    }

    @Test
    fun contains_allOfSomeTypes() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 2)
        resources.add(ResourceType.CLAY, 4)

        val resources2 = Resources()
        resources2.add(ResourceType.CLAY, 4)

        assertTrue(resources.contains(resources2))
    }

    @Test
    fun minus_empty() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 3)

        val emptyResources = Resources()

        val diff = resources.minus(emptyResources)
        assertEquals(1, diff.getQuantity(ResourceType.STONE).toLong())
        assertEquals(3, diff.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.ORE).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.LOOM).toLong())
    }

    @Test
    fun minus_self() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 3)

        val diff = resources.minus(resources)
        assertEquals(0, diff.getQuantity(ResourceType.STONE).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.ORE).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.LOOM).toLong())
    }

    @Test
    fun minus_allOfEachType() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 1)
        resources.add(ResourceType.CLAY, 3)

        val resources2 = Resources()
        resources2.add(ResourceType.STONE, 1)
        resources2.add(ResourceType.CLAY, 3)

        val diff = resources.minus(resources2)
        assertEquals(0, diff.getQuantity(ResourceType.STONE).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.ORE).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.LOOM).toLong())
    }

    @Test
    fun minus_someOfEachType() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 2)
        resources.add(ResourceType.CLAY, 4)

        val resources2 = Resources()
        resources2.add(ResourceType.STONE, 1)
        resources2.add(ResourceType.CLAY, 3)

        val diff = resources.minus(resources2)
        assertEquals(1, diff.getQuantity(ResourceType.STONE).toLong())
        assertEquals(1, diff.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.ORE).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.LOOM).toLong())
    }

    @Test
    fun minus_someOfSomeTypes() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 2)
        resources.add(ResourceType.CLAY, 4)

        val resources2 = Resources()
        resources2.add(ResourceType.CLAY, 3)

        val diff = resources.minus(resources2)
        assertEquals(2, diff.getQuantity(ResourceType.STONE).toLong())
        assertEquals(1, diff.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.ORE).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.LOOM).toLong())
    }

    @Test
    fun minus_allOfSomeTypes() {
        val resources = Resources()
        resources.add(ResourceType.STONE, 2)
        resources.add(ResourceType.CLAY, 4)

        val resources2 = Resources()
        resources2.add(ResourceType.CLAY, 4)

        val diff = resources.minus(resources2)
        assertEquals(2, diff.getQuantity(ResourceType.STONE).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.CLAY).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.ORE).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.GLASS).toLong())
        assertEquals(0, diff.getQuantity(ResourceType.LOOM).toLong())
    }

    @Test
    fun minus_tooMuchOfExistingType() {
        val resources = Resources()
        resources.add(ResourceType.CLAY, 4)

        val resources2 = Resources()
        resources2.add(ResourceType.CLAY, 5)

        val diff = resources.minus(resources2)
        assertEquals(0, diff.getQuantity(ResourceType.CLAY).toLong())
    }

    @Test
    fun minus_someOfAnAbsentType() {
        val resources = Resources()

        val resources2 = Resources()
        resources2.add(ResourceType.LOOM, 5)

        val diff = resources.minus(resources2)
        assertEquals(0, diff.getQuantity(ResourceType.LOOM).toLong())
    }

    @Test
    fun minus_someOfATypeWithZero() {
        val resources = Resources()
        resources.add(ResourceType.LOOM, 0)

        val resources2 = Resources()
        resources2.add(ResourceType.LOOM, 5)

        val diff = resources.minus(resources2)
        assertEquals(0, diff.getQuantity(ResourceType.LOOM).toLong())
    }

    @Test
    fun isEmpty_noElement() {
        val resources = Resources()
        assertTrue(resources.isEmpty)
    }

    @Test
    fun isEmpty_singleZeroElement() {
        val resources = Resources()
        resources.add(ResourceType.LOOM, 0)
        assertTrue(resources.isEmpty)
    }

    @Test
    fun isEmpty_multipleZeroElements() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 0)
        resources.add(ResourceType.ORE, 0)
        resources.add(ResourceType.LOOM, 0)
        assertTrue(resources.isEmpty)
    }

    @Test
    fun isEmpty_singleElementMoreThanZero() {
        val resources = Resources()
        resources.add(ResourceType.LOOM, 3)
        assertFalse(resources.isEmpty)
    }

    @Test
    fun isEmpty_mixedZeroAndNonZeroElements() {
        val resources = Resources()
        resources.add(ResourceType.WOOD, 0)
        resources.add(ResourceType.LOOM, 3)
        assertFalse(resources.isEmpty)
    }

    @Test
    fun isEmpty_mixedZeroAndNonZeroElements_reverseOrder() {
        val resources = Resources()
        resources.add(ResourceType.ORE, 3)
        resources.add(ResourceType.PAPYRUS, 0)
        assertFalse(resources.isEmpty)
    }

    @Test
    fun equals_falseWhenNull() {
        val resources = Resources()
        resources.add(ResourceType.GLASS, 1)

        assertFalse(resources == null)
    }

    @Test
    fun equals_falseWhenDifferentClass() {
        val resources = Resources()
        resources.add(ResourceType.GLASS, 1)
        val production = Production()
        production.addFixedResource(ResourceType.GLASS, 1)

        assertFalse(resources == production)
    }

    @Test
    fun equals_trueWhenSame() {
        val resources = Resources()
        assertEquals(resources, resources)
    }

    @Test
    fun equals_trueWhenSameContent() {
        val resources1 = Resources()
        val resources2 = Resources()
        assertTrue(resources1 == resources2)
        resources1.add(ResourceType.GLASS, 1)
        resources2.add(ResourceType.GLASS, 1)
        assertTrue(resources1 == resources2)
    }

    @Test
    fun hashCode_sameWhenSameContent() {
        val resources1 = Resources()
        val resources2 = Resources()
        assertEquals(resources1.hashCode().toLong(), resources2.hashCode().toLong())
        resources1.add(ResourceType.GLASS, 1)
        resources2.add(ResourceType.GLASS, 1)
        assertEquals(resources1.hashCode().toLong(), resources2.hashCode().toLong())
    }
}
