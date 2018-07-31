package org.luxons.sevenwonders.repositories

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.lobby.Player

class LobbyRepositoryTest {

    private lateinit var repository: LobbyRepository

    @Before
    fun setUp() {
        repository = LobbyRepository()
    }

    @Test
    fun list_initiallyEmpty() {
        assertTrue(repository.list().isEmpty())
    }

    @Test
    fun list_returnsAllLobbies() {
        val owner = Player("owner", "The Owner")
        val lobby1 = repository.create("Test Name 1", owner)
        val lobby2 = repository.create("Test Name 2", owner)
        assertTrue(repository.list().contains(lobby1))
        assertTrue(repository.list().contains(lobby2))
    }

    @Test
    fun create_withCorrectOwner() {
        val owner = Player("owner", "The Owner")
        val lobby = repository.create("Test Name", owner)
        assertTrue(lobby.isOwner(owner.username))
    }

    @Test(expected = LobbyNotFoundException::class)
    fun find_failsOnUnknownId() {
        repository.find(123)
    }

    @Test
    fun find_returnsTheSameObject() {
        val owner = Player("owner", "The Owner")
        val lobby1 = repository.create("Test Name 1", owner)
        val lobby2 = repository.create("Test Name 2", owner)
        assertSame(lobby1, repository.find(lobby1.id))
        assertSame(lobby2, repository.find(lobby2.id))
    }

    @Test(expected = LobbyNotFoundException::class)
    fun remove_failsOnUnknownId() {
        repository.remove(123)
    }

    @Test
    fun remove_succeeds() {
        val owner = Player("owner", "The Owner")
        val lobby1 = repository.create("Test Name 1", owner)
        assertNotNull(repository.find(lobby1.id))
        repository.remove(lobby1.id)
        try {
            repository.find(lobby1.id)
            fail() // the call to find() should have failed
        } catch (e: LobbyNotFoundException) {
            // the lobby has been properly removed
        }
    }
}
