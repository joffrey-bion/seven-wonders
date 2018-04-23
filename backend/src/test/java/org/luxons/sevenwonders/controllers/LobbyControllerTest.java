package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.actions.ReorderPlayersAction;
import org.luxons.sevenwonders.actions.UpdateSettingsAction;
import org.luxons.sevenwonders.controllers.LobbyController.PlayerIsNotOwnerException;
import org.luxons.sevenwonders.controllers.LobbyController.PlayerNotInLobbyException;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.data.definitions.WonderSidePickMethod;
import org.luxons.sevenwonders.lobby.Lobby;
import org.luxons.sevenwonders.lobby.Player;
import org.luxons.sevenwonders.lobby.State;
import org.luxons.sevenwonders.repositories.LobbyRepository;
import org.luxons.sevenwonders.repositories.PlayerRepository;
import org.luxons.sevenwonders.repositories.PlayerRepository.PlayerNotFoundException;
import org.luxons.sevenwonders.test.TestUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LobbyControllerTest {

    private PlayerRepository playerRepository;

    private LobbyRepository lobbyRepository;

    private LobbyController lobbyController;

    @Before
    public void setUp() {
        playerRepository = new PlayerRepository();
        lobbyRepository = new LobbyRepository();
        SimpMessagingTemplate template = TestUtils.createSimpMessagingTemplate();
        lobbyController = new LobbyController(playerRepository, template);
    }

    @Test(expected = PlayerNotFoundException.class)
    public void leave_failsWhenPlayerDoesNotExist() {
        Principal principal = new TestPrincipal("testuser");
        lobbyController.leave(principal);
    }

    @Test(expected = PlayerNotInLobbyException.class)
    public void leave_failsWhenNotInLobby() {
        playerRepository.createOrUpdate("testuser", "Test User");
        Principal principal = new TestPrincipal("testuser");
        lobbyController.leave(principal);
    }

    @Test
    public void leave_succeedsWhenInALobby_asOwner() {
        Player player = playerRepository.createOrUpdate("testuser", "Test User");
        Lobby lobby = lobbyRepository.create("Test Game", player);

        assertTrue(lobby.getPlayers().contains(player));
        assertSame(lobby, player.getLobby());

        Principal principal = new TestPrincipal("testuser");
        lobbyController.leave(principal);

        assertFalse(lobby.getPlayers().contains(player));
        assertNull(player.getLobby());
    }

    @Test
    public void leave_succeedsWhenInALobby_asPeasant() {
        Player player = playerRepository.createOrUpdate("testuser", "Test User");
        Lobby lobby = lobbyRepository.create("Test Game", player);
        Player player2 = addPlayer(lobby, "testuser2");

        assertTrue(lobby.getPlayers().contains(player2));
        assertSame(lobby, player2.getLobby());

        Principal principal = new TestPrincipal("testuser2");
        lobbyController.leave(principal);

        assertFalse(lobby.getPlayers().contains(player2));
        assertNull(player2.getLobby());
    }

    @Test
    public void reorderPlayers_succeedsForOwner() {
        Player player = playerRepository.createOrUpdate("testuser", "Test User");
        Lobby lobby = lobbyRepository.create("Test Game", player);

        Player player2 = addPlayer(lobby, "testuser2");
        Player player3 = addPlayer(lobby, "testuser3");
        Player player4 = addPlayer(lobby, "testuser4");

        List<Player> players = Arrays.asList(player, player2, player3, player4);
        assertEquals(players, lobby.getPlayers());

        ReorderPlayersAction reorderPlayersAction = new ReorderPlayersAction();
        List<Player> reorderedPlayers = Arrays.asList(player3, player, player2, player4);
        List<String> playerNames = reorderedPlayers.stream().map(Player::getUsername).collect(Collectors.toList());
        reorderPlayersAction.setOrderedPlayers(playerNames);

        Principal principal = new TestPrincipal("testuser");
        lobbyController.reorderPlayers(reorderPlayersAction, principal);

        assertEquals(reorderedPlayers, lobby.getPlayers());
    }

    @Test(expected = PlayerIsNotOwnerException.class)
    public void reorderPlayers_failsForPeasant() {
        Player player = playerRepository.createOrUpdate("testuser", "Test User");
        Lobby lobby = lobbyRepository.create("Test Game", player);

        Player player2 = addPlayer(lobby, "testuser2");
        Player player3 = addPlayer(lobby, "testuser3");

        ReorderPlayersAction reorderPlayersAction = new ReorderPlayersAction();
        List<Player> reorderedPlayers = Arrays.asList(player3, player, player2);
        List<String> playerNames = reorderedPlayers.stream().map(Player::getUsername).collect(Collectors.toList());
        reorderPlayersAction.setOrderedPlayers(playerNames);

        Principal principal = new TestPrincipal("testuser2");
        lobbyController.reorderPlayers(reorderPlayersAction, principal);
    }

    @Test
    public void updateSettings_succeedsForOwner() {
        Player player = playerRepository.createOrUpdate("testuser", "Test User");
        Lobby lobby = lobbyRepository.create("Test Game", player);

        addPlayer(lobby, "testuser2");
        addPlayer(lobby, "testuser3");
        addPlayer(lobby, "testuser4");

        assertEquals(new CustomizableSettings(), lobby.getSettings());

        UpdateSettingsAction updateSettingsAction = new UpdateSettingsAction();
        CustomizableSettings newSettings = new CustomizableSettings();
        newSettings.setInitialGold(12);
        newSettings.setDefaultTradingCost(5);
        newSettings.setWonderSidePickMethod(WonderSidePickMethod.ALL_A);
        newSettings.setTimeLimitInSeconds(5);
        newSettings.setPointsPer3Gold(5);
        newSettings.setDiscardedCardGold(4);
        newSettings.setLostPointsPerDefeat(10);
        newSettings.setWonPointsPerVictoryPerAge(new HashMap<>());
        updateSettingsAction.setSettings(newSettings);

        Principal principal = new TestPrincipal("testuser");
        lobbyController.updateSettings(updateSettingsAction, principal);

        assertEquals(newSettings, lobby.getSettings());
    }

    @Test(expected = PlayerIsNotOwnerException.class)
    public void updateSettings_failsForPeasant() {
        Player player = playerRepository.createOrUpdate("testuser", "Test User");
        Lobby lobby = lobbyRepository.create("Test Game", player);

        addPlayer(lobby, "testuser2");
        addPlayer(lobby, "testuser3");

        UpdateSettingsAction updateSettingsAction = new UpdateSettingsAction();
        updateSettingsAction.setSettings(new CustomizableSettings());

        Principal principal = new TestPrincipal("testuser2");
        lobbyController.updateSettings(updateSettingsAction, principal);
    }

    @Test
    public void startGame_succeedsForOwner() {
        Player player = playerRepository.createOrUpdate("testuser", "Test User");
        Lobby lobby = lobbyRepository.create("Test Game", player);

        addPlayer(lobby, "testuser2");
        addPlayer(lobby, "testuser3");
        addPlayer(lobby, "testuser4");

        Principal principal = new TestPrincipal("testuser");
        lobbyController.startGame(principal);

        assertSame(State.PLAYING, lobby.getState());
    }

    @Test(expected = PlayerIsNotOwnerException.class)
    public void startGame_failsForPeasant() {
        Player player = playerRepository.createOrUpdate("testuser", "Test User");
        Lobby lobby = lobbyRepository.create("Test Game", player);

        addPlayer(lobby, "testuser2");
        addPlayer(lobby, "testuser3");

        Principal principal = new TestPrincipal("testuser2");
        lobbyController.startGame(principal);
    }

    private Player addPlayer(Lobby lobby, String username) {
        Player player = playerRepository.createOrUpdate(username, username);
        lobby.addPlayer(player);

        assertTrue(lobby.getPlayers().contains(player));
        assertSame(lobby, player.getLobby());
        return player;
    }
}
