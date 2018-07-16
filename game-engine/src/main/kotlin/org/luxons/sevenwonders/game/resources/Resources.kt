package org.luxons.sevenwonders.game.resources

import java.util.NoSuchElementException

fun emptyResources(): Resources = MutableResources()

fun resourcesOf(singleResource: ResourceType): Resources = MutableResources(mutableMapOf(singleResource to 1))

fun resourcesOf(vararg resources: ResourceType): Resources =
    resources.fold(MutableResources()) { rs, r -> rs.add(r, 1); rs }

fun resourcesOf(resources: Iterable<ResourceType>): Resources =
    resources.fold(MutableResources()) { rs, r -> rs.add(r, 1); rs }

fun resourcesOf(vararg resources: Pair<ResourceType, Int>): Resources =
    resources.fold(MutableResources()) { rs, (type, qty) -> rs.add(type, qty); rs }

internal fun mutableResourcesOf() = MutableResources()

internal fun mutableResourcesOf(vararg resources: Pair<ResourceType, Int>) =
    resources.fold(MutableResources()) { rs, (type, qty) -> rs.add(type, qty); rs }

fun Iterable<ResourceType>.toResources(): Resources = resourcesOf(this)

fun Iterable<Resources>.merge(): Resources = fold(MutableResources()) { r1, r2 -> r1.add(r2); r1}

internal fun Resources.toMutableResources(): MutableResources {
    val resources = MutableResources()
    resources.add(this)
    return resources
}

interface Resources {

    val quantities: Map<ResourceType, Int>

    val size: Int
        get() = quantities.map { it.value }.sum()

    fun isEmpty(): Boolean = size == 0

    operator fun get(key: ResourceType): Int = quantities.getOrDefault(key, 0)

    fun containsAll(resources: Resources): Boolean = resources.quantities.all { it.value <= this[it.key] }

    operator fun plus(resources: Resources): Resources {
        val new = MutableResources()
        new.add(this)
        new.add(resources)
        return new
    }

    operator fun minus(resources: Resources): Resources {
        val diff = MutableResources()
        quantities.forEach { type, count ->
            val remainder = count - resources[type]
            diff.quantities[type] = Math.max(0, remainder)
        }
        return diff
    }

    fun toList(): List<ResourceType> = quantities.flatMap { (type, quantity) -> List(quantity) { type } }
}

data class MutableResources(
    override val quantities: MutableMap<ResourceType, Int> = mutableMapOf()
) : Resources {

    fun add(type: ResourceType, quantity: Int) {
        quantities.merge(type, quantity) { x, y -> x + y }
    }

    fun add(resources: Resources) = resources.quantities.forEach { type, quantity -> this.add(type, quantity) }

    fun remove(type: ResourceType, quantity: Int) {
        if (this[type] < quantity) {
            throw NoSuchElementException("Can't remove $quantity resources of type $type")
        }
        quantities.computeIfPresent(type) { _, oldQty -> oldQty - quantity }
        // to ensure equals() work properly
        if (quantities[type] == 0) {
            quantities.remove(type)
        }
    }
}
