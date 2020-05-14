package org.luxons.sevenwonders.model

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.model.score.ScoreBoard
import org.luxons.sevenwonders.model.wonders.WonderBuildability

enum class Action(val message: String) {
    SAY_READY("Say when you're ready to get your next hand"),
    PLAY("Pick the card you want to play or discard."),
    PLAY_2("Pick the first card you want to play or discard. Note that you have the ability to play these 2 last cards. You will choose how to play the last one during your next turn."),
    PLAY_LAST("You have the special ability to play your last card. Choose how you want to play it."),
    PICK_NEIGHBOR_GUILD("Choose a Guild card (purple) that you want to copy from one of your neighbours."),
    WAIT("Please wait for other players to perform extra actions."),
    WATCH_SCORE("The game is over! Look at the scoreboard to see the final ranking!")
}

@Serializable
data class PlayerTurnInfo(
    val playerIndex: Int,
    val table: TableState,
    val action: Action,
    val hand: List<HandCard>?,
    val preparedMove: PlayedMove?,
    val neighbourGuildCards: List<TableCard>,
    val scoreBoard: ScoreBoard? = null
) {
    val currentAge: Int = table.currentAge
    val message: String = action.message
    val wonderBuildability: WonderBuildability = table.boards[playerIndex].wonder.buildability
}

// TODO move to server code
fun Collection<PlayerTurnInfo>.hideHandsAndWaitForReadiness() =
        map { it.copy(action = Action.SAY_READY, hand = null) }

@Serializable
data class PlayedMove(
    val playerIndex: Int,
    val type: MoveType,
    val card: TableCard,
    val transactions: ResourceTransactions
)

@Serializable
data class PlayerMove(
    val type: MoveType,
    val cardName: String,
    val transactions: ResourceTransactions = noTransactions()
)

enum class MoveType {
    PLAY,
    PLAY_FREE,
    UPGRADE_WONDER,
    DISCARD,
    COPY_GUILD;
}
