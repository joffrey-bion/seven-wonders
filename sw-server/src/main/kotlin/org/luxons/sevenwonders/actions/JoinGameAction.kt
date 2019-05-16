package org.luxons.sevenwonders.actions

import org.hildan.livedoc.core.annotations.types.ApiType
import org.hildan.livedoc.core.annotations.types.ApiTypeProperty
import org.luxons.sevenwonders.doc.Documentation

/**
 * The action to join a game.
 */
@ApiType(group = Documentation.GROUP_ACTIONS)
class JoinGameAction(
    /**
     * The ID of the game to join.
     */
    @ApiTypeProperty(required = true)
    val gameId: Long
)
