package org.luxons.sevenwonders.game.cards

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.Resources
import org.luxons.sevenwonders.game.resources.asResources
import org.luxons.sevenwonders.game.resources.bestSolution
import org.luxons.sevenwonders.game.resources.emptyResources
import org.luxons.sevenwonders.game.resources.execute

data class Requirements internal constructor(
    val gold: Int = 0,
    val resources: Resources = emptyResources()
) {
    /**
     * Returns information about the extent to which the given [player] meets these requirements, either on its own or
     * by buying resources to neighbours.
     */
    internal fun assess(player: Player): RequirementsSatisfaction {
        if (player.board.gold < gold) {
            return RequirementsSatisfaction.missingRequiredGold(gold)
        }
        if (resources.isEmpty()) {
            if (gold > 0) {
                return RequirementsSatisfaction.enoughGold(gold)
            }
            return RequirementsSatisfaction.noRequirements()
        }
        if (producesRequiredResources(player.board)) {
            if (gold > 0) {
                return RequirementsSatisfaction.enoughResourcesAndGold(gold)
            }
            return RequirementsSatisfaction.enoughResources()
        }
        return satisfactionWithPotentialHelp(player)
    }

    private fun satisfactionWithPotentialHelp(player: Player): RequirementsSatisfaction {
        val (minPriceForResources, possibleTransactions) = bestSolution(resources, player)
        val minPrice = minPriceForResources + gold
        if (possibleTransactions.isEmpty()) {
            return RequirementsSatisfaction.unavailableResources()
        }
        if (player.board.gold < minPrice) {
            return RequirementsSatisfaction.missingGoldForResources(minPrice, possibleTransactions)
        }
        return RequirementsSatisfaction.metWithHelp(minPrice, possibleTransactions)
    }

    /**
     * Returns whether the given board meets these requirements, if the specified resources are bought from neighbours.
     *
     * @param board the board to check
     * @param boughtResources the resources the player intends to buy
     *
     * @return true if the given board meets these requirements
     */
    internal fun areMetWithHelpBy(board: Board, boughtResources: ResourceTransactions): Boolean {
        if (!hasRequiredGold(board, boughtResources)) {
            return false
        }
        return producesRequiredResources(board) || producesRequiredResourcesWithHelp(board, boughtResources)
    }

    private fun hasRequiredGold(board: Board, resourceTransactions: ResourceTransactions): Boolean {
        val resourcesPrice = board.tradingRules.computeCost(resourceTransactions)
        return board.gold >= gold + resourcesPrice
    }

    private fun producesRequiredResources(board: Board): Boolean = board.production.contains(resources)

    private fun producesRequiredResourcesWithHelp(board: Board, transactions: ResourceTransactions): Boolean {
        val totalBoughtResources = transactions.asResources()
        val remainingResources = resources - totalBoughtResources
        return board.production.contains(remainingResources)
    }

    internal fun pay(player: Player, transactions: ResourceTransactions) {
        player.board.removeGold(gold)
        transactions.execute(player)
    }
}
