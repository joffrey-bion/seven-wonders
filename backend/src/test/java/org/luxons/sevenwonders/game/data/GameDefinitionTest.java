package org.luxons.sevenwonders.game.data;

import java.util.List;

import org.junit.Test;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.*;

public class GameDefinitionTest {

    @Test
    public void successfulGameInit() throws Exception {
        GameDefinition gameDefinition = new GameDefinitionLoader().getGameDefinition();
        assertNotNull(gameDefinition);

        List<Player> players = TestUtils.createPlayers(7);
        Game game = gameDefinition.initGame(0, new CustomizableSettings(), players);
        assertNotNull(game);
    }
}