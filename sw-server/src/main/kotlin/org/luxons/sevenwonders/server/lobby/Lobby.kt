package org.luxons.sevenwonders.server.lobby

import org.luxons.sevenwonders.engine.Game
import org.luxons.sevenwonders.engine.data.GameDefinition
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.api.State
import org.luxons.sevenwonders.model.wonders.AssignedWonder
import org.luxons.sevenwonders.model.wonders.PreGameWonder
import org.luxons.sevenwonders.model.wonders.withRandomSide

class Lobby(
    val id: Long,
    val name: String,
    var owner: Player,
    private val gameDefinition: GameDefinition,
) {
    private val players: MutableList<Player> = ArrayList(gameDefinition.maxPlayers)

    val allWonders: List<PreGameWonder> = gameDefinition.allWonders

    private val assignedWonders: MutableList<AssignedWonder> = mutableListOf()

    var settings: Settings = Settings()

    var state = State.LOBBY
        private set

    init {
        addPlayer(owner)
    }

    fun getPlayers(): List<Player> = players

    fun getAssignedWonders(): List<AssignedWonder> = assignedWonders

    @Synchronized
    fun addPlayer(player: Player) {
        if (hasStarted()) {
            throw GameAlreadyStartedException(name)
        }
        if (maxPlayersReached()) {
            throw PlayerOverflowException(gameDefinition.maxPlayers)
        }
        if (playerNameAlreadyUsed(player.displayName)) {
            throw PlayerNameAlreadyUsedException(player.displayName, name)
        }
        player.join(this)
        players.add(player)
        assignedWonders.add(pickRandomWonder())
    }

    private fun pickRandomWonder(): AssignedWonder = allWonders.filter { !it.isAssigned() }.random().withRandomSide()

    private fun PreGameWonder.isAssigned() = name in assignedWonders.map { it.name }

    private fun hasStarted(): Boolean = state != State.LOBBY

    fun maxPlayersReached(): Boolean = players.size >= gameDefinition.maxPlayers

    private fun playerNameAlreadyUsed(name: String): Boolean = players.any { it.displayName == name }

    @Synchronized
    fun startGame(): Game {
        if (!hasEnoughPlayers()) {
            throw PlayerUnderflowException(gameDefinition.minPlayers)
        }
        state = State.PLAYING
        val game = gameDefinition.createGame(id, assignedWonders, settings)
        players.forEachIndexed { index, player -> player.join(game, index) }
        return game
    }

    fun hasEnoughPlayers(): Boolean = players.size >= gameDefinition.minPlayers

    @Synchronized
    fun reorderPlayers(orderedUsernames: List<String>) {
        val usernames = players.map { it.username }
        if (orderedUsernames.toSet() != usernames.toSet()) {
            throw PlayerListMismatchException(orderedUsernames)
        }
        val wondersMap = players.indices.associate { assignedWonders[it] to players[it].username }
        players.sortBy { orderedUsernames.indexOf(it.username) }
        assignedWonders.sortBy { orderedUsernames.indexOf(wondersMap[it]) }
    }

    @Synchronized
    fun reassignWonders(wonders: List<AssignedWonder>) {
        require(wonders.size == players.size)
        wonders.forEach {
            require(it.name in allWonders.map { w -> w.name })
        }
        assignedWonders.clear()
        assignedWonders.addAll(wonders)
    }

    @Synchronized
    fun isOwner(username: String?): Boolean = owner.username == username

    @Synchronized
    fun containsUser(username: String): Boolean = players.any { it.username == username }

    @Synchronized
    fun removePlayer(username: String): Player {
        val playerIndex = players.indexOfFirst { it.username == username }
        if (playerIndex < 0) {
            throw UnknownPlayerException(username)
        }
        assignedWonders.removeAt(playerIndex)
        val player = players.removeAt(playerIndex)
        player.leave()

        if (player == owner && players.isNotEmpty()) {
            owner = players[0]
        }
        return player
    }

    fun setEndOfGame() {
        state = State.FINISHED
    }

    internal class GameAlreadyStartedException(name: String) : IllegalStateException("Game '$name' has already started")

    internal class PlayerOverflowException(max: Int) : IllegalStateException("Maximum $max players allowed")

    internal class PlayerUnderflowException(min: Int) :
        IllegalStateException("Minimum $min players required to start a game")

    internal class PlayerNameAlreadyUsedException(displayName: String, gameName: String) :
        IllegalArgumentException("Name '$displayName' is already used by a player in game '$gameName'")

    internal class UnknownPlayerException(username: String) : IllegalArgumentException("Unknown player '$username'")

    internal class PlayerListMismatchException(usernames: List<String>) :
        IllegalArgumentException("Newly ordered usernames $usernames don't match the current player list")
}
