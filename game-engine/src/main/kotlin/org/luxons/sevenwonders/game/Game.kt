package org.luxons.sevenwonders.game

import org.luxons.sevenwonders.game.api.Action
import org.luxons.sevenwonders.game.api.HandCard
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.PlayerTurnInfo
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Decks
import org.luxons.sevenwonders.game.cards.Hands
import org.luxons.sevenwonders.game.data.LAST_AGE
import org.luxons.sevenwonders.game.effects.SpecialAbility
import org.luxons.sevenwonders.game.moves.InvalidMoveException
import org.luxons.sevenwonders.game.moves.Move
import org.luxons.sevenwonders.game.scoring.ScoreBoard

class Game(
    val id: Long,
    val settings: Settings,
    boards: List<Board>,
    private val decks: Decks
) {
    private val nbPlayers: Int = boards.size
    private val table: Table = Table(boards)
    private val discardedCards: MutableList<Card> = ArrayList()
    private val preparedMoves: MutableMap<Int, Move> = HashMap()
    private var currentTurnInfo: List<PlayerTurnInfo> = emptyList()
    private var hands: Hands = Hands(emptyList())

    init {
        startNewAge()
    }

    private fun startNewAge() {
        table.increaseCurrentAge()
        hands = decks.deal(table.currentAge, table.nbPlayers)
        startNewTurn()
    }

    private fun startNewTurn() {
        currentTurnInfo = IntRange(0, nbPlayers - 1).map { createPlayerTurnInfo(it) }
    }

    private fun createPlayerTurnInfo(playerIndex: Int): PlayerTurnInfo {
        val hand = hands.createHand(table, playerIndex)
        val action = determineAction(hand, table.getBoard(playerIndex))

        var neighbourGuildCards = emptyList<Card>()
        if (action === Action.PICK_NEIGHBOR_GUILD) {
            neighbourGuildCards = table.getNeighbourGuildCards(playerIndex)
        }

        return PlayerTurnInfo(playerIndex, table, action, hand, neighbourGuildCards)
    }

    fun getCurrentTurnInfo(): Collection<PlayerTurnInfo> {
        return currentTurnInfo
    }

    private fun determineAction(hand: List<HandCard>, board: Board): Action {
        return if (endOfGameReached() && board.hasSpecial(SpecialAbility.COPY_GUILD)) {
            determineCopyGuildAction(board)
        } else if (hand.size == 1 && board.hasSpecial(SpecialAbility.PLAY_LAST_CARD)) {
            Action.PLAY_LAST
        } else if (hand.size == 2 && board.hasSpecial(SpecialAbility.PLAY_LAST_CARD)) {
            Action.PLAY_2
        } else if (hand.isEmpty()) {
            Action.WAIT
        } else {
            Action.PLAY
        }
    }

    private fun determineCopyGuildAction(board: Board): Action {
        val neighbourGuildCards = table.getNeighbourGuildCards(board.playerIndex)
        return if (neighbourGuildCards.isEmpty()) Action.WAIT else Action.PICK_NEIGHBOR_GUILD
    }

    @Throws(InvalidMoveException::class)
    fun prepareMove(playerIndex: Int, playerMove: PlayerMove): CardBack {
        val card = decks.getCard(table.currentAge, playerMove.cardName)
        val move = playerMove.type.resolve(playerIndex, card, playerMove)
        validate(move)
        preparedMoves[playerIndex] = move
        return card.back
    }

    @Throws(InvalidMoveException::class)
    private fun validate(move: Move) {
        move.validate(table, hands[move.playerIndex])
    }

    fun allPlayersPreparedTheirMove(): Boolean {
        val nbExpectedMoves = currentTurnInfo.filter { it.action !== Action.WAIT }.count()
        return preparedMoves.size == nbExpectedMoves
    }

    fun playTurn(): Table {
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
        return table
    }

    private fun makeMoves() {
        val playedMoves = mapToList(preparedMoves)

        // all cards from this turn need to be placed before executing any effect
        // because effects depending on played cards need to take the ones from the current turn into account too
        placePreparedCards(playedMoves)

        // same goes for the discarded cards during the last turn, which should be available for special actions
        if (hands.maxOneCardRemains()) {
            discardLastCardsOfHands()
        }

        activatePlayedCards(playedMoves)

        table.lastPlayedMoves = playedMoves
        preparedMoves.clear()
    }

    private fun mapToList(movesPerPlayer: Map<Int, Move>): List<Move> =
        IntRange(0, nbPlayers - 1).map { p -> movesPerPlayer[p] ?: throw MissingPreparedMoveException(p) }

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
            move.place(table, discardedCards, settings)
            hands = hands.remove(move.playerIndex, move.card)
        }
    }

    private fun discardLastCardsOfHands() {
        for (i in 0 until nbPlayers) {
            val board = table.getBoard(i)
            if (!board.hasSpecial(SpecialAbility.PLAY_LAST_CARD)) {
                discardHand(i)
            }
        }
    }

    private fun discardHand(playerIndex: Int) {
        val hand = hands[playerIndex]
        discardedCards.addAll(hand)
        hands = hands.discardHand(playerIndex)
    }

    private fun activatePlayedCards(playedMoves: List<Move>) {
        playedMoves.forEach { move -> move.activate(table, discardedCards, settings) }
    }

    fun computeScore(): ScoreBoard = ScoreBoard(table.boards.map { b -> b.computePoints(table) })

    private class MissingPreparedMoveException internal constructor(playerIndex: Int) :
        IllegalStateException("Player $playerIndex has not prepared his move")
}
