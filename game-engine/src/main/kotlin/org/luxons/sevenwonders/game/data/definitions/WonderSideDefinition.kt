package org.luxons.sevenwonders.game.data.definitions

import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.wonders.WonderStage

internal class WonderSideDefinition(
    val initialResource: ResourceType,
    private val stages: List<WonderStageDefinition>,
    val image: String
) {
    fun createStages(): List<WonderStage> {
        return stages.map { def -> def.create() }
    }
}
