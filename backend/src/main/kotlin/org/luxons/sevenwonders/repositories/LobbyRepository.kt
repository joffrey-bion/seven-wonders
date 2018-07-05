package org.luxons.sevenwonders.repositories

import org.luxons.sevenwonders.game.data.GameDefinitionLoader
import org.luxons.sevenwonders.lobby.Lobby
import org.luxons.sevenwonders.lobby.Player
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.HashMap

@Repository
class LobbyRepository @Autowired constructor() {

    private val gameDefinitionLoader: GameDefinitionLoader = GameDefinitionLoader()

    private val lobbies = HashMap<Long, Lobby>()

    private var lastGameId: Long = 0

    fun list(): Collection<Lobby> = lobbies.values

    fun create(gameName: String, owner: Player): Lobby {
        val id = lastGameId++
        val lobby = Lobby(id, gameName, owner, gameDefinitionLoader.gameDefinition)
        lobbies[id] = lobby
        return lobby
    }

    fun find(lobbyId: Long): Lobby = lobbies[lobbyId] ?: throw LobbyNotFoundException(lobbyId)

    fun remove(lobbyId: Long): Lobby = lobbies.remove(lobbyId) ?: throw LobbyNotFoundException(lobbyId)
}

internal class LobbyNotFoundException(id: Long) : RuntimeException("Lobby not found for id '$id'")
