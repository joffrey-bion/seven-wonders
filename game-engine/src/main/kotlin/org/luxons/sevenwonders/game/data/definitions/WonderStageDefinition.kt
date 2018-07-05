package org.luxons.sevenwonders.game.data.definitions

import org.luxons.sevenwonders.game.cards.Requirements
import org.luxons.sevenwonders.game.wonders.WonderStage

internal class WonderStageDefinition(
    private val requirements: Requirements,
    private val effects: EffectsDefinition
) {
    fun create(): WonderStage {
        return WonderStage(requirements, effects.create())
    }
}
