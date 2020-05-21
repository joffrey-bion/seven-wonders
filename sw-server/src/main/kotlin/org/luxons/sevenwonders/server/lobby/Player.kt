package org.luxons.sevenwonders.server.lobby

import org.luxons.sevenwonders.engine.Game
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.server.ApiMisuseException

class Player(
    val username: String,
    var displayName: String,
    var icon: Icon? = null
) {
    var index: Int = -1

    var isReady: Boolean = false

    val isGameOwner: Boolean
        get() = _lobby?.isOwner(username) ?: false

    val isInLobby: Boolean
        get() = _lobby != null

    val isInGame: Boolean
        get() = _game != null

    private var _lobby: Lobby? = null

    val lobby: Lobby
        get() = _lobby ?: throw PlayerNotInLobbyException(username)

    val ownedLobby: Lobby
        get() = if (isGameOwner) lobby else throw PlayerIsNotOwnerException(username)

    private var _game: Game? = null

    val game: Game
        get() = _game ?: throw PlayerNotInGameException(username)

    fun join(lobby: Lobby) {
        _lobby = lobby
    }

    fun join(game: Game, index: Int) {
        _game = game
        this.index = index
    }

    fun leave() {
        _lobby = null
        _game = null
        index = -1
    }

    override fun toString(): String = "'$displayName' ($username)"
}

internal class PlayerNotInLobbyException(username: String) :
    ApiMisuseException("User $username is not in a lobby, create or join a game first")

internal class PlayerIsNotOwnerException(username: String) :
    ApiMisuseException("User $username does not own the lobby he's in")

internal class PlayerNotInGameException(username: String) :
    ApiMisuseException("User $username is not in a game, start a game first")
