package org.luxons.sevenwonders.repositories;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.test.TestUtils;
import org.luxons.sevenwonders.repositories.GameRepository.GameAlreadyExistsException;
import org.luxons.sevenwonders.repositories.GameRepository.GameNotFoundException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class GameRepositoryTest {

    private GameRepository repository;

    @Before
    public void setUp() {
        repository = new GameRepository();
    }

    @Test(expected = GameAlreadyExistsException.class)
    public void add_failsOnExistingId() {
        Game game1 = TestUtils.createGame(0, 5);
        repository.add(game1);
        Game game2 = TestUtils.createGame(0, 7);
        repository.add(game2);
    }

    @Test(expected = GameNotFoundException.class)
    public void find_failsOnUnknownId() {
        repository.find(123);
    }

    @Test
    public void find_returnsTheSameObject() {
        Game game = TestUtils.createGame(0, 5);
        repository.add(game);
        assertSame(game, repository.find(0));
    }

    @Test(expected = GameNotFoundException.class)
    public void remove_failsOnUnknownId() {
        repository.remove(123);
    }

    @Test
    public void remove_succeeds() {
        Game game = TestUtils.createGame(0, 5);
        repository.add(game);
        assertNotNull(repository.find(0));
        repository.remove(0);
        try {
            repository.find(0);
            fail(); // the call to find() should have failed
        } catch (GameNotFoundException e) {
            // the game has been properly removed
        }
    }
}