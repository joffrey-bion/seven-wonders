package org.luxons.sevenwonders.model

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.model.wonders.WonderBuildability

enum class Action(val message: String) {
    PLAY("Pick the card you want to play or discard."),
    PLAY_2("Pick the first card you want to play or discard. Note that you have the ability to play these 2 last cards. You will choose how to play the last one during your next turn."),
    PLAY_LAST("You have the special ability to play your last card. Choose how you want to play it."),
    PICK_NEIGHBOR_GUILD("Choose a Guild card (purple) that you want to copy from one of your neighbours."),
    WAIT("Please wait for other players to perform extra actions.")
}

@Serializable
data class PlayerTurnInfo(
    val playerIndex: Int,
    val table: GameState,
    val action: Action,
    val hand: List<HandCard>,
    val preparedMove: PlayedMove?,
    val neighbourGuildCards: List<TableCard>
) {
    val currentAge: Int = table.currentAge
    val message: String = action.message
    val wonderBuildability: WonderBuildability = table.boards[playerIndex].wonder.buildability
}

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
