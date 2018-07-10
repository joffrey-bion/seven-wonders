package org.luxons.sevenwonders.game.data.definitions

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.data.WonderSide
import org.luxons.sevenwonders.game.wonders.Wonder

internal class WonderDefinition(
    private val name: String,
    private val sides: Map<WonderSide, WonderSideDefinition>
) {
    fun create(settings: Settings): Wonder {
        val wonderSideDef = sides[settings.pickWonderSide()]!!
        val stages = wonderSideDef.createStages()
        return Wonder(name, wonderSideDef.initialResource, stages, wonderSideDef.image)
    }
}
