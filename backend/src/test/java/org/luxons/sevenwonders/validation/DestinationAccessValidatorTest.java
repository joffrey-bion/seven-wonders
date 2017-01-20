package org.luxons.sevenwonders.validation;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Lobby;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.luxons.sevenwonders.repositories.GameRepository;
import org.luxons.sevenwonders.repositories.GameRepository.GameNotFoundException;
import org.luxons.sevenwonders.repositories.LobbyRepository;
import org.luxons.sevenwonders.repositories.LobbyRepository.LobbyNotFoundException;

import static org.junit.Assert.*;

public class DestinationAccessValidatorTest {

    private LobbyRepository lobbyRepository;

    private GameRepository gameRepository;

    private DestinationAccessValidator destinationAccessValidator;

    @Before
    public void setup() {
        gameRepository = new GameRepository();
        lobbyRepository = new LobbyRepository(new GameDefinitionLoader());
        destinationAccessValidator = new DestinationAccessValidator(lobbyRepository, gameRepository);
    }

    private Lobby createLobby(String gameName, String ownerUsername, String... otherPlayers) {
        Player owner = new Player(ownerUsername, ownerUsername);
        Lobby lobby = lobbyRepository.create(gameName, owner);
        for (String playerName : otherPlayers) {
            Player player = new Player(playerName, playerName);
            lobby.addPlayer(player);
        }
        return lobby;
    }

    private void createGame(String gameName, String ownerUsername, String... otherPlayers) {
        Lobby lobby = createLobby(gameName, ownerUsername, otherPlayers);
        Game game = lobby.startGame();
        gameRepository.add(game);
    }

    @Test
    public void validate_failsOnNullUser() {
        assertFalse(destinationAccessValidator.hasAccess(null, "doesNotMatter"));
    }

    @Test
    public void validate_successWhenNoReference() {
        assertTrue(destinationAccessValidator.hasAccess("", ""));
        assertTrue(destinationAccessValidator.hasAccess("", "test"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "test"));
    }

    @Test
    public void validate_successWhenNoRefFollows() {
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/game/"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/lobby/"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "prefix/game/"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "prefix/lobby/"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/game//suffix"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/lobby//suffix"));
    }

    @Test
    public void validate_successWhenRefIsNotANumber() {
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/game/notANumber"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/lobby/notANumber"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "prefix/game/notANumber"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "prefix/lobby/notANumber"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/game/notANumber/suffix"));
        assertTrue(destinationAccessValidator.hasAccess("testUser", "/lobby/notANumber/suffix"));
    }

    @Test(expected = LobbyNotFoundException.class)
    public void validate_failWhenNoLobbyExist() {
        destinationAccessValidator.hasAccess("", "/lobby/0");
    }

    @Test(expected = GameNotFoundException.class)
    public void validate_failWhenNoGameExist() {
        destinationAccessValidator.hasAccess("", "/game/0");
    }

    @Test(expected = LobbyNotFoundException.class)
    public void validate_failWhenReferencedLobbyDoesNotExist() {
        createLobby("Test Game", "ownerUser1");
        createLobby("Test Game 2", "ownerUser2");
        destinationAccessValidator.hasAccess("doesNotMatter", "/lobby/3");
    }

    @Test(expected = GameNotFoundException.class)
    public void validate_failWhenReferencedGameDoesNotExist() {
        createGame("Test Game 1", "user1", "user2", "user3");
        createGame("Test Game 2", "user4", "user5", "user6");
        destinationAccessValidator.hasAccess("doesNotMatter", "/game/3");
    }

    @Test
    public void validate_failWhenUserIsNotPartOfReferencedLobby() {
        createLobby("Test Game", "ownerUser");
        destinationAccessValidator.hasAccess("userNotInLobby", "/lobby/0");
    }

    @Test
    public void validate_failWhenUserIsNotPartOfReferencedGame() {
        createGame("Test Game", "ownerUser", "otherUser1", "otherUser2");
        destinationAccessValidator.hasAccess("userNotInGame", "/game/0");
    }

    @Test
    public void validate_successWhenUserIsOwnerOfReferencedLobby() {
        createLobby("Test Game 1", "user1");
        assertTrue(destinationAccessValidator.hasAccess("user1", "/lobby/0"));
        createLobby("Test Game 2", "user2");
        assertTrue(destinationAccessValidator.hasAccess("user2", "/lobby/1"));
    }

    @Test
    public void validate_successWhenUserIsMemberOfReferencedLobby() {
        createLobby("Test Game 1", "user1", "user2");
        assertTrue(destinationAccessValidator.hasAccess("user2", "/lobby/0"));
        createLobby("Test Game 2", "user3", "user4");
        assertTrue(destinationAccessValidator.hasAccess("user4", "/lobby/1"));
    }

    @Test
    public void validate_successWhenUserIsOwnerOfReferencedGame() {
        createGame("Test Game 1", "owner1", "user2", "user3");
        assertTrue(destinationAccessValidator.hasAccess("owner1", "/game/0"));
        createGame("Test Game 2", "owner4", "user5", "user6");
        assertTrue(destinationAccessValidator.hasAccess("owner4", "/game/1"));
    }

    @Test
    public void validate_successWhenUserIsMemberOfReferencedGame() {
        createGame("Test Game 1", "owner1", "user2", "user3");
        assertTrue(destinationAccessValidator.hasAccess("user2", "/game/0"));
        createGame("Test Game 2", "owner4", "user5", "user6");
        assertTrue(destinationAccessValidator.hasAccess("user6", "/game/1"));
    }

}