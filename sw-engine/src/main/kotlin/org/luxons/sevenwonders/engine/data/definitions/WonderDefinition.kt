package org.luxons.sevenwonders.engine.data.definitions

import org.luxons.sevenwonders.engine.cards.Requirements
import org.luxons.sevenwonders.engine.wonders.Wonder
import org.luxons.sevenwonders.engine.wonders.WonderStage
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.wonders.WonderName
import org.luxons.sevenwonders.model.wonders.WonderSide

internal class WonderDefinition(
    val name: WonderName,
    val sides: Map<WonderSide, WonderSideDefinition>,
) {
    fun create(wonderSide: WonderSide): Wonder = sides[wonderSide]!!.createWonder(name)
}

internal class WonderSideDefinition(
    private val initialResource: ResourceType,
    private val stages: List<WonderStageDefinition>,
    val image: String,
) {
    fun createWonder(name: String): Wonder = Wonder(name, initialResource, stages.map { it.create() }, image)
}

internal class WonderStageDefinition(
    private val requirements: Requirements,
    private val effects: EffectsDefinition,
) {
    fun create(): WonderStage = WonderStage(requirements, effects.create())
}
