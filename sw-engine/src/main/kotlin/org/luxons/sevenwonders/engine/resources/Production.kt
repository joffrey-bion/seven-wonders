package org.luxons.sevenwonders.engine.resources

import org.luxons.sevenwonders.model.resources.ResourceType
import java.util.EnumSet

data class Production internal constructor(
    private val fixedResources: MutableResources = mutableResourcesOf(),
    // cannot be a Set because the same choices can be there multiple times
    private val alternativeResources: MutableList<Set<ResourceType>> = mutableListOf(),
) {
    fun getFixedResources(): Resources = fixedResources

    fun getAlternativeResources(): List<Set<ResourceType>> = alternativeResources

    fun addFixedResource(type: ResourceType, quantity: Int) = fixedResources.add(type, quantity)

    fun addChoice(vararg options: ResourceType) {
        val optionSet = EnumSet.copyOf(options.toList())
        alternativeResources.add(optionSet)
    }

    fun addAll(resources: Resources) = fixedResources.add(resources)

    fun addAll(production: Production) {
        fixedResources.add(production.fixedResources)
        alternativeResources.addAll(production.getAlternativeResources())
    }

    internal fun asChoices(): List<Set<ResourceType>> {
        val fixedAsChoices = fixedResources.toList().map { EnumSet.of(it) }
        return fixedAsChoices + alternativeResources
    }

    operator fun contains(resources: Resources): Boolean {
        if (fixedResources.containsAll(resources)) {
            return true
        }
        return containedInAlternatives(resources - fixedResources)
    }

    private fun containedInAlternatives(resources: Resources): Boolean =
        containedInAlternatives(resources.toMutableResources(), alternativeResources)

    private fun containedInAlternatives(
        resources: MutableResources,
        alternatives: MutableList<Set<ResourceType>>,
    ): Boolean {
        if (resources.isEmpty()) {
            return true
        }
        for (type in ResourceType.values()) {
            if (resources[type] <= 0) {
                continue
            }
            // return if no alternative produces the resource of this entry
            val candidate = alternatives.firstOrNull { a -> a.contains(type) } ?: return false
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
}
