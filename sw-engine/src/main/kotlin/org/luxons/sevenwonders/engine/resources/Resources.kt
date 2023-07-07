package org.luxons.sevenwonders.engine.resources

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.engine.data.serializers.ResourcesSerializer
import org.luxons.sevenwonders.model.resources.ResourceType

fun emptyResources(): Resources = MutableResources()

fun resourcesOf(singleResource: ResourceType): Resources = mapOf(singleResource to 1).toMutableResources()

fun resourcesOf(vararg resources: ResourceType): Resources = mutableResourcesOf(*resources)

fun resourcesOf(vararg resources: Pair<ResourceType, Int>): Resources = mutableResourcesOf(*resources)

fun Iterable<Pair<ResourceType, Int>>.toResources(): Resources = toMutableResources()

/**
 * Creates [Resources] from a copy of the given map. Future modifications to the input map won't affect the return
 * resources.
 */
fun Map<ResourceType, Int>.toResources(): Resources = toMutableResources()

fun Iterable<Resources>.merge(): Resources = fold(MutableResources()) { r1, r2 -> r1.add(r2); r1 }

internal fun mutableResourcesOf() = MutableResources()

internal fun mutableResourcesOf(vararg resources: ResourceType): MutableResources =
    resources.map { it to 1 }.toMutableResources()

internal fun mutableResourcesOf(vararg resources: Pair<ResourceType, Int>) = resources.asIterable().toMutableResources()

internal fun Iterable<Pair<ResourceType, Int>>.toMutableResources(): MutableResources =
    fold(MutableResources()) { mr, (type, qty) -> mr.add(type, qty); mr }

internal fun Map<ResourceType, Int>.toMutableResources(): MutableResources = MutableResources(toMutableMap())

internal fun Resources.toMutableResources(): MutableResources = quantities.toMutableResources()

@Serializable(with = ResourcesSerializer::class)
interface Resources {

    val quantities: Map<ResourceType, Int>

    val size: Int
        get() = quantities.values.sum()

    fun isEmpty(): Boolean = size == 0

    operator fun get(key: ResourceType): Int = quantities.getOrDefault(key, 0)

    fun containsAll(resources: Resources): Boolean = resources.quantities.all { it.value <= this[it.key] }

    operator fun plus(resources: Resources): Resources =
        ResourceType.entries.map { it to this[it] + resources[it] }.toResources()

    /**
     * Returns new resources containing these resources minus the given [resources]. If the given resources contain
     * more than these resources contain for a resource type, then the resulting resources will contain none of that
     * type.
     */
    operator fun minus(resources: Resources): Resources =
        quantities.mapValues { (type, q) -> (q - resources[type]).coerceAtLeast(0) }.toResources()

    fun toList(): List<ResourceType> = quantities.flatMap { (type, quantity) -> List(quantity) { type } }

    fun copy(): Resources = quantities.toResources()
}

class MutableResources(
    override val quantities: MutableMap<ResourceType, Int> = mutableMapOf(),
) : Resources {

    fun add(type: ResourceType, quantity: Int) {
        quantities.merge(type, quantity) { x, y -> x + y }
    }

    fun add(resources: Resources) = resources.quantities.forEach { (type, quantity) -> add(type, quantity) }

    fun remove(type: ResourceType, quantity: Int) {
        if (this[type] < quantity) {
            throw NoSuchElementException("Can't remove $quantity resources of type $type")
        }
        quantities.computeIfPresent(type) { _, oldQty -> oldQty - quantity }
    }

    override fun equals(other: Any?): Boolean =
        other is Resources && quantities.filterValues { it > 0 } == other.quantities.filterValues { it > 0 }

    override fun hashCode(): Int = quantities.filterValues { it > 0 }.hashCode()

    override fun toString(): String = "$quantities"
}
