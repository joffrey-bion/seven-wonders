package org.luxons.sevenwonders.game

import org.luxons.sevenwonders.game.api.Action
import org.luxons.sevenwonders.game.api.HandCard
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.PlayerTurnInfo
import org.luxons.sevenwonders.game.api.toApiTable
import org.luxons.sevenwonders.game.api.toTableCard
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.boards.Table
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Decks
import org.luxons.sevenwonders.game.cards.Hands
import org.luxons.sevenwonders.game.data.LAST_AGE
import org.luxons.sevenwonders.game.effects.SpecialAbility
import org.luxons.sevenwonders.game.moves.Move
import org.luxons.sevenwonders.game.score.ScoreBoard
import org.luxons.sevenwonders.game.api.Table as ApiTable

class Game internal constructor(
    val id: Long,
    private val settings: Settings,
    boards: List<Board>,
    private val decks: Decks
) {
    private val table: Table = Table(boards)
    private val players: List<Player> = boards.map { SimplePlayer(it.playerIndex, table) }
    private val discardedCards: MutableList<Card> = mutableListOf()
    private val preparedMoves: MutableMap<Int, Move> = mutableMapOf()
    private var currentTurnInfo: List<PlayerTurnInfo> = emptyList()
    private var hands: Hands = Hands(emptyList())

    init {
        startNewAge()
    }

    private fun startNewAge() {
        table.increaseCurrentAge()
        hands = decks.deal(table.currentAge, players.size)
        startNewTurn()
    }

    private fun startNewTurn() {
        currentTurnInfo = players.map { createPlayerTurnInfo(it) }
    }

    private fun createPlayerTurnInfo(player: Player): PlayerTurnInfo {
        val hand = hands.createHand(player)
        val action = determineAction(hand, player.board)
        val neighbourGuildCards = table.getNeighbourGuildCards(player.index).map { it.toTableCard(null) }

        return PlayerTurnInfo(player.index, table.toApiTable(), action, hand, neighbourGuildCards)
    }

    /**
     * Returns information for each player about the current turn.
     */
    fun getCurrentTurnInfo(): Collection<PlayerTurnInfo> = currentTurnInfo

    private fun determineAction(hand: List<HandCard>, board: Board): Action = when {
        endOfGameReached() && board.hasSpecial(SpecialAbility.COPY_GUILD) -> determineCopyGuildAction(board)
        hand.size == 1 && board.hasSpecial(SpecialAbility.PLAY_LAST_CARD) -> Action.PLAY_LAST
        hand.size == 2 && board.hasSpecial(SpecialAbility.PLAY_LAST_CARD) -> Action.PLAY_2
        hand.isEmpty() -> Action.WAIT
        else -> Action.PLAY
    }

    private fun determineCopyGuildAction(board: Board): Action {
        val neighbourGuildCards = table.getNeighbourGuildCards(board.playerIndex)
        return if (neighbourGuildCards.isEmpty()) Action.WAIT else Action.PICK_NEIGHBOR_GUILD
    }

    /**
     * Prepares the given [move] for the player at the given [playerIndex].
     *
     * @return the back of the card that is prepared on the table
     */
    fun prepareMove(playerIndex: Int, move: PlayerMove): CardBack {
        val card = decks.getCard(table.currentAge, move.cardName)
        val context = PlayerContext(playerIndex, table, hands[playerIndex])
        val resolvedMove = move.type.resolve(move, card, context)
        preparedMoves[playerIndex] = resolvedMove
        return card.back
    }

    /**
     * Returns true if all players that had to do something have [prepared their move][prepareMove]. This means we are
     * ready to [play the current turn][playTurn].
     */
    fun allPlayersPreparedTheirMove(): Boolean {
        val nbExpectedMoves = currentTurnInfo.count { it.action !== Action.WAIT }
        return preparedMoves.size == nbExpectedMoves
    }

    /**
     * Plays all the [prepared moves][prepareMove] for the current turn. An exception will be thrown if some players
     * had not prepared their moves (unless these players had nothing to do). To avoid this, please check if everyone
     * is ready using [allPlayersPreparedTheirMove].
     */
    fun playTurn(): ApiTable {
        makeMoves()
        if (endOfAgeReached()) {
            executeEndOfAgeEvents()
            if (!endOfGameReached()) {
                startNewAge()
            }
        } else {
            rotateHandsIfRelevant()
            startNewTurn()
        }
        return table.toApiTable()
    }

    private fun makeMoves() {
        val moves = getMovesToPerform()

        // all cards from this turn need to be placed before executing any effect
        // because effects depending on played cards need to take the ones from the current turn into account too
        placePreparedCards(moves)

        // same goes for the discarded cards during the last turn, which should be available for special actions
        if (hands.maxOneCardRemains()) {
            discardLastCardsOfHands()
        }

        activatePlayedCards(moves)

        table.lastPlayedMoves = moves
        preparedMoves.clear()
    }

    private fun getMovesToPerform(): List<Move> =
        currentTurnInfo.filter { it.action !== Action.WAIT }.map { getMoveToPerformFor(it.playerIndex) }

    private fun getMoveToPerformFor(playerIndex: Int) =
        preparedMoves[playerIndex] ?: throw MissingPreparedMoveException(playerIndex)

    private fun endOfAgeReached(): Boolean = hands.isEmpty

    private fun executeEndOfAgeEvents() = table.resolveMilitaryConflicts()

    private fun endOfGameReached(): Boolean = endOfAgeReached() && table.currentAge == LAST_AGE

    private fun rotateHandsIfRelevant() {
        // we don't rotate hands if some player can play his last card (with the special ability)
        if (!hands.maxOneCardRemains()) {
            hands = hands.rotate(table.handRotationDirection)
        }
    }

    private fun placePreparedCards(playedMoves: List<Move>) {
        playedMoves.forEach { move ->
            move.place(discardedCards, settings)
            hands = hands.remove(move.playerContext.index, move.card)
        }
    }

    private fun discardLastCardsOfHands() =
        table.boards.filterNot { it.hasSpecial(SpecialAbility.PLAY_LAST_CARD) }.forEach { discardHand(it.playerIndex) }

    private fun discardHand(playerIndex: Int) {
        val hand = hands[playerIndex]
        discardedCards.addAll(hand)
        hands = hands.discardHand(playerIndex)
    }

    private fun activatePlayedCards(playedMoves: List<Move>) =
        playedMoves.forEach { it.activate(discardedCards, settings) }

    /**
     * Computes the score for all players.
     */
    fun computeScore(): ScoreBoard = ScoreBoard(table.boards.map { it.computeScore(players[it.playerIndex]) })

    private class MissingPreparedMoveException internal constructor(playerIndex: Int) :
        IllegalStateException("Player $playerIndex has not prepared his move")
}
