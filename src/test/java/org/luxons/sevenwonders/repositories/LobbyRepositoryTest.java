package org.luxons.sevenwonders.repositories;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.Lobby;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.luxons.sevenwonders.repositories.LobbyRepository.LobbyNotFoundException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LobbyRepositoryTest {

    private LobbyRepository repository;

    @Before
    public void setUp() {
        repository = new LobbyRepository(new GameDefinitionLoader());
    }

    @Test
    public void list_initiallyEmpty() {
        assertTrue(repository.list().isEmpty());
    }

    @Test
    public void list_returnsAllLobbies() {
        Player owner = new Player("owner", "The Owner");
        Lobby lobby1 = repository.create("Test Name 1", owner);
        Lobby lobby2 = repository.create("Test Name 2", owner);
        assertTrue(repository.list().contains(lobby1));
        assertTrue(repository.list().contains(lobby2));
    }

    @Test
    public void create_withCorrectOwner() {
        Player owner = new Player("owner", "The Owner");
        Lobby lobby = repository.create("Test Name", owner);
        assertTrue(lobby.isOwner(owner.getUsername()));
    }

    @Test(expected = LobbyNotFoundException.class)
    public void find_failsOnUnknownId() {
        repository.find(123);
    }

    @Test
    public void find_returnsTheSameObject() {
        Player owner = new Player("owner", "The Owner");
        Lobby lobby1 = repository.create("Test Name 1", owner);
        Lobby lobby2 = repository.create("Test Name 2", owner);
        assertSame(lobby1, repository.find(lobby1.getId()));
        assertSame(lobby2, repository.find(lobby2.getId()));
    }

    @Test(expected = LobbyNotFoundException.class)
    public void remove_failsOnUnknownId() {
        repository.remove(123);
    }

    @Test
    public void remove_succeeds() {
        Player owner = new Player("owner", "The Owner");
        Lobby lobby1 = repository.create("Test Name 1", owner);
        assertNotNull(repository.find(lobby1.getId()));
        repository.remove(lobby1.getId());
        try {
            repository.find(lobby1.getId());
            fail(); // the call to find() should have failed
        } catch (LobbyNotFoundException e) {
            // the lobby has been properly removed
        }
    }
}