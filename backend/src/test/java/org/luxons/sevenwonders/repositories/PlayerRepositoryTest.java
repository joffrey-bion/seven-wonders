package org.luxons.sevenwonders.repositories;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.lobby.Player;
import org.luxons.sevenwonders.repositories.PlayerRepository.PlayerNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PlayerRepositoryTest {

    private PlayerRepository repository;

    @Before
    public void setUp() {
        repository = new PlayerRepository();
    }

    @Test
    public void contains_falseIfNoUserAdded() {
        assertFalse(repository.contains("anyUsername"));
    }

    @Test
    public void contains_trueForCreatedPlayer() {
        repository.createOrUpdate("player1", "Player 1");
        assertTrue(repository.contains("player1"));
    }

    @Test
    public void createOrUpdate_createsProperly() {
        Player player1 = repository.createOrUpdate("player1", "Player 1");
        assertEquals("player1", player1.getUsername());
        assertEquals("Player 1", player1.getDisplayName());
    }

    @Test
    public void createOrUpdate_updatesDisplayName() {
        Player player1 = repository.createOrUpdate("player1", "Player 1");
        Player player1Updated = repository.createOrUpdate("player1", "Much Better Name");
        assertSame(player1, player1Updated);
        assertEquals("Much Better Name", player1Updated.getDisplayName());
    }

    @Test(expected = PlayerNotFoundException.class)
    public void find_failsOnUnknownUsername() {
        repository.find("anyUsername");
    }

    @Test
    public void find_returnsTheSameObject() {
        Player player1 = repository.createOrUpdate("player1", "Player 1");
        Player player2 = repository.createOrUpdate("player2", "Player 2");
        assertSame(player1, repository.find("player1"));
        assertSame(player2, repository.find("player2"));
    }

    @Test(expected = PlayerNotFoundException.class)
    public void remove_failsOnUnknownUsername() {
        repository.remove("anyUsername");
    }

    @Test
    public void remove_succeeds() {
        repository.createOrUpdate("player1", "Player 1");
        assertTrue(repository.contains("player1"));
        repository.remove("player1");
        assertFalse(repository.contains("player1"));
    }
}
