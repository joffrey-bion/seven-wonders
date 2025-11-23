package org.luxons.sevenwonders.server.validation

import io.micrometer.core.instrument.simple.*
import org.luxons.sevenwonders.server.lobby.*
import org.luxons.sevenwonders.server.repositories.*
import kotlin.test.*

class DestinationAccessValidatorTest {

    private lateinit var lobbyRepository: LobbyRepository

    private lateinit var destinationAccessValidator: DestinationAccessValidator

    @BeforeTest
    fun setup() {
        val meterRegistry = SimpleMeterRegistry()
        lobbyRepository = LobbyRepository(meterRegistry)
        destinationAccessValidator = DestinationAccessValidator(lobbyRepository)
    }

    private fun createLobby(gameName: String, ownerUsername: String, vararg otherPlayers: String): Lobby {
        val owner = Player(ownerUsername, ownerUsername)
        val lobby = lobbyRepository.create(gameName, owner)
        for (playerName in otherPlayers) {
            val player = Player(playerName, playerName)
            lobby.addPlayer(player)
        }
        return lobby
    }

    private fun createGame(gameName: String, ownerUsername: String, vararg otherPlayers: String) {
        val lobby = createLobby(gameName, ownerUsername, *otherPlayers)
        lobby.startGame()
    }

    @Test
    fun validate_failsOnNullUser() {
        assertFalse(destinationAccessValidator.hasAccess(null, "doesNotMatter"))
    }

    @Test
    fun validate_successWhenNoReference() {
        assertTrue(destinationAccessValidator.hasAccess("", ""))
        assertTrue(destinationAccessValidator.hasAccess("", "test"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "test"))
    }

    @Test
    fun validate_successWhenNoRefFollows() {
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/game/"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/lobby/"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "prefix/game/"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "prefix/lobby/"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/game//suffix"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/lobby//suffix"))
    }

    @Test
    fun validate_successWhenRefIsNotANumber() {
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/game/notANumber"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/lobby/notANumber"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "prefix/game/notANumber"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "prefix/lobby/notANumber"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/game/notANumber/suffix"))
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/lobby/notANumber/suffix"))
    }

    @Test
    fun validate_failWhenNoLobbyExist() {
        assertFailsWith<LobbyNotFoundException> {
            destinationAccessValidator.hasAccess("", "/lobby/0")
        }
    }

    @Test
    fun validate_failWhenNoGameExist() {
        assertFailsWith<LobbyNotFoundException> {
            destinationAccessValidator.hasAccess("", "/game/0")
        }
    }

    @Test
    fun validate_failWhenReferencedLobbyDoesNotExist() {
        createLobby("Test Game", "ownerUser1")
        createLobby("Test Game 2", "ownerUser2")
        assertFailsWith<LobbyNotFoundException> {
            destinationAccessValidator.hasAccess("doesNotMatter", "/lobby/3")
        }
    }

    @Test
    fun validate_failWhenReferencedGameDoesNotExist() {
        createGame("Test Game 1", "user1", "user2", "user3")
        createGame("Test Game 2", "user4", "user5", "user6")
        assertFailsWith<LobbyNotFoundException> {
            destinationAccessValidator.hasAccess("doesNotMatter", "/game/3")
        }
    }

    @Test
    fun validate_failWhenUserIsNotPartOfReferencedLobby() {
        createLobby("Test Game", "ownerUser")
        destinationAccessValidator.hasAccess("userNotInLobby", "/lobby/0")
    }

    @Test
    fun validate_failWhenUserIsNotPartOfReferencedGame() {
        createGame("Test Game", "ownerUser", "otherUser1", "otherUser2")
        destinationAccessValidator.hasAccess("userNotInGame", "/game/0")
    }

    @Test
    fun validate_successWhenUserIsOwnerOfReferencedLobby() {
        createLobby("Test Game 1", "user1")
        assertTrue(destinationAccessValidator.hasAccess("user1", "/lobby/0"))
        createLobby("Test Game 2", "user2")
        assertTrue(destinationAccessValidator.hasAccess("user2", "/lobby/1"))
    }

    @Test
    fun validate_successWhenUserIsMemberOfReferencedLobby() {
        createLobby("Test Game 1", "user1", "user2")
        assertTrue(destinationAccessValidator.hasAccess("user2", "/lobby/0"))
        createLobby("Test Game 2", "user3", "user4")
        assertTrue(destinationAccessValidator.hasAccess("user4", "/lobby/1"))
    }

    @Test
    fun validate_successWhenUserIsOwnerOfReferencedGame() {
        createGame("Test Game 1", "owner1", "user2", "user3")
        assertTrue(destinationAccessValidator.hasAccess("owner1", "/game/0"))
        createGame("Test Game 2", "owner4", "user5", "user6")
        assertTrue(destinationAccessValidator.hasAccess("owner4", "/game/1"))
    }

    @Test
    fun validate_successWhenUserIsMemberOfReferencedGame() {
        createGame("Test Game 1", "owner1", "user2", "user3")
        assertTrue(destinationAccessValidator.hasAccess("user2", "/game/0"))
        createGame("Test Game 2", "owner4", "user5", "user6")
        assertTrue(destinationAccessValidator.hasAccess("user6", "/game/1"))
    }
}
