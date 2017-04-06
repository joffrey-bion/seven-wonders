package org.luxons.sevenwonders.actions;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "Reorder Players Action",
           description = "The action to update the order of the players around the table. Can only be called in the "
                   + "lobby by the owner of the game.",
           group = "Actions")
public class ReorderPlayersAction {

    @ApiObjectField
    @NotNull
    private List<String> orderedPlayers;

    public List<String> getOrderedPlayers() {
        return orderedPlayers;
    }

    public void setOrderedPlayers(List<String> orderedPlayers) {
        this.orderedPlayers = orderedPlayers;
    }
}
