package org.luxons.sevenwonders.game.data;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GameDefinitionLoaderTest {

    @Test
    public void successfulLoad() throws Exception {
        GameDefinitionLoader loader = new GameDefinitionLoader();
        GameDefinition gameDefinition = loader.getGameDefinition();
        assertNotNull(gameDefinition);
    }

}
