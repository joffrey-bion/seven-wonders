package org.luxons.sevenwonders.game.data.definitions

import org.luxons.sevenwonders.game.cards.Requirements
import org.luxons.sevenwonders.game.data.WonderSide
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.wonders.Wonder
import org.luxons.sevenwonders.game.wonders.WonderStage

internal class WonderDefinition(
    private val name: String,
    private val sides: Map<WonderSide, WonderSideDefinition>
) {
    fun create(wonderSide: WonderSide): Wonder = sides[wonderSide]!!.createWonder(name)
}

internal class WonderSideDefinition(
    private val initialResource: ResourceType,
    private val stages: List<WonderStageDefinition>,
    private val image: String
) {
    fun createWonder(name: String): Wonder = Wonder(name, initialResource, stages.map { it.create() }, image)
}

internal class WonderStageDefinition(
    private val requirements: Requirements,
    private val effects: EffectsDefinition
) {
    fun create(): WonderStage = WonderStage(requirements, effects.create())
}
