package org.luxons.sevenwonders.server.controllers

import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.model.api.events.GameListEvent
import org.luxons.sevenwonders.model.api.events.wrap
import org.luxons.sevenwonders.server.lobby.Player
import org.springframework.messaging.simp.SimpMessageSendingOperations

internal fun SimpMessageSendingOperations.sendGameEvent(player: Player, event: GameEvent) {
    convertAndSendToUser(player.username, "/queue/game/events", event.wrap())
}

internal fun SimpMessageSendingOperations.sendGameEvent(players: List<Player>, event: GameEvent) {
    players.forEach { sendGameEvent(it, event) }
}

internal fun SimpMessageSendingOperations.sendGameListEvent(event: GameListEvent) {
    convertAndSend("/topic/games", event.wrap())
}
