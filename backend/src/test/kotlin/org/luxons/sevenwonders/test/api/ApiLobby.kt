package org.luxons.sevenwonders.test.api

import java.util.Objects

import org.luxons.sevenwonders.game.api.CustomizableSettings
import org.luxons.sevenwonders.lobby.State

class ApiLobby {

    var id: Long = 0

    var name: String? = null

    var owner: String? = null

    var players: List<ApiPlayer>? = null

    var settings: CustomizableSettings? = null

    var state: State? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val apiLobby = o as ApiLobby?
        return (id == apiLobby!!.id && name == apiLobby.name && owner == apiLobby.owner && players == apiLobby.players && settings == apiLobby.settings && state === apiLobby.state)
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name, owner, players, settings, state)
    }
}
