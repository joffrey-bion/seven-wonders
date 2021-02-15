package org.luxons.sevenwonders.server.repositories

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class PlayerRepositoryTest {

    private lateinit var repository: PlayerRepository

    @Before
    fun setUp() {
        val meterRegistry = SimpleMeterRegistry()
        repository = PlayerRepository(meterRegistry)
    }

    @Test
    fun contains_falseIfNoUserAdded() {
        assertFalse(repository.contains("anyUsername"))
    }

    @Test
    fun contains_trueForCreatedPlayer() {
        repository.createOrUpdate("player1", "Player 1")
        assertTrue(repository.contains("player1"))
    }

    @Test
    fun createOrUpdate_createsProperly() {
        val player1 = repository.createOrUpdate("player1", "Player 1")
        assertEquals("player1", player1.username)
        assertEquals("Player 1", player1.displayName)
    }

    @Test
    fun createOrUpdate_updatesDisplayName() {
        val player1 = repository.createOrUpdate("player1", "Player 1")
        val player1Updated = repository.createOrUpdate("player1", "Much Better Name")
        assertSame(player1, player1Updated)
        assertEquals("Much Better Name", player1Updated.displayName)
    }

    @Test
    fun find_failsOnUnknownUsername() {
        assertFailsWith<PlayerNotFoundException> {
            repository.get("anyUsername")
        }
    }

    @Test
    fun find_returnsTheSameObject() {
        val player1 = repository.createOrUpdate("player1", "Player 1")
        val player2 = repository.createOrUpdate("player2", "Player 2")
        assertSame(player1, repository.get("player1"))
        assertSame(player2, repository.get("player2"))
    }

    @Test
    fun remove_failsOnUnknownUsername() {
        assertFailsWith<PlayerNotFoundException> {
            repository.remove("anyUsername")
        }
    }

    @Test
    fun remove_succeeds() {
        repository.createOrUpdate("player1", "Player 1")
        assertTrue(repository.contains("player1"))
        repository.remove("player1")
        assertFalse(repository.contains("player1"))
    }
}
