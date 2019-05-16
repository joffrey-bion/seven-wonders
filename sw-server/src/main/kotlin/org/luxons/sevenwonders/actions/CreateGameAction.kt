package org.luxons.sevenwonders.actions

import org.hildan.livedoc.core.annotations.types.ApiType
import org.hildan.livedoc.core.annotations.types.ApiTypeProperty
import org.luxons.sevenwonders.doc.Documentation
import javax.validation.constraints.Size

/**
 * The action to create a game.
 */
@ApiType(group = Documentation.GROUP_ACTIONS)
class CreateGameAction(
    /**
     * The name of the game to create.
     */
    @Size(min = 2, max = 30)
    @ApiTypeProperty(required = true)
    val gameName: String
)
