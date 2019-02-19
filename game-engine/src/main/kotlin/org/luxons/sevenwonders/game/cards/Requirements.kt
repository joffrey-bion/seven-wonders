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
     * Returns whether the given [board] meets these requirements on its own.
     *
     * @param board the board to check
     *
     * @return true if the given board meets these requirements without any transaction with its neighbours
     */
    internal fun areMetWithoutNeighboursBy(board: Board): Boolean =
        hasRequiredGold(board) && producesRequiredResources(board)

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

    /**
     * Returns whether the given player meets these requirements, either on its own or by buying resources to
     * neighbours.
     *
     * @param player the player to check
     *
     * @return true if the given player's board could meet these requirements
     */
    internal fun areMetBy(player: Player): Boolean {
        val board = player.board
        if (!hasRequiredGold(board)) {
            return false
        }
        if (producesRequiredResources(board)) {
            return true
        }
        val solution = bestSolution(resources, player)
        return !solution.possibleTransactions.isEmpty() && solution.price <= board.gold - gold
    }

    private fun hasRequiredGold(board: Board): Boolean = board.gold >= gold

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
