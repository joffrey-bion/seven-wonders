package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.api.Table;

public class SpecialAbilityTrigger implements Effect {

    private final SpecialAbility specialAbility;

    public SpecialAbilityTrigger(SpecialAbility specialAbility) {
        this.specialAbility = specialAbility;
    }

    public SpecialAbility getSpecialAbility() {
        return specialAbility;
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
