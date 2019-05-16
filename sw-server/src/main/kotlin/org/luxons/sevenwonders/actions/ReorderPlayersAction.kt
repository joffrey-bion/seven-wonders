package org.luxons.sevenwonders.actions

import org.hildan.livedoc.core.annotations.types.ApiType
import org.hildan.livedoc.core.annotations.types.ApiTypeProperty
import org.luxons.sevenwonders.doc.Documentation

/**
 * The action to update the order of the players around the table. Can only be called in the lobby by the owner of the
 * game.
 */
@ApiType(group = Documentation.GROUP_ACTIONS)
class ReorderPlayersAction(
    /**
     * The list of usernames of the players, in the new order.
     */
    @ApiTypeProperty(required = true)
    val orderedPlayers: List<String>
)
