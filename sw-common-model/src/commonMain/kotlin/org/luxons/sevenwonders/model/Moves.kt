package org.luxons.sevenwonders.model

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.boards.Board
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.model.score.ScoreBoard
import org.luxons.sevenwonders.model.wonders.WonderBuildability

enum class Action(val message: String) {
    SAY_READY("Click 'READY' when you are ready to receive your next hand."),
    PLAY("Pick the card you want to play or discard."),
    PLAY_2("Pick the first card you want to play or discard. Note that you have the ability to play these 2 last cards. You will choose how to play the last one during your next turn."),
    PLAY_LAST("You have the special ability to play your last card. Choose how you want to play it."),
    PLAY_FREE_DISCARDED("Pick a card from the discarded deck, you can play it for free (but you cannot discard for 3 gold coins or upgrade your wonder with it)."),
    PICK_NEIGHBOR_GUILD("Choose a Guild card (purple) that you want to copy from one of your neighbours."),
    WAIT("Please wait for other players to perform extra actions."),
    WATCH_SCORE("The game is over! Look at the scoreboard to see the final ranking!");

    fun allowsBuildingWonder(): Boolean = when (this) {
        PLAY, PLAY_2, PLAY_LAST -> true
        else -> false
    }

    fun allowsDiscarding(): Boolean = when (this) {
        PLAY, PLAY_2, PLAY_LAST -> true
        else -> false
    }
}

@Serializable
data class PlayerTurnInfo(
    val playerIndex: Int,
    val table: TableState,
    val action: Action,
    val hand: List<HandCard>?,
    val preparedMove: PlayedMove?,
    val neighbourGuildCards: List<HandCard>,
    val discardedCards: List<HandCard>?, // only present when the player can actually see them
    val scoreBoard: ScoreBoard? = null,
) {
    val currentAge: Int = table.currentAge
    val message: String = action.message
    val wonderBuildability: WonderBuildability = table.boards[playerIndex].wonder.buildability

    val RelativeBoardPosition.index: Int
        get() = getIndexFrom(playerIndex, table.boards.size)
}

fun PlayerTurnInfo.getOwnBoard(): Board = table.boards[playerIndex]

fun PlayerTurnInfo.getBoard(position: RelativeBoardPosition): Board = table.boards[position.index]

fun PlayerTurnInfo.getNonNeighbourBoards(): List<Board> {
    val nPlayers = table.boards.size
    val first = (playerIndex + 2) % nPlayers
    val last = (playerIndex - 2 + nPlayers) % nPlayers
    val range = if (first < last) first..last else ((first until nPlayers) + (0..last))
    return range.map { table.boards[it] }
}

// TODO move to server code
fun Collection<PlayerTurnInfo>.hideHandsAndWaitForReadiness() = map { it.copy(action = Action.SAY_READY, hand = null) }

@Serializable
data class PlayedMove(
    val playerIndex: Int,
    val type: MoveType,
    val card: TableCard,
    val transactions: ResourceTransactions,
)

@Serializable
data class PlayerMove(
    val type: MoveType,
    val cardName: String,
    val transactions: ResourceTransactions = noTransactions(),
)

enum class MoveType {
    PLAY,
    PLAY_FREE,
    PLAY_FREE_DISCARDED,
    UPGRADE_WONDER,
    DISCARD,
    COPY_GUILD,
}
