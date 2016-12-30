package org.luxons.sevenwonders.actions;

import java.util.List;
import javax.validation.constraints.NotNull;

public class ReorderPlayersAction {

    @NotNull
    private List<String> orderedPlayers;

    public List<String> getOrderedPlayers() {
        return orderedPlayers;
    }

    public void setOrderedPlayers(List<String> orderedPlayers) {
        this.orderedPlayers = orderedPlayers;
    }
}
