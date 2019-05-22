package org.luxons.sevenwonders.actions

import org.hildan.livedoc.core.annotations.types.ApiType
import org.hildan.livedoc.core.annotations.types.ApiTypeProperty
import org.luxons.sevenwonders.doc.Documentation
import org.luxons.sevenwonders.model.PlayerMove

/**
 * The action to prepare the next move during a game.
 */
@ApiType(group = Documentation.GROUP_ACTIONS)
class PrepareMoveAction(
    /**
     * The move to prepare.
     */
    @ApiTypeProperty(required = true)
    val move: PlayerMove
)
