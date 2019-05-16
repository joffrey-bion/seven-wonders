package org.luxons.sevenwonders.api

import org.luxons.sevenwonders.lobby.Player

data class PlayerDTO(
    val username: String,
    val displayName: String,
    val index: Int,
    val isGameOwner: Boolean,
    val isUser: Boolean
)

fun Player.toDTO(currentUser: String) =
    PlayerDTO(username, displayName, index, isGameOwner, username === currentUser)
