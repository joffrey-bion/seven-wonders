package org.luxons.sevenwonders.server.repositories

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.server.lobby.Player
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail

class LobbyRepositoryTest {

    private lateinit var repository: LobbyRepository

    @Before
    fun setUp() {
        val meterRegistry = SimpleMeterRegistry()
        repository = LobbyRepository(meterRegistry)
    }

    @Test
    fun list_initiallyEmpty() {
        assertTrue(repository.list().isEmpty())
    }

    @Test
    fun list_returnsAllLobbies() {
        val lobby1 = repository.create("Test Name 1", Player("owner1", "The Owner"))
        val lobby2 = repository.create("Test Name 2", Player("owner2", "The Owner"))
        assertTrue(repository.list().contains(lobby1))
        assertTrue(repository.list().contains(lobby2))
    }

    @Test
    fun create_withCorrectOwner() {
        val owner = Player("owner", "The Owner")
        val lobby = repository.create("Test Name", owner)
        assertTrue(lobby.isOwner(owner.username))
    }

    @Test
    fun find_failsOnUnknownId() {
        assertFailsWith<LobbyNotFoundException> {
            repository.get(123)
        }
    }

    @Test
    fun find_returnsTheSameObject() {
        val lobby1 = repository.create("Test Name 1", Player("owner1", "The Owner"))
        val lobby2 = repository.create("Test Name 2", Player("owner2", "The Owner"))
        assertSame(lobby1, repository.get(lobby1.id))
        assertSame(lobby2, repository.get(lobby2.id))
    }

    @Test
    fun remove_failsOnUnknownId() {
        assertFailsWith<LobbyNotFoundException> {
            repository.remove(123)
        }
    }

    @Test
    fun remove_succeeds() {
        val owner = Player("owner", "The Owner")
        val lobby1 = repository.create("Test Name 1", owner)
        assertNotNull(repository.get(lobby1.id))
        repository.remove(lobby1.id)
        try {
            repository.get(lobby1.id)
            fail() // the call to find() should have failed
        } catch (e: LobbyNotFoundException) {
            // the lobby has been properly removed
        }
    }
}
