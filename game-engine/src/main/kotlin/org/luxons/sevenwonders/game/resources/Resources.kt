package org.luxons.sevenwonders.game.resources

import java.util.NoSuchElementException

class Resources(quantities: Map<ResourceType, Int> = emptyMap()) {

    private val quantities: MutableMap<ResourceType, Int> = quantities.toMutableMap()

    constructor(singleResource: ResourceType): this(mapOf(singleResource to 1))

    val isEmpty: Boolean
        get() = size() == 0

    fun add(type: ResourceType, quantity: Int) {
        quantities.merge(type, quantity) { x, y -> x + y }
    }

    fun remove(type: ResourceType, quantity: Int) {
        if (getQuantity(type) < quantity) {
            throw NoSuchElementException("Can't remove $quantity resources of type $type")
        }
        quantities.computeIfPresent(type) { _, oldQty -> oldQty - quantity }
    }

    fun addAll(resources: Resources) {
        resources.quantities.forEach { type, quantity -> this.add(type, quantity) }
    }

    fun getQuantity(type: ResourceType): Int = quantities[type] ?: 0

    fun asList(): List<ResourceType> = quantities.flatMap { e -> List(e.value) { e.key } }

    fun containsAll(resources: Resources): Boolean = resources.quantities.all { it.value <= this.getQuantity(it.key) }

    operator fun plus(resources: Resources): Resources {
        val new = Resources(this.quantities)
        new.addAll(resources)
        return new
    }

    /**
     * Creates a new [Resources] object containing these resources minus the given resources.
     *
     * @param resources
     * the resources to subtract from these resources
     *
     * @return a new [Resources] object containing these resources minus the given resources.
     */
    operator fun minus(resources: Resources): Resources {
        val diff = Resources()
        quantities.forEach { type, count ->
            val remainder = count - resources.getQuantity(type)
            diff.quantities[type] = Math.max(0, remainder)
        }
        return diff
    }

    fun size(): Int = quantities.values.sum()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Resources

        if (quantities != other.quantities) return false

        return true
    }

    override fun hashCode(): Int = quantities.hashCode()
}
