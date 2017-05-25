package org.luxons.sevenwonders.test.api;

import org.luxons.sevenwonders.game.cards.CardBack;
import org.luxons.sevenwonders.game.cards.Requirements;

public class ApiWonderStage {

    private Requirements requirements;

    private CardBack cardBack;

    private boolean built;

    public Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public CardBack getCardBack() {
        return cardBack;
    }

    public void setCardBack(CardBack cardBack) {
        this.cardBack = cardBack;
    }

    public boolean isBuilt() {
        return built;
    }

    public void setBuilt(boolean built) {
        this.built = built;
    }
}
