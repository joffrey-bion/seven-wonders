package org.luxons.sevenwonders.game.wonders

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.ResourceType

internal class Wonder(
    val name: String,
    val initialResource: ResourceType,
    val stages: List<WonderStage>,
    val image: String
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

    fun isNextStageBuildable(board: Board, boughtResources: ResourceTransactions): Boolean =
        nbBuiltStages < stages.size && nextStage.isBuildable(board, boughtResources)

    fun placeCard(cardBack: CardBack) = nextStage.placeCard(cardBack)

    fun activateLastBuiltStage(player: Player, boughtResources: ResourceTransactions) =
        lastBuiltStage!!.activate(player, boughtResources)

    fun computePoints(player: Player): Int =
        stages.filter { it.isBuilt }.flatMap { it.effects }.sumBy { it.computePoints(player) }
}
