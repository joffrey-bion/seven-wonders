package org.luxons.sevenwonders.game.wonders;

import java.util.List;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.CardBack;
import org.luxons.sevenwonders.game.cards.Requirements;
import org.luxons.sevenwonders.game.effects.Effect;
import org.luxons.sevenwonders.game.resources.BoughtResources;

public class WonderStage {

    private final Requirements requirements;

    private final List<Effect> effects;

    private CardBack cardBack;

    public WonderStage(Requirements requirements, List<Effect> effects) {
        this.requirements = requirements;
        this.effects = effects;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public CardBack getCardBack() {
        return cardBack;
    }

    public boolean isBuilt() {
        return cardBack != null;
    }

    public boolean isBuildable(Table table, int playerIndex, List<BoughtResources> boughtResources) {
        Board board = table.getBoard(playerIndex);
        return requirements.areMetWithHelpBy(board, boughtResources);
    }

    void build(CardBack cardBack) {
        this.cardBack = cardBack;
    }

    void activate(Table table, int playerIndex, List<BoughtResources> boughtResources) {
        effects.forEach(e -> e.apply(table, playerIndex));
    }
}
