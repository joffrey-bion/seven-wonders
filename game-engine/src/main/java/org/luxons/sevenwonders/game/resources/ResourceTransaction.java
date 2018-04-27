package org.luxons.sevenwonders.game.resources;

import java.util.Objects;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;

public class ResourceTransaction {

    private final Provider provider;

    private final Resources resources;

    public ResourceTransaction(Provider provider, Resources resources) {
        this.provider = provider;
        this.resources = resources;
    }

    public Provider getProvider() {
        return provider;
    }

    public Resources getResources() {
        return resources;
    }

    void execute(Table table, int playerIndex) {
        Board board = table.getBoard(playerIndex);
        int price = board.getTradingRules().computeCost(this);
        board.removeGold(price);
        RelativeBoardPosition providerPosition = provider.getBoardPosition();
        Board providerBoard = table.getBoard(playerIndex, providerPosition);
        providerBoard.addGold(price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceTransaction that = (ResourceTransaction) o;
        return provider == that.provider && Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, resources);
    }
}
