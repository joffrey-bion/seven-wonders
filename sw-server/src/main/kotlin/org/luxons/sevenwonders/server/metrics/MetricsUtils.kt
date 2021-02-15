package org.luxons.sevenwonders.server.metrics

import io.micrometer.core.instrument.Tag
import org.luxons.sevenwonders.server.lobby.Lobby

fun Lobby.playerCountsTags(): List<Tag> {
    val players = getPlayers()
    return listOf(
        Tag.of("nPlayers", players.size.toString()),
        Tag.of("nHumans", players.filter { it.isHuman }.size.toString()),
        Tag.of("nBots", players.filterNot { it.isHuman }.size.toString()),
    )
}
