package org.luxons.sevenwonders.game.cards

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.Resources
import org.luxons.sevenwonders.game.resources.bestPrice

data class Requirements @JvmOverloads constructor(
    val gold: Int = 0,
    val resources: Resources = Resources()
) {
    /**
     * Returns whether the given [board] meets these requirements on its own.
     *
     * @param board the board to check
     *
     * @return true if the given board meets these requirements without any transaction with its neighbours
     */
    fun areMetWithoutNeighboursBy(board: Board): Boolean {
        return hasRequiredGold(board) && producesRequiredResources(board)
    }

    /**
     * Returns whether the given board meets these requirements, if the specified resources are bought from neighbours.
     *
     * @param board the board to check
     * @param boughtResources the resources the player intends to buy
     *
     * @return true if the given board meets these requirements
     */
    fun areMetWithHelpBy(board: Board, boughtResources: ResourceTransactions): Boolean {
        if (!hasRequiredGold(board, boughtResources)) {
            return false
        }
        return producesRequiredResources(board) || producesRequiredResourcesWithHelp(board, boughtResources)
    }

    /**
     * Returns whether the given player's board meets these requirements, either on its own or by buying resources to
     * neighbours.
     *
     * @param table the current game table
     * @param playerIndex the index of the player to check
     *
     * @return true if the given player's board could meet these requirements
     */
    fun areMetBy(player: Player): Boolean {
        val board = player.board
        if (!hasRequiredGold(board)) {
            return false
        }
        if (producesRequiredResources(board)) {
            return true
        }
        val bestPrice = bestPrice(resources, player)
        return bestPrice != null && bestPrice <= board.gold - gold
    }

    private fun hasRequiredGold(board: Board): Boolean {
        return board.gold >= gold
    }

    private fun hasRequiredGold(board: Board, resourceTransactions: ResourceTransactions): Boolean {
        val resourcesPrice = board.tradingRules.computeCost(resourceTransactions)
        return board.gold >= gold + resourcesPrice
    }

    private fun producesRequiredResources(board: Board): Boolean {
        return board.production.contains(resources)
    }

    private fun producesRequiredResourcesWithHelp(board: Board, transactions: ResourceTransactions): Boolean {
        val totalBoughtResources = transactions.asResources()
        val remainingResources = this.resources.minus(totalBoughtResources)
        return board.production.contains(remainingResources)
    }

    fun pay(player: Player, transactions: ResourceTransactions) {
        player.board.removeGold(gold)
        transactions.execute(player)
    }
}
