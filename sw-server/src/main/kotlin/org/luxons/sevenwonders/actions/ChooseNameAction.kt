package org.luxons.sevenwonders.actions

import org.hildan.livedoc.core.annotations.types.ApiType
import org.hildan.livedoc.core.annotations.types.ApiTypeProperty
import org.luxons.sevenwonders.doc.Documentation
import javax.validation.constraints.Size

/**
 * The action to choose the player's name. This is the first action that should be called.
 */
@ApiType(group = Documentation.GROUP_ACTIONS)
class ChooseNameAction(
    /**
     * The display name of the player. May contain spaces and special characters.
     */
    @Size(min = 2, max = 20)
    @ApiTypeProperty(required = true)
    val playerName: String
)
