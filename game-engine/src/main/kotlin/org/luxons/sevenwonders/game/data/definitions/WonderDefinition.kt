package org.luxons.sevenwonders.game.data.definitions

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.data.WonderSide
import org.luxons.sevenwonders.game.wonders.Wonder

internal data class WonderDefinition(
    private val name: String,
    private val sides: Map<WonderSide, WonderSideDefinition>
) {
    fun create(settings: Settings): Wonder {
        val wonder = Wonder()
        wonder.name = name

        val wonderSideDef = sides[settings.pickWonderSide()]!!
        wonder.initialResource = wonderSideDef.initialResource
        wonder.stages = wonderSideDef.createStages()
        wonder.image = wonderSideDef.image
        return wonder
    }
}
