package org.luxons.sevenwonders.test.api

import org.luxons.sevenwonders.game.api.Action
import java.util.Objects

class ApiPlayerTurnInfo {

    var playerIndex: Int = 0

    var table: ApiTable? = null

    var currentAge: Int = 0

    var action: Action? = null

    var hand: List<ApiHandCard>? = null

    var neighbourGuildCards: List<ApiCard>? = null

    var message: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as ApiPlayerTurnInfo?
        return (playerIndex == that!!.playerIndex && currentAge == that.currentAge && table == that.table && action === that.action && hand == that.hand && neighbourGuildCards == that.neighbourGuildCards && message == that.message)
    }

    override fun hashCode(): Int {
        return Objects.hash(playerIndex, table, currentAge, action, hand, neighbourGuildCards, message)
    }
}
