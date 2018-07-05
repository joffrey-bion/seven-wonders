package org.luxons.sevenwonders.game.data.definitions

import org.luxons.sevenwonders.game.effects.BonusPerBoardElement
import org.luxons.sevenwonders.game.effects.Discount
import org.luxons.sevenwonders.game.effects.Effect
import org.luxons.sevenwonders.game.effects.GoldIncrease
import org.luxons.sevenwonders.game.effects.MilitaryReinforcements
import org.luxons.sevenwonders.game.effects.ProductionIncrease
import org.luxons.sevenwonders.game.effects.RawPointsIncrease
import org.luxons.sevenwonders.game.effects.ScienceProgress
import org.luxons.sevenwonders.game.effects.SpecialAbility
import org.luxons.sevenwonders.game.effects.SpecialAbilityActivation
import java.util.ArrayList

internal data class EffectsDefinition(
    private val gold: GoldIncrease? = null,
    private val military: MilitaryReinforcements? = null,
    private val science: ScienceProgress? = null,
    private val discount: Discount? = null,
    private val perBoardElement: BonusPerBoardElement? = null,
    private val production: ProductionIncrease? = null,
    private val points: RawPointsIncrease? = null,
    private val action: SpecialAbility? = null
) {
    fun create(): List<Effect> {
        val effects = ArrayList<Effect>()
        if (gold != null) {
            effects.add(gold)
        }
        if (military != null) {
            effects.add(military)
        }
        if (science != null) {
            effects.add(science)
        }
        if (discount != null) {
            effects.add(discount)
        }
        if (perBoardElement != null) {
            effects.add(perBoardElement)
        }
        if (production != null) {
            effects.add(production)
        }
        if (points != null) {
            effects.add(points)
        }
        if (action != null) {
            effects.add(SpecialAbilityActivation(action))
        }
        return effects
    }
}
