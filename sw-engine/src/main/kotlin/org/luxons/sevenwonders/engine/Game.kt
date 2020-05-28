package org.luxons.sevenwonders.engine

import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.engine.cards.Decks
import org.luxons.sevenwonders.engine.cards.Hands
import org.luxons.sevenwonders.engine.converters.toHandCard
import org.luxons.sevenwonders.engine.converters.toPlayedMove
import org.luxons.sevenwonders.engine.converters.toTableState
import org.luxons.sevenwonders.engine.data.LAST_AGE
import org.luxons.sevenwonders.engine.effects.SpecialAbility
import org.luxons.sevenwonders.engine.moves.Move
import org.luxons.sevenwonders.engine.moves.resolve
import org.luxons.sevenwonders.model.*
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.score.ScoreBoard

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
        currentTurnInfo = players.map {
            val hand = hands.createHand(it)
            val action = determineAction(hand, it.board)
            createPlayerTurnInfo(it, action, hand)
        }
    }

    private fun startPlayDiscardedTurn() {
        currentTurnInfo = players.map {
            val action = if (it.board.hasSpecial(SpecialAbility.PLAY_DISCARDED)) {
                Action.PLAY_FREE_DISCARDED
            } else {
                Action.WAIT
            }
            createPlayerTurnInfo(it, action, null)
        }
    }

    private fun startEndGameTurn() {
        // some player may need to do additional stuff
        startNewTurn()
        val allDone = currentTurnInfo.all { it.action == Action.WAIT }
        if (!allDone) {
            return // we play the last turn like a normal one
        }
        val scoreBoard = computeScore()
        currentTurnInfo = currentTurnInfo.map {
            it.copy(action = Action.WATCH_SCORE, scoreBoard = scoreBoard)
        }
    }

    private fun createPlayerTurnInfo(player: Player, action: Action, hand: List<HandCard>?): PlayerTurnInfo {
        val neighbourGuildCards = table.getNeighbourGuildCards(player.index).map { it.toHandCard(player, true) }
        val exposedDiscardedCards = if (action == Action.PLAY_FREE_DISCARDED) {
            discardedCards.map { it.toHandCard(player, true) }
        } else {
            null
        }
        return PlayerTurnInfo(
            playerIndex = player.index,
            table = table.toTableState(),
            action = action,
            hand = hand,
            preparedMove = preparedMoves[player.index]?.toPlayedMove(),
            discardedCards = exposedDiscardedCards,
            neighbourGuildCards = neighbourGuildCards
        )
    }

    /**
     * Returns information for each player about the current turn.
     */
    fun getCurrentTurnInfo(): Collection<PlayerTurnInfo> = currentTurnInfo

    private fun determineAction(hand: List<HandCard>, board: Board): Action = when {
        endOfGameReached() -> when {
            board.hasSpecial(SpecialAbility.COPY_GUILD) && board.copiedGuild == null -> determineCopyGuildAction(board)
            else -> Action.WAIT
        }
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
        val card = move.findCard()
        val context = PlayerContext(playerIndex, table, hands[playerIndex])
        val resolvedMove = move.type.resolve(move, card, context, discardedCards)
        preparedMoves[playerIndex] = resolvedMove
        return card.back
    }

    private fun PlayerMove.findCard() = when (type) {
        MoveType.PLAY_FREE_DISCARDED -> discardedCards.first { it.name == cardName }
        else -> decks.getCard(table.currentAge, cardName)
    }

    fun unprepareMove(playerIndex: Int) {
        preparedMoves.remove(playerIndex)
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
    fun playTurn(): TableState {
        makeMoves()
        if (endOfAgeReached()) {
            executeEndOfAgeEvents()
            if (endOfGameReached()) {
                startEndGameTurn()
            } else {
                startNewAge()
            }
        } else {
            if (shouldStartPlayDiscardedTurn()) {
                startPlayDiscardedTurn()
            } else {
                rotateHandsIfRelevant()
                startNewTurn()
            }
        }
        return table.toTableState()
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

    fun endOfGameReached(): Boolean = endOfAgeReached() && table.currentAge == LAST_AGE

    private fun shouldStartPlayDiscardedTurn(): Boolean {
        val boardsWithPlayDiscardedAbility = table.boards.filter { it.hasSpecial(SpecialAbility.PLAY_DISCARDED) }
        if (boardsWithPlayDiscardedAbility.isEmpty()) {
            return false
        }
        if (discardedCards.isEmpty()) {
            // it was wasted for this turn, no discarded card to play
            boardsWithPlayDiscardedAbility.forEach { it.removeSpecial(SpecialAbility.PLAY_DISCARDED) }
            return false
        }
        return true
    }

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
        hands = hands.clearHand(playerIndex)
    }

    private fun activatePlayedCards(playedMoves: List<Move>) =
        playedMoves.forEach { it.activate(discardedCards, settings) }

    /**
     * Computes the score for all players.
     */
    private fun computeScore(): ScoreBoard =
        ScoreBoard(table.boards.map { it.computeScore(players[it.playerIndex]) }.sortedDescending())

    private class MissingPreparedMoveException(playerIndex: Int) :
        IllegalStateException("Player $playerIndex has not prepared his move")
}
