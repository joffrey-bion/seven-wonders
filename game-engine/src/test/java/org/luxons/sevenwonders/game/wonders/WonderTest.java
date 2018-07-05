package org.luxons.sevenwonders.game.wonders;

import org.junit.Test;
import org.luxons.sevenwonders.game.cards.CardBack;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WonderTest {

    @Test
    public void buildLevel_increasesNbBuiltStages() {
        Wonder wonder = TestUtilsKt.testWonder();
        assertEquals(0, wonder.getNbBuiltStages());
        wonder.buildLevel(new CardBack("img"));
        assertEquals(1, wonder.getNbBuiltStages());
        wonder.buildLevel(new CardBack("img"));
        assertEquals(2, wonder.getNbBuiltStages());
        wonder.buildLevel(new CardBack("img"));
        assertEquals(3, wonder.getNbBuiltStages());
    }

    @Test
    public void buildLevel_failsIfFull() {
        Wonder wonder = TestUtilsKt.testWonder();
        wonder.buildLevel(new CardBack("img"));
        wonder.buildLevel(new CardBack("img"));
        wonder.buildLevel(new CardBack("img"));
        try {
            wonder.buildLevel(new CardBack("img"));
            fail();
        } catch (IllegalStateException e) {
            // expected exception because there is no 4th level in this wonder
        }
    }
}
