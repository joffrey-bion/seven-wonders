package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.api.Table;

public class SpecialActionTrigger implements Effect {

    private final SpecialAction specialAction;

    public SpecialActionTrigger(SpecialAction specialAction) {
        this.specialAction = specialAction;
    }

    public SpecialAction getSpecialAction() {
        return specialAction;
    }

    @Override
    public void apply(Table table, int playerIndex) {
        // TODO do something to activate the special action
    }

    @Override
    public int computePoints(Table table, int playerIndex) {
        return 0;
    }
}
