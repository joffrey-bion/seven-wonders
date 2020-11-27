package org.luxons.sevenwonders.engine.cards

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.resources.*
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.bestPrice

data class Requirements internal constructor(
    val gold: Int = 0,
    val resources: Resources = emptyResources(),
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
        val options = transactionOptions(resources, player)
        val minPrice = options.bestPrice + gold
        if (options.isEmpty()) {
            return RequirementsSatisfaction.unavailableResources()
        }
        if (player.board.gold < minPrice) {
            return RequirementsSatisfaction.missingGoldForResources(minPrice, options)
        }
        return RequirementsSatisfaction.metWithHelp(minPrice, options)
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
