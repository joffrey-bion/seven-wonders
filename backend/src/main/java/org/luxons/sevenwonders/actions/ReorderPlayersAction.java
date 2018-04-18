package org.luxons.sevenwonders.actions;

import java.util.List;
import javax.validation.constraints.NotNull;

import org.hildan.livedoc.core.annotations.types.ApiType;
import org.luxons.sevenwonders.doc.Documentation;

/**
 * The action to update the order of the players around the table. Can only be called in the lobby by the owner of the
 * game.
 */
@ApiType(group = Documentation.GROUP_ACTIONS)
public class ReorderPlayersAction {

    @NotNull
    private List<String> orderedPlayers;

    /**
     * @return the list of usernames of the players, in the new order
     */
    public List<String> getOrderedPlayers() {
        return orderedPlayers;
    }

    public void setOrderedPlayers(List<String> orderedPlayers) {
        this.orderedPlayers = orderedPlayers;
    }
}
