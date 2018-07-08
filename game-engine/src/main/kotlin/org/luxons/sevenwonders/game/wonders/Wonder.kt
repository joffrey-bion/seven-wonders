package org.luxons.sevenwonders.game.wonders

import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.ResourceType

class Wonder(
    val name: String,
    val initialResource: ResourceType,
    val stages: List<WonderStage>,
    val image: String
) {
    val nbBuiltStages: Int
        get() = stages.filter { it.isBuilt }.count()

    private val nextStage: WonderStage
        get() {
            val nextLevel = nbBuiltStages
            if (nextLevel == stages.size) {
                throw IllegalStateException("This wonder has already reached its maximum level")
            }
            return stages[nextLevel]
        }

    private val lastBuiltStage: WonderStage
        get() {
            val lastLevel = nbBuiltStages - 1
            return stages[lastLevel]
        }

    fun isNextStageBuildable(table: Table, playerIndex: Int, boughtResources: ResourceTransactions): Boolean =
        nbBuiltStages < stages.size && nextStage.isBuildable(table, playerIndex, boughtResources)

    fun buildLevel(cardBack: CardBack) = nextStage.build(cardBack)

    fun activateLastBuiltStage(table: Table, playerIndex: Int, boughtResources: ResourceTransactions) =
        lastBuiltStage.activate(table, playerIndex, boughtResources)

    fun computePoints(table: Table, playerIndex: Int): Int = stages
        .filter { it.isBuilt }
        .flatMap { c -> c.effects }
        .map { e -> e.computePoints(table, playerIndex) }
        .sum()
}
