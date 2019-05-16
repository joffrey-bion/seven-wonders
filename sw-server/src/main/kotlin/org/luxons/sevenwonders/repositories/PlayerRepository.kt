package org.luxons.sevenwonders.repositories

import org.luxons.sevenwonders.errors.ApiMisuseException
import org.luxons.sevenwonders.lobby.Player
import org.springframework.stereotype.Repository
import java.util.HashMap

@Repository
class PlayerRepository {

    private val players = HashMap<String, Player>()

    operator fun contains(username: String): Boolean = players.containsKey(username)

    fun createOrUpdate(username: String, displayName: String): Player {
        return if (players.containsKey(username)) {
            update(username, displayName)
        } else {
            create(username, displayName)
        }
    }

    private fun create(username: String, displayName: String): Player {
        val player = Player(username, displayName)
        players[username] = player
        return player
    }

    private fun update(username: String, displayName: String): Player {
        val player = find(username)
        player.displayName = displayName
        return player
    }

    fun find(username: String): Player = players[username] ?: throw PlayerNotFoundException(username)

    fun remove(username: String): Player = players.remove(username) ?: throw PlayerNotFoundException(username)
}

internal class PlayerNotFoundException(username: String) :
    ApiMisuseException("Player '$username' doesn't exist")
