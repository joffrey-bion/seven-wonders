package org.luxons.sevenwonders.test.api

import org.luxons.sevenwonders.game.api.CustomizableSettings
import org.luxons.sevenwonders.lobby.State
import java.util.Objects

class ApiLobby {

    var id: Long = 0

    var name: String? = null

    var owner: String? = null

    var players: List<ApiPlayer>? = null

    var settings: CustomizableSettings? = null

    var state: State? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val apiLobby = other as ApiLobby?
        return (id == apiLobby!!.id && name == apiLobby.name && owner == apiLobby.owner && players == apiLobby.players && settings == apiLobby.settings && state === apiLobby.state)
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name, owner, players, settings, state)
    }
}
