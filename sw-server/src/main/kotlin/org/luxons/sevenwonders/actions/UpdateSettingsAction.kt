package org.luxons.sevenwonders.actions

import org.hildan.livedoc.core.annotations.types.ApiType
import org.hildan.livedoc.core.annotations.types.ApiTypeProperty
import org.luxons.sevenwonders.doc.Documentation
import org.luxons.sevenwonders.model.CustomizableSettings

/**
 * The action to update the settings of the game. Can only be called in the lobby by the owner of the game.
 */
@ApiType(group = Documentation.GROUP_ACTIONS)
class UpdateSettingsAction(
    /**
     * The new values for the settings.
     */
    @ApiTypeProperty(required = true)
    val settings: CustomizableSettings
)
