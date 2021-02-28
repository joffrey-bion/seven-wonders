package org.luxons.sevenwonders.engine

import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.engine.cards.Decks
import org.luxons.sevenwonders.engine.cards.Hands
import org.luxons.sevenwonders.engine.converters.toHandCards
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
    private val decks: Decks,
) {
    private val table: Table = Table(boards)
    private val players: List<Player> = boards.map { SimplePlayer(it.playerIndex, table) }
    private val discardedCards: MutableList<Card> = mutableListOf()
    private val preparedMoves: MutableMap<Int, Move> = mutableMapOf()
    private var currentTurnInfo: List<PlayerTurnInfo> = emptyList()
    private var hands: Hands = Hands(emptyList())
    private var militaryConflictsResolved = false

    init {
        startNewAge()
    }

    private fun startNewAge() {
        militaryConflictsResolved = false
        table.increaseCurrentAge()
        hands = decks.deal(table.currentAge, players.size)
        startNewTurn()
    }

    private fun startNewTurn() {
        currentTurnInfo = players.map { player ->
            val hand = hands.createHand(player)
            val action = if (hand.isEmpty()) {
                TurnAction.Wait(message = ActionMessages.WAIT_OTHER_PLAY_LAST)
            } else {
                TurnAction.PlayFromHand(message = actionMessage(hand, player), hand = hand)
            }
            PlayerTurnInfo(playerIndex = player.index, table = table.toTableState(), action = action)
        }
    }

    private fun actionMessage(hand: List<HandCard>, player: Player) = when {
        hand.size == 1 && player.board.hasSpecial(SpecialAbility.PLAY_LAST_CARD) -> ActionMessages.PLAY_LAST_SPECIAL
        hand.size == 2 && player.board.hasSpecial(SpecialAbility.PLAY_LAST_CARD) -> ActionMessages.PLAY_2
        hand.size == 2 -> ActionMessages.PLAY_LAST
        else -> ActionMessages.PLAY
    }

    private fun startPlayDiscardedTurn() {
        val newTableState = table.toTableState()
        currentTurnInfo = players.map { player ->
            val action = when {
                player.board.hasSpecial(SpecialAbility.PLAY_DISCARDED) -> TurnAction.PlayFromDiscarded(
                    discardedCards = discardedCards.toHandCards(player, true),
                )
                else -> TurnAction.Wait(message = ActionMessages.WAIT_OTHER_PLAY_DISCARD)
            }
            PlayerTurnInfo(playerIndex = player.index, table = newTableState, action = action)
        }
    }

    private fun startEndGameActionsTurn() {
        // some player may need to do additional stuff
        currentTurnInfo = players.map { player ->
            PlayerTurnInfo(
                playerIndex = player.index,
                table = table.toTableState(),
                action = computeEndGameAction(player),
            )
        }
        val someSpecialActions = currentTurnInfo.any { it.action !is TurnAction.Wait }
        if (someSpecialActions) {
            return // we play the last turn like a normal one
        }
        val scoreBoard = computeScore()
        currentTurnInfo = currentTurnInfo.map {
            it.copy(action = TurnAction.WatchScore(message = ActionMessages.WATCH_SCORE, scoreBoard = scoreBoard))
        }
    }

    private fun computeEndGameAction(player: Player): TurnAction {
        val guilds = table.getNeighbourGuildCards(player.index).toHandCards(player, true)
        return when {
            player.canCopyGuild() && guilds.isNotEmpty() -> TurnAction.PickNeighbourGuild(guilds)
            else -> TurnAction.Wait(ActionMessages.WAIT_OTHER_PICK_GUILD)
        }
    }

    private fun Player.canCopyGuild() = board.hasSpecial(SpecialAbility.COPY_GUILD) && board.copiedGuild == null

    /**
     * Returns information for each player about the current turn.
     */
    fun getCurrentTurnInfo(): List<PlayerTurnInfo> = currentTurnInfo

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
        val nbExpectedMoves = currentTurnInfo.count { it.action !is TurnAction.Wait }
        return preparedMoves.size == nbExpectedMoves
    }

    /**
     * Plays all the [prepared moves][prepareMove] for the current turn.
     *
     * An exception will be thrown if some players had not prepared their moves (unless these players had nothing to
     * do). To avoid this, please check if everyone is ready using [allPlayersPreparedTheirMove].
     */
    fun playTurn() {
        makeMoves()

        if (hands.maxOneCardRemains()) {
            discardLastCardsIfRelevant()
        }

        if (shouldStartPlayDiscardedTurn()) {
            startPlayDiscardedTurn()
        } else if (endOfAgeReached()) {
            resolveMilitaryConflicts()
            if (endOfGameReached()) {
                startEndGameActionsTurn()
            } else {
                startNewAge()
            }
        } else {
            rotateHandsIfRelevant()
            startNewTurn()
        }
    }

    private fun makeMoves() {
        val moves = getMovesToPerform()

        // all cards from this turn need to be placed before executing any effect
        // because effects depending on played cards need to take the ones from the current turn into account too
        placePreparedCards(moves)
        activatePlayedCards(moves)

        table.lastPlayedMoves = moves
        preparedMoves.clear()
    }

    private fun getMovesToPerform(): List<Move> =
        currentTurnInfo.filter { it.action !is TurnAction.Wait }.map { getMoveToPerformFor(it.playerIndex) }

    private fun getMoveToPerformFor(playerIndex: Int) =
        preparedMoves[playerIndex] ?: throw MissingPreparedMoveException(playerIndex)

    private fun endOfAgeReached(): Boolean = hands.areEmpty

    private fun resolveMilitaryConflicts() {
        // this is necessary because this method is actually called twice in the 3rd age if someone has CPY_GUILD
        // TODO we should instead manage the game's state machine in a better way to avoid stuff like this
        if (!militaryConflictsResolved) {
            table.resolveMilitaryConflicts()
            militaryConflictsResolved = true
        }
    }

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
            move.place(discardedCards)
            hands = hands.remove(move.playerContext.index, move.card)
        }
    }

    private fun discardLastCardsIfRelevant() =
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
