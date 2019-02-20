package org.luxons.sevenwonders.game.api

enum class Action(val message: String) {
    PLAY("Pick the card you want to play or discard."),
    PLAY_2("Pick the first card you want to play or discard. Note that you have the ability to play these 2 last cards. You will choose how to play the last one during your next turn."),
    PLAY_LAST("You have the special ability to play your last card. Choose how you want to play it."),
    PICK_NEIGHBOR_GUILD("Choose a Guild card (purple) that you want to copy from one of your neighbours."),
    WAIT("Please wait for other players to perform extra actions.")
}

data class PlayerTurnInfo internal constructor(
    val playerIndex: Int,
    val table: Table,
    val action: Action,
    val hand: List<HandCard>,
    val neighbourGuildCards: List<TableCard>
) {
    val currentAge: Int = table.currentAge
    val message: String = action.message
}
