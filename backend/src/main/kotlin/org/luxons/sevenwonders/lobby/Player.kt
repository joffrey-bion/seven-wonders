package org.luxons.sevenwonders.lobby

import com.fasterxml.jackson.annotation.JsonIgnore
import org.luxons.sevenwonders.errors.ApiMisuseException
import org.luxons.sevenwonders.game.Game

class Player(
    val username: String,
    var displayName: String
) {
    var index: Int = -1

    var isReady: Boolean = false

    val isGameOwner: Boolean
        get() = _lobby?.isOwner(username) ?: false

    val isInLobby: Boolean
        get() = _lobby != null

    val isInGame: Boolean
        get() = _game != null

    @Transient
    private var _lobby: Lobby? = null

    @get:JsonIgnore
    val lobby: Lobby
        get() = _lobby ?: throw PlayerNotInLobbyException(username)

    @get:JsonIgnore
    val ownedLobby: Lobby
        get() = if (isGameOwner) lobby else throw PlayerIsNotOwnerException(username)

    @Transient
    private var _game: Game? = null

    @get:JsonIgnore
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

    override fun toString(): String {
        return "'$displayName' ($username)"
    }
}

internal class PlayerNotInLobbyException(username: String) :
    ApiMisuseException("User $username is not in a lobby, create or join a game first")

internal class PlayerIsNotOwnerException(username: String) :
    ApiMisuseException("User $username does not own the lobby he's in")

internal class PlayerNotInGameException(username: String) :
    ApiMisuseException("User $username is not in a game, start a game first")
