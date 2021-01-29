package org.luxons.sevenwonders.engine.data.definitions

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.engine.cards.Requirements
import org.luxons.sevenwonders.engine.data.serializers.ResourceTypeSerializer
import org.luxons.sevenwonders.engine.wonders.Wonder
import org.luxons.sevenwonders.engine.wonders.WonderStage
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.wonders.WonderName
import org.luxons.sevenwonders.model.wonders.WonderSide

@Serializable
internal class WonderDefinition(
    val name: WonderName,
    val sides: Map<WonderSide, WonderSideDefinition>,
) {
    fun create(wonderSide: WonderSide): Wonder = sides[wonderSide]!!.createWonder(name)
}

@Serializable
internal class WonderSideDefinition(
    @Serializable(with = ResourceTypeSerializer::class)
    private val initialResource: ResourceType,
    private val stages: List<WonderStageDefinition>,
    val image: String,
) {
    fun createWonder(name: String): Wonder = Wonder(name, initialResource, stages.map { it.create() }, image)
}

@Serializable
internal class WonderStageDefinition(
    private val requirements: Requirements,
    private val effects: EffectsDefinition,
) {
    fun create(): WonderStage = WonderStage(requirements, effects.create())
}
