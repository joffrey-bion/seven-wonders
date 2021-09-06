package org.luxons.sevenwonders.engine.wonders

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.cards.RequirementsSatisfaction
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.PlayabilityLevel
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.resources.noTransactionOptions
import org.luxons.sevenwonders.model.wonders.WonderBuildability

internal class Wonder(
    val name: String,
    val initialResource: ResourceType,
    val stages: List<WonderStage>,
    val image: String,
) {
    val nbBuiltStages: Int
        get() = stages.count { it.isBuilt }

    private val nextStage: WonderStage
        get() {
            if (nbBuiltStages == stages.size) {
                throw IllegalStateException("This wonder has already reached its maximum level")
            }
            return stages[nbBuiltStages]
        }

    val lastBuiltStage: WonderStage?
        get() = stages.getOrNull(nbBuiltStages - 1)

    fun computeBuildabilityBy(player: Player): WonderBuildability {
        if (nbBuiltStages == stages.size) {
            return Buildability.wonderFullyBuilt()
        }
        return Buildability.requirementDependent(nextStage.requirements.assess(player))
    }

    fun isNextStageBuildable(board: Board, boughtResources: ResourceTransactions): Boolean =
        nbBuiltStages < stages.size && nextStage.isBuildable(board, boughtResources)

    fun placeCard(cardBack: CardBack) = nextStage.placeCard(cardBack)

    fun activateLastBuiltStage(player: Player, boughtResources: ResourceTransactions) =
        lastBuiltStage!!.activate(player, boughtResources)

    fun computePoints(player: Player): Int =
        stages.filter { it.isBuilt }.flatMap { it.effects }.sumOf { it.computePoints(player) }
}

private object Buildability {

    fun wonderFullyBuilt() = WonderBuildability(
        isBuildable = false,
        minPrice = Int.MAX_VALUE,
        transactionsOptions = noTransactionOptions(),
        playabilityLevel = PlayabilityLevel.WONDER_FULLY_BUILT,
    )

    fun requirementDependent(satisfaction: RequirementsSatisfaction) = WonderBuildability(
        isBuildable = satisfaction.satisfied,
        minPrice = satisfaction.minPrice,
        transactionsOptions = satisfaction.transactionOptions,
        playabilityLevel = satisfaction.level,
    )
}
