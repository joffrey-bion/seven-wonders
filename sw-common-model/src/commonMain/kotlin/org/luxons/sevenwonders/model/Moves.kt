package org.luxons.sevenwonders.model

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.noTransactions

@Serializable
data class PlayedMove(
    val playerIndex: Int,
    val type: MoveType,
    val card: TableCard, // For UPGRADE_WONDER and DISCARD, this shouldn't be sent to everyone
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
