package org.luxons.sevenwonders.game.data;

import org.junit.Test;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.api.CustomizableSettings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GameDefinitionTest {

    @Test
    public void successfulGameInit() {
        GameDefinition gameDefinition = new GameDefinitionLoader().getGameDefinition();
        assertNotNull(gameDefinition);
        assertEquals(3, gameDefinition.getMinPlayers());
        assertEquals(7, gameDefinition.getMaxPlayers());

        Game game = gameDefinition.initGame(0, new CustomizableSettings(), 7);
        assertNotNull(game);
    }
}
