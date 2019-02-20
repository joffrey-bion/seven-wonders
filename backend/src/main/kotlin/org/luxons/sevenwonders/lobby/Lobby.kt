package org.luxons.sevenwonders.lobby

import org.luxons.sevenwonders.game.Game
import org.luxons.sevenwonders.game.api.CustomizableSettings
import org.luxons.sevenwonders.game.data.GameDefinition

enum class State {
    LOBBY, PLAYING
}

class Lobby(
    val id: Long,
    val name: String,
    var owner: Player,
    @field:Transient private val gameDefinition: GameDefinition
) {
    private val players: MutableList<Player> = ArrayList(gameDefinition.maxPlayers)

    var settings: CustomizableSettings = CustomizableSettings()

    var state = State.LOBBY
        private set

    init {
        addPlayer(owner)
    }

    fun getPlayers(): List<Player> = players

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
    }

    private fun hasStarted(): Boolean = state != State.LOBBY

    private fun maxPlayersReached(): Boolean = players.size >= gameDefinition.maxPlayers

    private fun playerNameAlreadyUsed(name: String?): Boolean = players.any { it.displayName == name }

    @Synchronized
    fun startGame(): Game {
        if (!hasEnoughPlayers()) {
            throw PlayerUnderflowException(gameDefinition.minPlayers)
        }
        state = State.PLAYING
        val game = gameDefinition.initGame(id, settings, players.size)
        players.forEachIndexed { index, player -> player.join(game, index) }
        return game
    }

    private fun hasEnoughPlayers(): Boolean = players.size >= gameDefinition.minPlayers

    @Synchronized
    fun reorderPlayers(orderedUsernames: List<String>) {
        val usernames = players.map { it.username }
        if (orderedUsernames.toSet() != usernames.toSet()) {
            throw PlayerListMismatchException(orderedUsernames)
        }
        players.sortBy { orderedUsernames.indexOf(it.username) }
    }

    private fun find(username: String): Player =
        players.firstOrNull { it.username == username } ?: throw UnknownPlayerException(username)

    @Synchronized
    fun isOwner(username: String?): Boolean = owner.username == username

    @Synchronized
    fun containsUser(username: String): Boolean = players.any { it.username == username }

    @Synchronized
    fun removePlayer(username: String): Player {
        val player = find(username)
        players.remove(player)
        player.leave()

        if (player == owner && !players.isEmpty()) {
            owner = players[0]
        }
        return player
    }

    internal class GameAlreadyStartedException(name: String) :
        IllegalStateException("Game '$name' has already started")

    internal class PlayerOverflowException(max: Int) :
        IllegalStateException("Maximum $max players allowed")

    internal class PlayerUnderflowException(min: Int) :
        IllegalStateException("Minimum $min players required to start a game")

    internal class PlayerNameAlreadyUsedException(displayName: String, gameName: String) :
        IllegalArgumentException("Name '$displayName' is already used by a player in game '$gameName'")

    internal class UnknownPlayerException(username: String) :
        IllegalArgumentException("Unknown player '$username'")

    internal class PlayerListMismatchException(usernames: List<String>) :
        IllegalArgumentException("Newly ordered usernames $usernames don't match the current player list")
}
