package org.luxons.sevenwonders.engine.data.definitions

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.engine.effects.BonusPerBoardElement
import org.luxons.sevenwonders.engine.effects.Discount
import org.luxons.sevenwonders.engine.effects.Effect
import org.luxons.sevenwonders.engine.effects.GoldIncrease
import org.luxons.sevenwonders.engine.effects.MilitaryReinforcements
import org.luxons.sevenwonders.engine.effects.ProductionIncrease
import org.luxons.sevenwonders.engine.effects.RawPointsIncrease
import org.luxons.sevenwonders.engine.effects.ScienceProgress
import org.luxons.sevenwonders.engine.effects.SpecialAbility
import org.luxons.sevenwonders.engine.effects.SpecialAbilityActivation

@Serializable
internal class EffectsDefinition(
    private val gold: Int? = null,
    private val military: Int? = null,
    private val science: ScienceProgress? = null,
    private val discount: Discount? = null,
    private val perBoardElement: BonusPerBoardElement? = null,
    private val production: ProductionIncrease? = null,
    private val points: Int? = null,
    private val action: SpecialAbility? = null,
) {
    fun create(): List<Effect> = mutableListOf<Effect>().apply {
        gold?.let { add(GoldIncrease(it)) }
        military?.let { add(MilitaryReinforcements(it)) }
        science?.let { add(it) }
        discount?.let { add(it) }
        perBoardElement?.let { add(it) }
        production?.let { add(it) }
        points?.let { add(RawPointsIncrease(it)) }
        action?.let { add(SpecialAbilityActivation(it)) }
    }
}
