package org.luxons.sevenwonders.server.validation

import org.luxons.sevenwonders.server.repositories.LobbyRepository
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class DestinationAccessValidator(private val lobbyRepository: LobbyRepository) {

    fun hasAccess(username: String?, destination: String): Boolean {
        return when {
            username == null -> false // unnamed user cannot belong to anything
            hasForbiddenGameReference(username, destination) -> false
            hasForbiddenLobbyReference(username, destination) -> false
            else -> true
        }
    }

    private fun hasForbiddenGameReference(username: String, destination: String): Boolean {
        val gameMatcher = gameDestination.matcher(destination)
        if (!gameMatcher.matches()) {
            return false // no game reference is always OK
        }
        val gameId = gameMatcher.group("id").toLong()
        return !isUserInLobby(username, gameId)
    }

    private fun hasForbiddenLobbyReference(username: String, destination: String): Boolean {
        val lobbyMatcher = lobbyDestination.matcher(destination)
        if (!lobbyMatcher.matches()) {
            return false // no lobby reference is always OK
        }
        val lobbyId = lobbyMatcher.group("id").toLong()
        return !isUserInLobby(username, lobbyId)
    }

    private fun isUserInLobby(username: String, lobbyId: Long): Boolean =
        lobbyRepository.get(lobbyId).containsUser(username)

    companion object {

        private val lobbyDestination = Pattern.compile(".*?/lobby/(?<id>\\d+?)(/.*)?")

        private val gameDestination = Pattern.compile(".*?/game/(?<id>\\d+?)(/.*)?")
    }
}
