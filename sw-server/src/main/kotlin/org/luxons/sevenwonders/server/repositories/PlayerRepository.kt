package org.luxons.sevenwonders.server.repositories

import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.server.ApiMisuseException
import org.luxons.sevenwonders.server.lobby.Player
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class PlayerRepository {

    private val players = ConcurrentHashMap<String, Player>()

    operator fun contains(username: String): Boolean = players.containsKey(username)

    fun createOrUpdate(username: String, displayName: String, isHuman: Boolean = true, icon: Icon? = null): Player {
        val p = players.computeIfAbsent(username) { Player(username, displayName, isHuman, icon) }
        p.displayName = displayName
        p.icon = icon
        return p
    }

    fun find(username: String): Player? = players[username]

    fun get(username: String): Player = find(username) ?: throw PlayerNotFoundException(username)

    fun remove(username: String): Player = players.remove(username) ?: throw PlayerNotFoundException(username)
}

internal class PlayerNotFoundException(username: String) : ApiMisuseException("Player '$username' doesn't exist")
