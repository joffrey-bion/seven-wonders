package org.luxons.sevenwonders.game.data.definitions;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.effects.BonusPerBoardElement;
import org.luxons.sevenwonders.game.effects.Discount;
import org.luxons.sevenwonders.game.effects.Effect;
import org.luxons.sevenwonders.game.effects.GoldIncrease;
import org.luxons.sevenwonders.game.effects.MilitaryReinforcements;
import org.luxons.sevenwonders.game.effects.ProductionIncrease;
import org.luxons.sevenwonders.game.effects.RawPointsIncrease;
import org.luxons.sevenwonders.game.effects.ScienceProgress;
import org.luxons.sevenwonders.game.effects.SpecialAbility;
import org.luxons.sevenwonders.game.effects.SpecialAbilityActivation;

@SuppressWarnings("unused") // the fields are injected by Gson
public class EffectsDefinition implements Definition<List<Effect>> {

    private GoldIncrease gold;

    private MilitaryReinforcements military;

    private ScienceProgress science;

    private Discount discount;

    private BonusPerBoardElement perBoardElement;

    private ProductionIncrease production;

    private RawPointsIncrease points;

    private SpecialAbility action;

    @Override
    public List<Effect> create(Settings settings) {
        List<Effect> effects = new ArrayList<>();
        if (gold != null) {
            effects.add(gold);
        }
        if (military != null) {
            effects.add(military);
        }
        if (science != null) {
            effects.add(science);
        }
        if (discount != null) {
            effects.add(discount);
        }
        if (perBoardElement != null) {
            effects.add(perBoardElement);
        }
        if (production != null) {
            effects.add(production);
        }
        if (points != null) {
            effects.add(points);
        }
        if (action != null) {
            effects.add(new SpecialAbilityActivation(action));
        }
        return effects;
    }
}
