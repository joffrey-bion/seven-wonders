package org.luxons.sevenwonders.game.resources

import java.util.Arrays
import java.util.EnumSet

class Production internal constructor() {

    val fixedResources = Resources()
    private val alternativeResources: MutableSet<Set<ResourceType>> = mutableSetOf()

    fun getAlternativeResources(): Set<Set<ResourceType>> {
        return alternativeResources
    }

    fun addFixedResource(type: ResourceType, quantity: Int) {
        fixedResources.add(type, quantity)
    }

    fun addChoice(vararg options: ResourceType) {
        val optionSet = EnumSet.copyOf(Arrays.asList(*options))
        alternativeResources.add(optionSet)
    }

    fun addAll(resources: Resources) {
        fixedResources.addAll(resources)
    }

    fun addAll(production: Production) {
        fixedResources.addAll(production.fixedResources)
        alternativeResources.addAll(production.getAlternativeResources())
    }

    internal fun asChoices(): Set<Set<ResourceType>> {
        val fixedAsChoices = fixedResources.asList().map{ EnumSet.of(it) }.toSet()
        return fixedAsChoices + alternativeResources
    }

    operator fun contains(resources: Resources): Boolean {
        if (fixedResources.containsAll(resources)) {
            return true
        }
        return containedInAlternatives(resources - fixedResources)
    }

    private fun containedInAlternatives(resources: Resources): Boolean {
        return containedInAlternatives(resources, alternativeResources)
    }

    private fun containedInAlternatives(resources: Resources, alternatives: MutableSet<Set<ResourceType>>): Boolean {
        if (resources.isEmpty) {
            return true
        }
        for (type in ResourceType.values()) {
            if (resources.getQuantity(type) <= 0) {
                continue
            }
            val candidate = findFirstAlternativeContaining(alternatives, type)
                    ?: return false // no alternative produces the resource of this entry
            resources.remove(type, 1)
            alternatives.remove(candidate)
            val remainingAreContainedToo = containedInAlternatives(resources, alternatives)
            resources.add(type, 1)
            alternatives.add(candidate)
            if (remainingAreContainedToo) {
                return true
            }
        }
        return false
    }

    private fun findFirstAlternativeContaining(
        alternatives: Set<Set<ResourceType>>,
        type: ResourceType
    ): Set<ResourceType>? {
        return alternatives.stream().filter { a -> a.contains(type) }.findAny().orElse(null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Production

        if (fixedResources != other.fixedResources) return false
        if (alternativeResources != other.alternativeResources) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fixedResources.hashCode()
        result = 31 * result + alternativeResources.hashCode()
        return result
    }
}
