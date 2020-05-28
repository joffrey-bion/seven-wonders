package org.luxons.sevenwonders.model.api

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.model.wonders.AssignedWonder

interface BasicPlayerInfo {
    val username: String
    val displayName: String
    val icon: Icon?
}

@Serializable
data class ConnectedPlayer(
    override val username: String,
    override val displayName: String,
    override val icon: Icon?
) : BasicPlayerInfo

@Serializable
data class PlayerDTO(
    override val username: String,
    override val displayName: String,
    override val icon: Icon?,
    val wonder: AssignedWonder,
    val isGameOwner: Boolean,
    val isReady: Boolean
) : BasicPlayerInfo
