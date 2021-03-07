package org.luxons.sevenwonders.model

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.score.ScoreBoard

object ActionMessages {
    const val SAY_READY = "Click 'READY' when you are ready to receive your next hand."
    const val PLAY = "Pick the card you want to play or discard. The rest of the cards will be passed on to the next player."
    const val PLAY_LAST = "Pick the card you want to play or discard. The remaining card will be discarded."
    const val PLAY_2 = "Pick the first card you want to play or discard. Note that you have the ability to play these 2 last cards. You will choose how to play the last one during your next turn."
    const val PLAY_LAST_SPECIAL = "You have the special ability to play your last card. Choose how you want to play it."
    const val PLAY_FREE_DISCARDED = "Pick a card from the discarded deck, you can play it for free (but you cannot discard for 3 gold coins or upgrade your wonder with it)."
    const val PICK_NEIGHBOR_GUILD = "Choose a Guild card (purple) that you want to copy from one of your neighbours."
    const val WAIT_OTHER_PLAY_LAST = "Another player can play his last card, please wait…"
    const val WAIT_OTHER_PICK_GUILD = "Another player is picking a guild card to copy, please wait…"
    const val WAIT_OTHER_PLAY_DISCARD = "Another player is playing a free card from the discard pile, please wait…"
    const val WATCH_SCORE = "The game is over! Look at the scoreboard to see the final ranking!"
}

@Serializable
sealed class TurnAction {
    abstract val message: String

    @Serializable
    object SayReady : TurnAction() {
        override val message: String = ActionMessages.SAY_READY

        override fun toString(): String = "Say Ready"
    }

    @Serializable
    data class PlayFromHand(
        override val message: String,
        val hand: List<HandCard>,
    ) : TurnAction() {
        override fun toString(): String = "${super.toString()} (${hand.size} cards)"
    }

    @Serializable
    data class PlayFromDiscarded(
        val discardedCards: List<HandCard>,
    ) : TurnAction() {
        override val message: String = ActionMessages.PLAY_FREE_DISCARDED

        override fun toString(): String = "${super.toString()} (${discardedCards.size} cards)"
    }

    @Serializable
    data class PickNeighbourGuild(
        val neighbourGuildCards: List<HandCard>,
    ) : TurnAction() {
        override val message: String = ActionMessages.PICK_NEIGHBOR_GUILD

        override fun toString(): String = "${super.toString()} (${neighbourGuildCards.size} cards)"
    }

    @Serializable
    data class Wait(
        override val message: String,
    ) : TurnAction()

    @Serializable
    data class WatchScore(
        override val message: String = ActionMessages.WATCH_SCORE,
        val scoreBoard: ScoreBoard,
    ) : TurnAction() {
        override fun toString(): String = "${super.toString()} (winner: Player #${scoreBoard.scores[0].playerIndex})"
    }

    fun allowsBuildingWonder(): Boolean = this is PlayFromHand

    fun allowsDiscarding(): Boolean = this is PlayFromHand

    override fun toString(): String = this::class.simpleName!!
}
